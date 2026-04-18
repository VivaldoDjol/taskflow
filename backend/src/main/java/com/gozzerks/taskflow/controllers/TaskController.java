package com.gozzerks.taskflow.controllers;

import com.gozzerks.taskflow.domain.dto.ErrorResponse;
import com.gozzerks.taskflow.domain.dto.TaskDTO;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.mappers.TaskMapper;
import com.gozzerks.taskflow.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/task-lists/{task_list_id}/tasks")
@Tag(name = "Tasks", description = "Tasks scoped to a specific task list.")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    @Operation(summary = "List all tasks in a task list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks returned (may be empty)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<TaskDTO> listTasks(@PathVariable("task_list_id") UUID taskListId) {
        return taskService.listTasks(taskListId)
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Create a new task in the given task list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TaskDTO createTask(@PathVariable("task_list_id") UUID taskListId,
                              @Valid @RequestBody TaskDTO taskDTO
    ) {
        Task createdTask = taskService.createTask(
                taskListId,
                taskMapper.fromDTO(taskDTO)
        );
        return taskMapper.toDTO(createdTask);
    }

    @GetMapping(path = "/{task_id}")
    @Operation(summary = "Fetch a single task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task or its list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Optional<TaskDTO> getTask(
            @PathVariable("task_list_id") UUID taskListId,
            @PathVariable("task_id") UUID taskId
    ) {
        return taskService.getTask(taskListId, taskId).map(taskMapper::toDTO);
    }

    @PutMapping(path = "/{task_id}")
    @Operation(summary = "Update an existing task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task or its list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TaskDTO updateTask(
            @PathVariable("task_list_id") UUID taskListId,
            @PathVariable("task_id") UUID taskId,
            @Valid @RequestBody TaskDTO taskDTO
    ) {
        Task updatedTask = taskService.updateTask(
                taskListId,
                taskId,
                taskMapper.fromDTO(taskDTO)
        );
        return taskMapper.toDTO(updatedTask);
    }

    @DeleteMapping(path = "/{task_id}")
    @Operation(summary = "Delete a task from a task list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task or its list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void deleteTask(
            @PathVariable("task_list_id") UUID taskListId,
            @PathVariable("task_id") UUID taskId
    ) {
        taskService.deleteTask(taskListId, taskId);
    }
}
