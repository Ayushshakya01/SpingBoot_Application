package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskmanager.dto.StatusUpdateRequest;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskPriority;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository repository;
    private TaskService service;
    private Task task;

    @BeforeEach void setUp() {
        service = new TaskService(repository);
        task = new Task("Write tests", "Service tests", TaskStatus.TODO, TaskPriority.HIGH, LocalDate.of(2026, 11, 1));
    }

    @Test void createUsesDefaultsWhenOptionalEnumsMissing() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TaskResponse result = service.create(new TaskRequest("  New task  ", null, null, null, null));
        assertEquals("New task", result.title());
        assertEquals(TaskStatus.TODO, result.status());
        assertEquals(TaskPriority.MEDIUM, result.priority());
    }

    @Test void getByIdReturnsTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        assertEquals("Write tests", service.getById(1L).title());
    }

    @Test void getByIdThrowsWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.getById(7L));
    }

    @Test void listWithoutFiltersReturnsEverything() {
        when(repository.findAll()).thenReturn(List.of(task));
        assertEquals(1, service.list(null, null).size());
    }

    @Test void listWithStatusUsesStatusFilter() {
        when(repository.findByStatus(TaskStatus.TODO)).thenReturn(List.of(task));
        assertEquals(1, service.list(TaskStatus.TODO, null).size());
    }

    @Test void listWithPriorityUsesPriorityFilter() {
        when(repository.findByPriority(TaskPriority.HIGH)).thenReturn(List.of(task));
        assertEquals(1, service.list(null, TaskPriority.HIGH).size());
    }

    @Test void listWithBothFiltersUsesAndQuery() {
        when(repository.findByStatusAndPriority(TaskStatus.TODO, TaskPriority.HIGH)).thenReturn(List.of(task));
        assertEquals(1, service.list(TaskStatus.TODO, TaskPriority.HIGH).size());
    }

    @Test void updateChangesAllFields() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);
        TaskRequest request = new TaskRequest("Updated", "New description", TaskStatus.DONE, TaskPriority.LOW, null);
        TaskResponse result = service.update(1L, request);
        assertAll(() -> assertEquals("Updated", result.title()), () -> assertEquals(TaskStatus.DONE, result.status()),
                () -> assertEquals(TaskPriority.LOW, result.priority()), () -> assertNull(result.dueDate()));
    }

    @Test void updateStatusChangesOnlyStatus() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);
        TaskResponse result = service.updateStatus(1L, new StatusUpdateRequest(TaskStatus.IN_PROGRESS));
        assertEquals(TaskStatus.IN_PROGRESS, result.status());
        assertEquals("Write tests", result.title());
    }

    @Test void deleteRemovesExistingTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        service.delete(1L);
        verify(repository).delete(task);
    }

    @Test void deleteMissingTaskThrows() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.delete(9L));
        verify(repository, never()).delete(any(Task.class));
    }
}
