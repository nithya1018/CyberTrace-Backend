package com.cybertrace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatabaseConsolePageController {

    @GetMapping("/")
    public String home() {
        return "forward:/database.html";
    }

    @GetMapping("/database")
    public String databaseConsole() {
        return "forward:/database.html";
    }
}
