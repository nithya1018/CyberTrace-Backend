package com.cybertrace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/admin/database")
@RequiredArgsConstructor
public class DatabaseConsoleController {

    private static final int ROW_LIMIT = 100;
    private static final Pattern PRIVATE_COLUMN = Pattern.compile(
            "(?i).*(password|passwd|token|secret|credential|api.?key|salt|private.?key|storage.?path).*"
    );

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/tables")
    public Map<String, Object> tables() {
        List<Map<String, Object>> tables = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet result = metadata.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (result.next()) {
                    String name = result.getString("TABLE_NAME");
                    List<String> columns = visibleColumns(metadata, connection.getCatalog(), name);
                    long count = countRows(connection, name);
                    tables.add(Map.of("name", name, "columns", columns, "rowCount", count));
                }
            }
            return Map.of("tables", tables, "rowLimit", ROW_LIMIT);
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    @GetMapping("/tables/{tableName}")
    public Map<String, Object> table(@PathVariable String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            String safeName = resolveTableName(connection.getMetaData(), connection.getCatalog(), tableName);
            List<String> columns = visibleColumns(connection.getMetaData(), connection.getCatalog(), safeName);
            if (columns.isEmpty()) {
                return Map.of("name", safeName, "columns", List.of(), "rows", List.of(), "rowLimit", ROW_LIMIT);
            }

            String selectColumns = columns.stream().map(DatabaseConsoleController::quoteIdentifier)
                    .reduce((left, right) -> left + "," + right).orElseThrow();
            String sql = "SELECT " + selectColumns + " FROM " + quoteIdentifier(safeName) + " LIMIT " + ROW_LIMIT;
            List<Map<String, Object>> rows = jdbcTemplate.query(sql, resultSet -> {
                List<Map<String, Object>> values = new ArrayList<>();
                ResultSetMetaData resultMetadata = resultSet.getMetaData();
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int index = 1; index <= resultMetadata.getColumnCount(); index++) {
                        row.put(resultMetadata.getColumnLabel(index), resultSet.getObject(index));
                    }
                    values.add(row);
                }
                return values;
            });
            return Map.of("name", safeName, "columns", columns, "rows", rows, "rowLimit", ROW_LIMIT);
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    private static List<String> visibleColumns(DatabaseMetaData metadata, String catalog, String tableName)
            throws SQLException {
        List<String> columns = new ArrayList<>();
        try (ResultSet result = metadata.getColumns(catalog, null, tableName, "%")) {
            while (result.next()) {
                String name = result.getString("COLUMN_NAME");
                int type = result.getInt("DATA_TYPE");
                if (!PRIVATE_COLUMN.matcher(name).matches() && !isBinary(type)) {
                    columns.add(name);
                }
            }
        }
        return columns;
    }

    private static String resolveTableName(DatabaseMetaData metadata, String catalog, String requested)
            throws SQLException {
        try (ResultSet result = metadata.getTables(catalog, null, "%", new String[]{"TABLE"})) {
            while (result.next()) {
                String name = result.getString("TABLE_NAME");
                if (name.equalsIgnoreCase(requested)) return name;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found");
    }

    private static long countRows(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + quoteIdentifier(tableName);
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getLong(1);
        }
    }

    private static boolean isBinary(int type) {
        return type == Types.BINARY || type == Types.VARBINARY || type == Types.LONGVARBINARY
                || type == Types.BLOB || type == Types.CLOB || type == Types.NCLOB;
    }

    private static String quoteIdentifier(String identifier) {
        return "`" + identifier.replace("`", "``") + "`";
    }

    private static ResponseStatusException databaseError(SQLException ex) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Database inspection is temporarily unavailable", ex);
    }
}
