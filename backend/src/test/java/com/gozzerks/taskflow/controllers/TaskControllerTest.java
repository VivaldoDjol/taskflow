package com.gozzerks.taskflow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozzerks.taskflow.domain.dto.TaskDTO;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import com.gozzerks.taskflow.mappers.TaskMapper;
import com.gozzerks.taskflow.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    private UUID taskListId;
    private UUID taskId;
    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        taskListId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        // Arrange
        TaskList taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setTitle("Test Task List");

        // Arrange
        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.OPEN);
        task.setTaskList(taskList);
        task.setDueDate(LocalDateTime.now().plusDays(7));
        task.setCreated(LocalDateTime.now());
        task.setUpdated(LocalDateTime.now());

        // Arrange
        taskDTO = new TaskDTO(
                taskId,
                "Test Task",
                "Test Description",
                LocalDateTime.now().plusDays(7),
                TaskPriority.HIGH,
                TaskStatus.OPEN
        );
    }

    @Test
    @DisplayName("GET /task-lists/{id}/tasks - Should return all tasks in task list")
    void listTasks_ShouldReturnAllTasks() throws Exception {
        // Arrange
        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Second Task");
        task2.setDescription("Second Description");
        task2.setPriority(TaskPriority.LOW);
        task2.setStatus(TaskStatus.OPEN);

        TaskDTO taskDTO2 = new TaskDTO(
                task2.getId(),
                "Second Task",
                "Second Description",
                null,
                TaskPriority.LOW,
                TaskStatus.OPEN
        );

        List<Task> tasks = Arrays.asList(task, task2);

        when(taskService.listTasks(taskListId)).thenReturn(tasks);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);
        when(taskMapper.toDTO(task2)).thenReturn(taskDTO2);

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(taskId.toString())))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[0].priority", is("HIGH")))
                .andExpect(jsonPath("$[0].status", is("OPEN")))
                .andExpect(jsonPath("$[1].title", is("Second Task")));

        verify(taskService, times(1)).listTasks(taskListId);
        verify(taskMapper, times(2)).toDTO(any(Task.class));
    }

    @Test
    @DisplayName("GET /task-lists/{id}/tasks - Should return empty list when no tasks exist")
    void listTasks_ShouldReturnEmptyList_WhenNoTasksExist() throws Exception {
        // Arrange
        when(taskService.listTasks(taskListId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).listTasks(taskListId);
        verify(taskMapper, never()).toDTO(any(Task.class));
    }

    @Test
    @DisplayName("GET /task-lists/{listId}/tasks/{taskId} - Should return task when both IDs exist")
    void getTask_ShouldReturnTask_WhenBothIdsExist() throws Exception {
        // Arrange
        when(taskService.getTask(taskListId, taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks/{taskId}", taskListId, taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("OPEN")));

        verify(taskService, times(1)).getTask(taskListId, taskId);
        verify(taskMapper, times(1)).toDTO(task);
    }

    @Test
    @DisplayName("GET /task-lists/{listId}/tasks/{taskId} - Should return null when task not found")
    void getTask_ShouldReturnNull_WhenTaskDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentTaskId = UUID.randomUUID();
        when(taskService.getTask(taskListId, nonExistentTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks/{taskId}", taskListId, nonExistentTaskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(taskService, times(1)).getTask(taskListId, nonExistentTaskId);
        verify(taskMapper, never()).toDTO(any(Task.class));
    }

    @Test
    @DisplayName("POST /task-lists/{id}/tasks - Should create task with valid data")
    void createTask_ShouldCreateTask_WithValidData() throws Exception {
        // Arrange
        TaskDTO inputDTO = new TaskDTO(
                null,
                "New Task",
                "New Description",
                null,
                TaskPriority.MEDIUM,
                TaskStatus.OPEN
        );

        when(taskMapper.fromDTO(any(TaskDTO.class))).thenReturn(task);
        when(taskService.createTask(eq(taskListId), any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        // Act & Assert
        mockMvc.perform(post("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("OPEN")));

        verify(taskMapper, times(1)).fromDTO(any(TaskDTO.class));
        verify(taskService, times(1)).createTask(eq(taskListId), any(Task.class));
        verify(taskMapper, times(1)).toDTO(task);
    }

    @Test
    @DisplayName("POST /task-lists/{id}/tasks - Should return 400 when service throws")
    void createTask_ShouldReturn400_WhenServiceThrows() throws Exception {
        // Arrange
        TaskDTO inputDTO = new TaskDTO(
                null,
                null,
                "Description",
                null,
                TaskPriority.MEDIUM,
                TaskStatus.OPEN
        );

        when(taskMapper.fromDTO(any(TaskDTO.class))).thenReturn(new Task());
        when(taskService.createTask(eq(taskListId), any(Task.class)))
                .thenThrow(new IllegalArgumentException("Task must have a title!"));

        // Act & Assert
        mockMvc.perform(post("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("title")));
    }

    @Test
    @DisplayName("PUT /task-lists/{listId}/tasks/{taskId} - Should update task")
    void updateTask_ShouldUpdateTask() throws Exception {
        // Arrange
        TaskDTO updateDTO = new TaskDTO(
                taskId,
                "Updated Task",
                "Updated Description",
                null,
                TaskPriority.LOW,
                TaskStatus.CLOSED
        );

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Updated Task");
        updatedTask.setPriority(TaskPriority.LOW);
        updatedTask.setStatus(TaskStatus.CLOSED);

        TaskDTO updatedTaskDTO = new TaskDTO(
                taskId,
                "Updated Task",
                "Updated Description",
                null,
                TaskPriority.LOW,
                TaskStatus.CLOSED
        );

        when(taskMapper.fromDTO(any(TaskDTO.class))).thenReturn(updatedTask);
        when(taskService.updateTask(eq(taskListId), eq(taskId), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDTO(updatedTask)).thenReturn(updatedTaskDTO);

        // Act & Assert
        mockMvc.perform(put("/task-lists/{taskListId}/tasks/{taskId}", taskListId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.priority", is("LOW")))
                .andExpect(jsonPath("$.status", is("CLOSED")));

        verify(taskService, times(1)).updateTask(eq(taskListId), eq(taskId), any(Task.class));
    }

    @Test
    @DisplayName("DELETE /task-lists/{listId}/tasks/{taskId} - Should delete task")
    void deleteTask_ShouldDeleteTask() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(taskListId, taskId);

        // Act & Assert
        mockMvc.perform(delete("/task-lists/{taskListId}/tasks/{taskId}", taskListId, taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(taskListId, taskId);
    }
}