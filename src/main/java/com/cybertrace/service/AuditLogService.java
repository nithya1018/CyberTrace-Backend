package com.cybertrace.service;

import com.cybertrace.model.AuditLog;
import com.cybertrace.model.User;
import com.cybertrace.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String ipAddress, String details) {
        AuditLog entry = AuditLog.builder()
                .user(user)
                .action(action)
                .ipAddress(ipAddress)
                .details(details)
                .build();
        auditLogRepository.save(entry);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }
}
