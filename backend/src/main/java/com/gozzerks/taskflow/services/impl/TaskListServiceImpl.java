package com.gozzerks.taskflow.services.impl;

import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.User;
import com.gozzerks.taskflow.exceptions.NotFoundException;
import com.gozzerks.taskflow.repositories.TaskListRepository;
import com.gozzerks.taskflow.services.TaskListService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskListServiceImpl implements TaskListService {
    private final TaskListRepository taskListRepository;

    public TaskListServiceImpl(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    @Override
    public List<TaskList> listTaskLists() {
        return taskListRepository.findAllByOwner(currentUser());
    }

    public TaskList createTaskList(TaskList taskList) {
        if (null != taskList.getId()) {
            throw new IllegalArgumentException("Task list already has an ID!");
        }
        if (null == taskList.getTitle() || taskList.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task list title is required!");
        }

        LocalDateTime now = LocalDateTime.now();
        return taskListRepository.save(new TaskList(
                null,
                taskList.getTitle(),
                taskList.getDescription(),
                currentUser(),
                null,
                now,
                now
        ));
    }

    @Override
    public Optional<TaskList> getTaskList(UUID id) {
        return taskListRepository.findByIdAndOwner(id, currentUser());
    }

    @Transactional
    @Override
    public TaskList updateTaskList(UUID taskListId, TaskList taskList) {
        if (null == taskList.getId()) {
            throw new IllegalArgumentException("Task list must have an ID!");
        }

        if (!Objects.equals(taskList.getId(), taskListId)) {
            throw new IllegalArgumentException("Attempting to change task list ID, this is not permitted!");
        }
        TaskList existingTaskList = taskListRepository.findByIdAndOwner(taskListId, currentUser())
                .orElseThrow(() -> new NotFoundException("Task list not found"));

        existingTaskList.setTitle(taskList.getTitle());
        existingTaskList.setDescription(taskList.getDescription());
        existingTaskList.setUpdated(LocalDateTime.now());
        return taskListRepository.save(existingTaskList);
    }

    @Override
    public void deleteTaskList(UUID taskListId) {
        TaskList existingTaskList = taskListRepository.findByIdAndOwner(taskListId, currentUser())
                .orElseThrow(() -> new NotFoundException("Task list not found"));
        taskListRepository.delete(existingTaskList);
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) principal;
    }
}
