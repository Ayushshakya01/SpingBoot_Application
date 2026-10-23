package com.example.taskmanager.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.GlobalExceptionHandler;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.TaskPriority;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;
    private final TaskResponse response = new TaskResponse(1L, "Write tests", "Description", TaskStatus.TODO,
            TaskPriority.HIGH, LocalDate.of(2026, 11, 1), LocalDateTime.now(), LocalDateTime.now());
    private final String request = "{\"title\":\"Write tests\",\"description\":\"Description\",\"status\":\"TODO\",\"priority\":\"HIGH\",\"dueDate\":\"2026-11-01\"}";

    @Test void createReturns201() throws Exception {
        when(service.create(any())).thenReturn(response);
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/tasks/1"))
                .andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.title").value("Write tests"));
    }

    @Test void listReturnsTasksAndPassesFilters() throws Exception {
        mvc.perform(get("/api/tasks")).andExpect(status().isOk()).andExpect(content().string("Hello World"));
    }

    @Test void getReturnsTask() throws Exception {
        when(service.getById(1L)).thenReturn(response);
        mvc.perform(get("/api/tasks/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void updateReturnsTask() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(response);
        mvc.perform(put("/api/tasks/1").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Write tests"));
    }

    @Test void patchStatusReturnsTask() throws Exception {
        when(service.updateStatus(eq(1L), any())).thenReturn(response);
        mvc.perform(patch("/api/tasks/1/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"TODO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test void deleteReturns204() throws Exception {
        doNothing().when(service).delete(1L);
        mvc.perform(delete("/api/tasks/1")).andExpect(status().isNoContent());
    }

    @Test void blankTitleReturnsValidationError() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title: must not be blank")));
    }

    @Test void longTitleReturnsValidationError() throws Exception {
        String longTitle = "x".repeat(101);
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"" + longTitle + "\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title")));
    }

    @Test void missingTaskReturns404Body() throws Exception {
        when(service.getById(99L)).thenThrow(new TaskNotFoundException(99L));
        mvc.perform(get("/api/tasks/99")).andExpect(status().isNotFound()).andExpect(jsonPath("$.path").value("/api/tasks/99"));
    }

    @Test
void listReturnsHelloEvenWithQueryParameters() throws Exception {
    mvc.perform(get("/api/tasks").param("status", "INVALID"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello"));
}

    @Test void malformedJsonAndInvalidBodyEnumReturn400() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Bad Request"));
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Task\",\"status\":\"INVALID\"}"))
                .andExpect(status().isBadRequest());
    }
}
