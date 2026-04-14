package com.gozzerks.taskflow.mappers.impl;

import com.gozzerks.taskflow.domain.dto.TaskDTO;
import com.gozzerks.taskflow.domain.dto.TaskListDTO;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import com.gozzerks.taskflow.mappers.TaskListMapper;
import com.gozzerks.taskflow.mappers.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskListMapperImpl Tests")
class TaskListMapperImplTest {

    @Mock
    private TaskMapper taskMapper;

    private TaskListMapper taskListMapper;

    @BeforeEach
    void setUp() {
        taskListMapper = new TaskListMapperImpl(taskMapper);
    }

    @Nested
    @DisplayName("fromDTO Tests")
    class FromDTOTests {

        @Test
        @DisplayName("Should map DTO to Entity with all fields")
        void shouldMapDTOToEntity_WithAllFields() {
            // Arrange
            UUID taskListId = UUID.randomUUID();
            TaskDTO taskDto = new TaskDTO(
                    UUID.randomUUID(),
                    "Sample Task",
                    "Sample Description",
                    LocalDateTime.now(),
                    TaskPriority.HIGH,
                    TaskStatus.OPEN
            );
            
            Task mappedTask = new Task();
            mappedTask.setId(taskDto.id());
            mappedTask.setTitle(taskDto.title());
            mappedTask.setDescription(taskDto.description());
            mappedTask.setDueDate(taskDto.dueDate());
            mappedTask.setPriority(taskDto.priority());
            mappedTask.setStatus(taskDto.status());

            TaskListDTO taskListDto = new TaskListDTO(
                    taskListId,
                    "Test Task List",
                    "Test Description",
                    1,
                    0.0,
                    List.of(taskDto)
            );

            when(taskMapper.fromDTO(any(TaskDTO.class))).thenReturn(mappedTask);

            // Act
            TaskList taskList = taskListMapper.fromDTO(taskListDto);

            // Assert
            assertThat(taskList).isNotNull();
            assertThat(taskList.getId()).isEqualTo(taskListId);
            assertThat(taskList.getTitle()).isEqualTo("Test Task List");
            assertThat(taskList.getDescription()).isEqualTo("Test Description");
            assertThat(taskList.getTasks()).hasSize(1);
            assertThat(taskList.getTasks().get(0).getId()).isEqualTo(taskDto.id());
        }

        @Test
        @DisplayName("Should handle DTO with null tasks list")
        void shouldHandleNullTasksList() {
            // Arrange
            UUID taskListId = UUID.randomUUID();
            TaskListDTO taskListDto = new TaskListDTO(
                    taskListId,
                    "Test Task List",
                    "Test Description",
                    0,
                    0.0,
                    null
            );

            // Act
            TaskList taskList = taskListMapper.fromDTO(taskListDto);

            // Assert
            assertThat(taskList).isNotNull();
            assertThat(taskList.getId()).isEqualTo(taskListId);
            assertThat(taskList.getTitle()).isEqualTo("Test Task List");
            assertThat(taskList.getDescription()).isEqualTo("Test Description");
            assertThat(taskList.getTasks()).isNull();
        }

        @Test
        @DisplayName("Should handle DTO with empty tasks list")
        void shouldHandleEmptyTasksList() {
            // Arrange
            UUID taskListId = UUID.randomUUID();
            TaskListDTO taskListDto = new TaskListDTO(
                    taskListId,
                    "Test Task List",
                    "Test Description",
                    0,
                    0.0,
                    List.of()
            );

            // Act
            TaskList taskList = taskListMapper.fromDTO(taskListDto);

            // Assert
            assertThat(taskList).isNotNull();
            assertThat(taskList.getId()).isEqualTo(taskListId);
            assertThat(taskList.getTitle()).isEqualTo("Test Task List");
            assertThat(taskList.getDescription()).isEqualTo("Test Description");
            assertThat(taskList.getTasks()).isNotNull();
            assertThat(taskList.getTasks()).isEmpty();
        }

        @Test
        @DisplayName("Should handle DTO with null title")
        void shouldHandleNullTitle() {
            // Arrange
            UUID taskListId = UUID.randomUUID();
            TaskListDTO taskListDto = new TaskListDTO(
                    taskListId,
                    null,
                    "Test Description",
                    0,
                    0.0,
                    null
            );

            // Act
            TaskList taskList = taskListMapper.fromDTO(taskListDto);

            // Assert
            assertThat(taskList).isNotNull();
            assertThat(taskList.getId()).isEqualTo(taskListId);
            assertThat(taskList.getTitle()).isNull();
            assertThat(taskList.getDescription()).isEqualTo("Test Description");
        }
    }

    @Nested
    @DisplayName("toDTO Tests")
    class ToDTOTests {

        @Test
        @DisplayName("Should map Entity to DTO with all fields")
        void shouldMapEntityToDTO_WithAllFields() {
            // Arrange
            UUID taskListId = UUID.randomUUID();
            
            Task task1 = new Task();
            task1.setId(UUID.randomUUID());
            task1.setTitle("Task 1");
            task1.setStatus(TaskStatus.OPEN);
            
            Task task2 = new Task();
            task2.setId(UUID.randomUUID());
            task2.setTitle("Task 2");
            task2.setStatus(TaskStatus.CLOSED);

            TaskList taskList = new TaskList();
            taskList.setId(taskListId);
            taskList.setTitle("Test Task List");
            taskList.setDescription("Test Description");
            taskList.setTasks(Arrays.asList(task1, task2));

            TaskDTO mappedTask1 = new TaskDTO(
                    task1.getId(),
                    task1.getTitle(),
                    task1.getDescription(),
                    task1.getDueDate(),
                    task1.getPriority(),
                    task1.getStatus()
            );
            
            TaskDTO mappedTask2 = new TaskDTO(
                    task2.getId(),
                    task2.getTitle(),
                    task2.getDescription(),
                    task2.getDueDate(),
                    task2.getPriority(),
                    task2.getStatus()
            );

            when(taskMapper.toDTO(task1)).thenReturn(mappedTask1);
            when(taskMapper.toDTO(task2)).thenReturn(mappedTask2);

            // Act
            TaskListDTO taskListDto = taskListMapper.toDTO(taskList);

            // Assert
            assertThat(taskListDto).isNotNull();
            assertThat(taskListDto.id()).isEqualTo(taskListId);
            assertThat(taskListDto.title()).isEqualTo("Test Task List");
            assertThat(taskListDto.description()).isEqualTo("Test Description");
            assertThat(taskListDto.count()).isEqualTo(2);
            assertThat(taskListDto.progress()).isEqualTo(0.5);
            assertThat(taskListDto.tasks()).hasSize(2);
        }

        @Test
        @DisplayName("Should calculate progress correctly with all open tasks")
        void shouldCalculateProgressWithAllOpenTasks() {
            // Arrange
            Task openTask1 = new Task();
            openTask1.setId(UUID.randomUUID());
            openTask1.setStatus(TaskStatus.OPEN);
            
            Task openTask2 = new Task();
            openTask2.setId(UUID.randomUUID());
            openTask2.setStatus(TaskStatus.OPEN);

            TaskList taskList = new TaskList();
            taskList.setTasks(Arrays.asList(openTask1, openTask2));

            when(taskMapper.toDTO(any(Task.class))).thenReturn(new TaskDTO(
                    UUID.randomUUID(), "Title", "Desc", null, TaskPriority.MEDIUM, TaskStatus.OPEN));

            // Act
            TaskListDTO taskListDto = taskListMapper.toDTO(taskList);

            // Assert
            assertThat(taskListDto.progress()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should calculate progress correctly with all closed tasks")
        void shouldCalculateProgressWithAllClosedTasks() {
            // Arrange
            Task closedTask1 = new Task();
            closedTask1.setId(UUID.randomUUID());
            closedTask1.setStatus(TaskStatus.CLOSED);
            
            Task closedTask2 = new Task();
            closedTask2.setId(UUID.randomUUID());
            closedTask2.setStatus(TaskStatus.CLOSED);

            TaskList taskList = new TaskList();
            taskList.setTasks(Arrays.asList(closedTask1, closedTask2));

            when(taskMapper.toDTO(any(Task.class))).thenReturn(new TaskDTO(
                    UUID.randomUUID(), "Title", "Desc", null, TaskPriority.MEDIUM, TaskStatus.OPEN));

            // Act
            TaskListDTO taskListDto = taskListMapper.toDTO(taskList);

            // Assert
            assertThat(taskListDto.progress()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("Should handle null tasks in entity")
        void shouldHandleNullTasksInEntity() {
            // Arrange
            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());
            taskList.setTitle("Test Task List");
            taskList.setDescription("Test Description");
            taskList.setTasks(null);

            // Act
            TaskListDTO taskListDto = taskListMapper.toDTO(taskList);

            // Assert
            assertThat(taskListDto).isNotNull();
            assertThat(taskListDto.count()).isEqualTo(0);
            assertThat(taskListDto.progress()).isNull();
            assertThat(taskListDto.tasks()).isNull();
        }

        @Test
        @DisplayName("Should handle empty tasks in entity")
        void shouldHandleEmptyTasksInEntity() {
            // Arrange
            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());
            taskList.setTitle("Test Task List");
            taskList.setDescription("Test Description");
            taskList.setTasks(List.of());

            // Act
            TaskListDTO taskListDto = taskListMapper.toDTO(taskList);

            // Assert
            assertThat(taskListDto).isNotNull();
            assertThat(taskListDto.count()).isEqualTo(0);
            assertThat(taskListDto.progress()).isNaN();
            assertThat(taskListDto.tasks()).isNotNull();
            assertThat(taskListDto.tasks()).isEmpty();
        }

        @Test
        @DisplayName("Should handle entity with null title")
        void shouldHandleEntityWithNullTitle() {
            // Arrange
            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());
            taskList.setTitle(null);
            taskList.setDescription("Test Description");

            // Act
            TaskListDTO taskListDto = taskListMapper.toDTO(taskList);

            // Assert
            assertThat(taskListDto).isNotNull();
            assertThat(taskListDto.title()).isNull();
            assertThat(taskListDto.description()).isEqualTo("Test Description");
        }
    }
}
