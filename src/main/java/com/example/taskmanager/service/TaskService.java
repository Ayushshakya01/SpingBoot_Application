package com.example.taskmanager.service;

import com.example.taskmanager.dto.StatusUpdateRequest;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskPriority;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { this.repository = repository; }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task(request.title().trim(), request.description(),
                request.status() == null ? TaskStatus.TODO : request.status(),
                request.priority() == null ? TaskPriority.MEDIUM : request.priority(), request.dueDate());
        return TaskResponse.from(repository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) { return TaskResponse.from(requireTask(id)); }

    @Transactional(readOnly = true)
    public List<TaskResponse> list(TaskStatus status, TaskPriority priority) {
        List<Task> tasks;
        if (status != null && priority != null) tasks = repository.findByStatusAndPriority(status, priority);
        else if (status != null) tasks = repository.findByStatus(status);
        else if (priority != null) tasks = repository.findByPriority(priority);
        else tasks = repository.findAll();
        return tasks.stream().map(TaskResponse::from).toList();
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = requireTask(id);
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
        task.setPriority(request.priority() == null ? TaskPriority.MEDIUM : request.priority());
        task.setDueDate(request.dueDate());
        return TaskResponse.from(repository.save(task));
    }

    public TaskResponse updateStatus(Long id, StatusUpdateRequest request) {
        Task task = requireTask(id);
        task.setStatus(request.status());
        return TaskResponse.from(repository.save(task));
    }

    public void delete(Long id) { repository.delete(requireTask(id)); }

    private Task requireTask(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
