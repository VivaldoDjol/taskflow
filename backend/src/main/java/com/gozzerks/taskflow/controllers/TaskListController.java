package com.gozzerks.taskflow.controllers;

import com.gozzerks.taskflow.domain.dto.TaskListDTO;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.mappers.TaskListMapper;
import com.gozzerks.taskflow.services.TaskListService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/task-lists")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    public TaskListController(TaskListService taskListService, TaskListMapper taskListMapper) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
    }

    @GetMapping
    public List<TaskListDTO> listTaskLists() {
        return taskListService.listTaskLists()
                .stream()
                .map(taskListMapper::toDTO)
                .toList();
    }

    @PostMapping
    public TaskListDTO createTaskList(@Valid @RequestBody TaskListDTO taskListDTO) {

        TaskList createdTaskList = taskListService.createTaskList(
                taskListMapper.fromDTO(taskListDTO)

        );
        return taskListMapper.toDTO(createdTaskList);
    }

    @GetMapping(path = "/{task_list_id}")
    public Optional<TaskListDTO> getTaskList(@PathVariable("task_list_id") UUID taskListId) {
        return taskListService.getTaskList(taskListId)
                .map(taskListMapper::toDTO);
    }

    @PutMapping(path = "/{task_list_id}")
    public TaskListDTO updateTaskList(
            @PathVariable("task_list_id") UUID taskListId,
            @Valid @RequestBody TaskListDTO taskListDTO
    ) {
        TaskList updatedTaskList = taskListService.updateTaskList(
                taskListId,
                taskListMapper.fromDTO(taskListDTO)
        );
        return taskListMapper.toDTO(updatedTaskList);
    }

    @DeleteMapping(path = "/{task_list_id}")
    public void deleteTaskList(@PathVariable("task_list_id") UUID taskListId) {
        taskListService.deleteTaskList(taskListId);
    }
}