package com.gozzerks.taskflow.controllers;

import com.gozzerks.taskflow.domain.dto.ErrorResponse;
import com.gozzerks.taskflow.domain.dto.TaskListDTO;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.mappers.TaskListMapper;
import com.gozzerks.taskflow.services.TaskListService;
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
@RequestMapping(path = "/task-lists")
@Tag(name = "Task Lists", description = "Task lists owned by the authenticated user.")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    public TaskListController(TaskListService taskListService, TaskListMapper taskListMapper) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
    }

    @GetMapping
    @Operation(summary = "List all task lists owned by the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task lists returned (may be empty)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<TaskListDTO> listTaskLists() {
        return taskListService.listTaskLists()
                .stream()
                .map(taskListMapper::toDTO)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Create a new task list owned by the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TaskListDTO createTaskList(@Valid @RequestBody TaskListDTO taskListDTO) {

        TaskList createdTaskList = taskListService.createTaskList(
                taskListMapper.fromDTO(taskListDTO)

        );
        return taskListMapper.toDTO(createdTaskList);
    }

    @GetMapping(path = "/{task_list_id}")
    @Operation(summary = "Fetch a single task list by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Optional<TaskListDTO> getTaskList(@PathVariable("task_list_id") UUID taskListId) {
        return taskListService.getTaskList(taskListId)
                .map(taskListMapper::toDTO);
    }

    @PutMapping(path = "/{task_list_id}")
    @Operation(summary = "Update an existing task list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "Delete a task list and its tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task list does not exist or is owned by another user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void deleteTaskList(@PathVariable("task_list_id") UUID taskListId) {
        taskListService.deleteTaskList(taskListId);
    }
}
