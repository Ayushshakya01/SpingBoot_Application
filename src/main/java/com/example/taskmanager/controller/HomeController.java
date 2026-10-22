package com.example.taskmanager.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "application", "Task Manager API",
                "tasks", "/api/tasks",
                "health", "/actuator/health");
    }
}
