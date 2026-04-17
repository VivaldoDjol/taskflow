package com.gozzerks.taskflow.services.impl;

import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import com.gozzerks.taskflow.domain.entities.User;
import com.gozzerks.taskflow.exceptions.NotFoundException;
import com.gozzerks.taskflow.repositories.TaskListRepository;
import com.gozzerks.taskflow.repositories.TaskRepository;
import com.gozzerks.taskflow.services.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskListRepository taskListRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
    }

    @Override
    public List<Task> listTasks(UUID taskListId) {
        requireOwnedTaskList(taskListId);
        return taskRepository.findByTaskListId(taskListId);
    }

    @Transactional
    @Override
    public Task createTask(UUID taskListId, Task task) {
        if (null != task.getId()) {
            throw new IllegalArgumentException("Task already has an ID!");
        }
        if (null == task.getTitle() || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task must have a title!");
        }

        TaskPriority taskPriority = Optional.ofNullable(task.getPriority())
                .orElse(TaskPriority.MEDIUM);

        TaskStatus taskStatus = TaskStatus.OPEN;

        TaskList taskList = requireOwnedTaskList(taskListId);

        LocalDateTime now = LocalDateTime.now();

        Task taskToSave = new Task(
                null,
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                taskStatus,
                taskPriority,
                taskList,
                now,
                now
        );

        return taskRepository.save(taskToSave);
    }

    @Override
    public Optional<Task> getTask(UUID taskListId, UUID taskId) {
        requireOwnedTaskList(taskListId);
        return taskRepository.findByTaskListIdAndId(taskListId, taskId);
    }

    @Transactional
    @Override
    public Task updateTask(UUID taskListId, UUID taskId, Task task) {
        if (null == task.getId()) {
            throw new IllegalArgumentException("Task must have an ID!");
        }
        if (!Objects.equals(taskId, task.getId())) {
            throw new IllegalArgumentException("Task ID does not match!");
        }
        if (null == task.getPriority()) {
            throw new IllegalArgumentException("Task must have a valid priority!");
        }
        if (null == task.getStatus()) {
            throw new IllegalArgumentException("Task must have a valid status!");
        }

        requireOwnedTaskList(taskListId);

        Task existingTask = taskRepository.findByTaskListIdAndId(taskListId, taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setUpdated(LocalDateTime.now());

        return taskRepository.save(existingTask);
    }

    @Transactional
    @Override
    public void deleteTask(UUID taskListId, UUID taskId) {
        requireOwnedTaskList(taskListId);
        taskRepository.deleteByTaskListIdAndId(taskListId, taskId);
    }

    private TaskList requireOwnedTaskList(UUID taskListId) {
        return taskListRepository.findByIdAndOwner(taskListId, currentUser())
                .orElseThrow(() -> new NotFoundException("Task list not found"));
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) principal;
    }
}
