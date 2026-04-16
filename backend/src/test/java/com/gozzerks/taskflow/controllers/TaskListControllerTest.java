package com.gozzerks.taskflow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozzerks.taskflow.domain.dto.TaskListDTO;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.mappers.TaskListMapper;
import com.gozzerks.taskflow.services.TaskListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(TaskListController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TaskListController Tests")
class TaskListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskListService taskListService;

    @MockBean
    private TaskListMapper taskListMapper;

    private UUID taskListId;
    private TaskList taskList;
    private TaskListDTO taskListDTO;

    @BeforeEach
    void setUp() {
        taskListId = UUID.randomUUID();

        // Arrange
        taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setTitle("Test Task List");
        taskList.setDescription("Test Description");

        // Arrange
        taskListDTO = new TaskListDTO(
                taskListId,
                "Test Task List",
                "Test Description",
                0,
                0.0,
                List.of()
        );
    }

    @Test
    @DisplayName("GET /task-lists - Should return list of all task lists")
    void listTaskLists_ShouldReturnAllTaskLists() throws Exception {
        // Arrange
        TaskList taskList2 = new TaskList();
        taskList2.setId(UUID.randomUUID());
        taskList2.setTitle("Second List");
        taskList2.setDescription("Second Description");

        TaskListDTO taskListDTO2 = new TaskListDTO(
                taskList2.getId(),
                "Second List",
                "Second Description",
                0,
                0.0,
                List.of()
        );

        List<TaskList> taskLists = Arrays.asList(taskList, taskList2);

        when(taskListService.listTaskLists()).thenReturn(taskLists);
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDTO);
        when(taskListMapper.toDTO(taskList2)).thenReturn(taskListDTO2);

        // Act & Assert
        mockMvc.perform(get("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(taskListId.toString())))
                .andExpect(jsonPath("$[0].title", is("Test Task List")))
                .andExpect(jsonPath("$[0].description", is("Test Description")))
                .andExpect(jsonPath("$[1].title", is("Second List")));

        // Verify
        verify(taskListService, times(1)).listTaskLists();
        verify(taskListMapper, times(2)).toDTO(any(TaskList.class));
    }

    @Test
    @DisplayName("GET /task-lists - Should return empty list when no task lists exist")
    void listTaskLists_ShouldReturnEmptyList_WhenNoTaskListsExist() throws Exception {
        // Arrange
        when(taskListService.listTaskLists()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskListService, times(1)).listTaskLists();
        verify(taskListMapper, never()).toDTO(any(TaskList.class));
    }

    @Test
    @DisplayName("GET /task-lists/{id} - Should return task list when ID exists")
    void getTaskList_ShouldReturnTaskList_WhenIdExists() throws Exception {
        // Arrange
        when(taskListService.getTaskList(taskListId)).thenReturn(Optional.of(taskList));
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDTO);

        // Act & Assert
        mockMvc.perform(get("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskListId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task List")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.progress", is(0.0)));

        verify(taskListService, times(1)).getTaskList(taskListId);
        verify(taskListMapper, times(1)).toDTO(taskList);
    }

    @Test
    @DisplayName("GET /task-lists/{id} - Should return null when ID does not exist")
    void getTaskList_ShouldReturnNull_WhenIdDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskListService.getTaskList(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/task-lists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(taskListService, times(1)).getTaskList(nonExistentId);
        verify(taskListMapper, never()).toDTO(any(TaskList.class));
    }

    @Test
    @DisplayName("POST /task-lists - Should create task list with valid data")
    void createTaskList_ShouldCreateTaskList_WithValidData() throws Exception {
        // Arrange
        TaskListDTO inputDTO = new TaskListDTO(
                null,
                "New Task List",
                "New Description",
                null,
                null,
                null
        );

        when(taskListMapper.fromDTO(any(TaskListDTO.class))).thenReturn(taskList);
        when(taskListService.createTaskList(any(TaskList.class))).thenReturn(taskList);
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDTO);

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskListId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task List")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(taskListMapper, times(1)).fromDTO(any(TaskListDTO.class));
        verify(taskListService, times(1)).createTaskList(any(TaskList.class));
        verify(taskListMapper, times(1)).toDTO(taskList);
    }

    @Test
    @DisplayName("POST /task-lists - Should return 400 when service throws IllegalArgumentException")
    void createTaskList_ShouldReturn400_WhenServiceThrows() throws Exception {
        // Arrange
        TaskListDTO inputDTO = new TaskListDTO(
                null,
                "Valid title",
                "Description only",
                null,
                null,
                null
        );

        when(taskListMapper.fromDTO(any(TaskListDTO.class))).thenReturn(new TaskList());
        when(taskListService.createTaskList(any(TaskList.class)))
                .thenThrow(new IllegalArgumentException("Task list title is required!"));

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("title is required")));
    }

    @Test
    @DisplayName("PUT /task-lists/{id} - Should update task list with valid data")
    void updateTaskList_ShouldUpdateTaskList_WithValidData() throws Exception {
        // Arrange
        TaskList updatedTaskList = new TaskList();
        updatedTaskList.setId(taskListId);
        updatedTaskList.setTitle("Updated Task List");
        updatedTaskList.setDescription("Updated Description");

        TaskListDTO updatedDTO = new TaskListDTO(
                taskListId,
                "Updated Task List",
                "Updated Description",
                0,
                0.0,
                List.of()
        );

        when(taskListMapper.fromDTO(any(TaskListDTO.class))).thenReturn(updatedTaskList);
        when(taskListService.updateTaskList(eq(taskListId), any(TaskList.class))).thenReturn(updatedTaskList);
        when(taskListMapper.toDTO(updatedTaskList)).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskListId.toString())))
                .andExpect(jsonPath("$.title", is("Updated Task List")))
                .andExpect(jsonPath("$.description", is("Updated Description")));

        verify(taskListMapper, times(1)).fromDTO(any(TaskListDTO.class));
        verify(taskListService, times(1)).updateTaskList(eq(taskListId), any(TaskList.class));
        verify(taskListMapper, times(1)).toDTO(updatedTaskList);
    }

    @Test
    @DisplayName("PUT /task-lists/{id} - Should return 400 when task list not found")
    void updateTaskList_ShouldReturn400_WhenNotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        TaskListDTO updateDTO = new TaskListDTO(
                nonExistentId,
                "Updated",
                "Updated",
                null,
                null,
                null
        );

        when(taskListMapper.fromDTO(any(TaskListDTO.class))).thenReturn(new TaskList());
        when(taskListService.updateTaskList(eq(nonExistentId), any(TaskList.class)))
                .thenThrow(new IllegalArgumentException("Task list not found!"));

        // Act & Assert
        mockMvc.perform(put("/task-lists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Task list not found")));
    }

    @Test
    @DisplayName("DELETE /task-lists/{id} - Should delete task list")
    void deleteTaskList_ShouldDeleteTaskList() throws Exception {
        // Arrange
        doNothing().when(taskListService).deleteTaskList(taskListId);

        // Act & Assert
        mockMvc.perform(delete("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(taskListService, times(1)).deleteTaskList(taskListId);
    }

    @Test
    @DisplayName("Should handle malformed JSON in request body")
    void shouldHandleMalformedJson() throws Exception {
        // Arrange
        String malformedJson = "{title: 'Missing quotes', description: }";

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).createTaskList(any(TaskList.class));
    }
}
