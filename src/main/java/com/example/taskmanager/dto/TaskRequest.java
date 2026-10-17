package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskPriority;
import com.example.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "must not be blank") @Size(max = 100, message = "size must be between 0 and 100") String title,
        @Size(max = 500, message = "size must be between 0 and 500") String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate) { }
