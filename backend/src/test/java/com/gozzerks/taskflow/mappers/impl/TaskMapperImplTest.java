package com.gozzerks.taskflow.mappers.impl;

import com.gozzerks.taskflow.domain.dto.TaskDTO;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import com.gozzerks.taskflow.mappers.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskMapperImpl Tests")
class TaskMapperImplTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Nested
    @DisplayName("toDTO Tests")
    class ToDTOTests {

        @Test
        @DisplayName("Should map Entity to DTO with all fields")
        void shouldMapEntityToDTO_WithAllFields() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            LocalDateTime dueDate = LocalDateTime.now().plusDays(7);

            Task task = new Task();
            task.setId(taskId);
            task.setTitle("Sample Task");
            task.setDescription("Sample Description");
            task.setDueDate(dueDate);
            task.setPriority(TaskPriority.HIGH);
            task.setStatus(TaskStatus.OPEN);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDTO).isNotNull();
            assertThat(taskDTO.id()).isEqualTo(taskId);
            assertThat(taskDTO.title()).isEqualTo("Sample Task");
            assertThat(taskDTO.description()).isEqualTo("Sample Description");
            assertThat(taskDTO.dueDate()).isEqualTo(dueDate);
            assertThat(taskDTO.priority()).isEqualTo(TaskPriority.HIGH);
            assertThat(taskDTO.status()).isEqualTo(TaskStatus.OPEN);
        }

        @Test
        @DisplayName("Should handle Entity with null description")
        void shouldHandleNullDescription() {
            // Arrange
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setTitle("Task Without Description");
            task.setDescription(null);
            task.setPriority(TaskPriority.MEDIUM);
            task.setStatus(TaskStatus.OPEN);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDTO).isNotNull();
            assertThat(taskDTO.title()).isEqualTo("Task Without Description");
            assertThat(taskDTO.description()).isNull();
        }

        @Test
        @DisplayName("Should handle Entity with null due date")
        void shouldHandleNullDueDate() {
            // Arrange
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setTitle("Task Without Due Date");
            task.setDescription("Description");
            task.setDueDate(null);
            task.setPriority(TaskPriority.LOW);
            task.setStatus(TaskStatus.IN_PROGRESS);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDTO).isNotNull();
            assertThat(taskDTO.dueDate()).isNull();
        }

        @Test
        @DisplayName("Should map different priority levels correctly")
        void shouldMapDifferentPriorityLevels() {
            // Arrange
            Task lowPriorityTask = new Task();
            lowPriorityTask.setId(UUID.randomUUID());
            lowPriorityTask.setTitle("Low Priority");
            lowPriorityTask.setPriority(TaskPriority.LOW);
            lowPriorityTask.setStatus(TaskStatus.OPEN);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(lowPriorityTask);

            // Assert
            assertThat(taskDTO).isNotNull();
            assertThat(taskDTO.priority()).isEqualTo(TaskPriority.LOW);
        }

        @Test
        @DisplayName("Should map different status values correctly")
        void shouldMapDifferentStatusValues() {
            // Arrange
            Task closedTask = new Task();
            closedTask.setId(UUID.randomUUID());
            closedTask.setTitle("Closed Task");
            closedTask.setPriority(TaskPriority.MEDIUM);
            closedTask.setStatus(TaskStatus.CLOSED);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(closedTask);

            // Assert
            assertThat(taskDTO).isNotNull();
            assertThat(taskDTO.status()).isEqualTo(TaskStatus.CLOSED);
        }

        @Test
        @DisplayName("Should handle all status types")
        void shouldHandleAllStatusTypes() {
            // Act & Assert for OPEN
            Task openTask = new Task();
            openTask.setId(UUID.randomUUID());
            openTask.setTitle("Open Task");
            openTask.setStatus(TaskStatus.OPEN);
            openTask.setPriority(TaskPriority.MEDIUM);
            assertThat(taskMapper.toDTO(openTask).status()).isEqualTo(TaskStatus.OPEN);

            // Act & Assert for IN_PROGRESS
            Task inProgressTask = new Task();
            inProgressTask.setId(UUID.randomUUID());
            inProgressTask.setTitle("In Progress Task");
            inProgressTask.setStatus(TaskStatus.IN_PROGRESS);
            inProgressTask.setPriority(TaskPriority.MEDIUM);
            assertThat(taskMapper.toDTO(inProgressTask).status()).isEqualTo(TaskStatus.IN_PROGRESS);

            // Act & Assert for CLOSED
            Task closedTask = new Task();
            closedTask.setId(UUID.randomUUID());
            closedTask.setTitle("Closed Task");
            closedTask.setStatus(TaskStatus.CLOSED);
            closedTask.setPriority(TaskPriority.MEDIUM);
            assertThat(taskMapper.toDTO(closedTask).status()).isEqualTo(TaskStatus.CLOSED);
        }

        @Test
        @DisplayName("Should handle all priority types")
        void shouldHandleAllPriorityTypes() {
            // Act & Assert for LOW
            Task lowTask = new Task();
            lowTask.setId(UUID.randomUUID());
            lowTask.setTitle("Low Priority");
            lowTask.setPriority(TaskPriority.LOW);
            lowTask.setStatus(TaskStatus.OPEN);
            assertThat(taskMapper.toDTO(lowTask).priority()).isEqualTo(TaskPriority.LOW);

            // Act & Assert for MEDIUM
            Task mediumTask = new Task();
            mediumTask.setId(UUID.randomUUID());
            mediumTask.setTitle("Medium Priority");
            mediumTask.setPriority(TaskPriority.MEDIUM);
            mediumTask.setStatus(TaskStatus.OPEN);
            assertThat(taskMapper.toDTO(mediumTask).priority()).isEqualTo(TaskPriority.MEDIUM);

            // Act & Assert for HIGH
            Task highTask = new Task();
            highTask.setId(UUID.randomUUID());
            highTask.setTitle("High Priority");
            highTask.setPriority(TaskPriority.HIGH);
            highTask.setStatus(TaskStatus.OPEN);
            assertThat(taskMapper.toDTO(highTask).priority()).isEqualTo(TaskPriority.HIGH);
        }

        @Test
        @DisplayName("Should preserve all entity field values in DTO")
        void shouldPreserveAllFieldValues() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            LocalDateTime dueDate = LocalDateTime.now().plusDays(10);

            Task task = new Task();
            task.setId(taskId);
            task.setTitle("Complete Task");
            task.setDescription("Full Description");
            task.setDueDate(dueDate);
            task.setPriority(TaskPriority.HIGH);
            task.setStatus(TaskStatus.IN_PROGRESS);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDTO.id()).isEqualTo(task.getId());
            assertThat(taskDTO.title()).isEqualTo(task.getTitle());
            assertThat(taskDTO.description()).isEqualTo(task.getDescription());
            assertThat(taskDTO.dueDate()).isEqualTo(task.getDueDate());
            assertThat(taskDTO.priority()).isEqualTo(task.getPriority());
            assertThat(taskDTO.status()).isEqualTo(task.getStatus());
        }

        @Test
        @DisplayName("Should handle entity with minimum required fields")
        void shouldHandleMinimumRequiredFields() {
            // Arrange
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setTitle("Minimal Task");
            task.setPriority(TaskPriority.LOW);
            task.setStatus(TaskStatus.OPEN);

            // Act
            TaskDTO taskDTO = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDTO).isNotNull();
            assertThat(taskDTO.id()).isNotNull();
            assertThat(taskDTO.title()).isEqualTo("Minimal Task");
            assertThat(taskDTO.description()).isNull();
            assertThat(taskDTO.dueDate()).isNull();
        }
    }

    @Nested
    @DisplayName("fromDTO Tests")
    class FromDTOTests {

        @Test
        @DisplayName("Should map DTO to Entity with all fields")
        void shouldMapDTOToEntity_WithAllFields() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            LocalDateTime dueDate = LocalDateTime.now().plusDays(5);

            TaskDTO taskDTO = new TaskDTO(
                    taskId,
                    "DTO Task",
                    "DTO Description",
                    dueDate,
                    TaskPriority.HIGH,
                    TaskStatus.IN_PROGRESS
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getId()).isEqualTo(taskId);
            assertThat(task.getTitle()).isEqualTo("DTO Task");
            assertThat(task.getDescription()).isEqualTo("DTO Description");
            assertThat(task.getDueDate()).isEqualTo(dueDate);
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(task.getTaskList()).isNull();
        }

        @Test
        @DisplayName("Should handle DTO with null description")
        void shouldHandleNullDescription() {
            // Arrange
            TaskDTO taskDTO = new TaskDTO(
                    UUID.randomUUID(),
                    "Task Title",
                    null,
                    LocalDateTime.now(),
                    TaskPriority.LOW,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Task Title");
            assertThat(task.getDescription()).isNull();
        }

        @Test
        @DisplayName("Should handle DTO with null due date")
        void shouldHandleNullDueDate() {
            // Arrange
            TaskDTO taskDTO = new TaskDTO(
                    UUID.randomUUID(),
                    "Task Title",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getDueDate()).isNull();
        }

        @Test
        @DisplayName("Should map all priority levels correctly")
        void shouldMapAllPriorityLevels() {
            // Arrange
            TaskDTO highPriorityDto = new TaskDTO(
                    UUID.randomUUID(),
                    "High Priority",
                    "Description",
                    null,
                    TaskPriority.HIGH,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromDTO(highPriorityDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        }

        @Test
        @DisplayName("Should map all status values correctly")
        void shouldMapAllStatusValues() {
            // Arrange
            TaskDTO inProgressDto = new TaskDTO(
                    UUID.randomUUID(),
                    "In Progress Task",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.IN_PROGRESS
            );

            // Act
            Task task = taskMapper.fromDTO(inProgressDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should handle minimal DTO with only required fields")
        void shouldHandleMinimalDTO() {
            // Arrange
            TaskDTO minimalDto = new TaskDTO(
                    UUID.randomUUID(),
                    "Minimal Task",
                    null,
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromDTO(minimalDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Minimal Task");
            assertThat(task.getDescription()).isNull();
            assertThat(task.getDueDate()).isNull();
            assertThat(task.getTaskList()).isNull();
        }

        @Test
        @DisplayName("Should preserve UUID values correctly")
        void shouldPreserveUUIDValues() {
            // Arrange
            UUID taskId = UUID.randomUUID();

            TaskDTO taskDTO = new TaskDTO(
                    taskId,
                    "Task",
                    "Description",
                    LocalDateTime.now(),
                    TaskPriority.LOW,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getId()).isEqualTo(taskId);
            assertThat(task.getId()).isNotNull();
        }

        @Test
        @DisplayName("Should handle all combinations of priority and status")
        void shouldHandleAllPriorityStatusCombinations() {
            // Arrange
            TaskDTO taskDTO = new TaskDTO(
                    UUID.randomUUID(),
                    "Completed High Priority Task",
                    "Important task that's done",
                    LocalDateTime.now().minusDays(1),
                    TaskPriority.HIGH,
                    TaskStatus.CLOSED
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
            assertThat(task.getStatus()).isEqualTo(TaskStatus.CLOSED);
        }

        @Test
        @DisplayName("Should not set TaskList relationship in mapper")
        void shouldNotSetTaskListRelationship() {
            // Arrange
            TaskDTO taskDTO = new TaskDTO(
                    UUID.randomUUID(),
                    "Task",
                    "Description",
                    LocalDateTime.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task.getTaskList()).isNull();
        }

        @Test
        @DisplayName("Should preserve all DTO field values in entity")
        void shouldPreserveAllFieldValues() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            LocalDateTime dueDate = LocalDateTime.now().plusDays(3);

            TaskDTO taskDTO = new TaskDTO(
                    taskId,
                    "Preserved Task",
                    "All fields should be preserved",
                    dueDate,
                    TaskPriority.LOW,
                    TaskStatus.IN_PROGRESS
            );

            // Act
            Task task = taskMapper.fromDTO(taskDTO);

            // Assert
            assertThat(task.getId()).isEqualTo(taskDTO.id());
            assertThat(task.getTitle()).isEqualTo(taskDTO.title());
            assertThat(task.getDescription()).isEqualTo(taskDTO.description());
            assertThat(task.getDueDate()).isEqualTo(taskDTO.dueDate());
            assertThat(task.getPriority()).isEqualTo(taskDTO.priority());
            assertThat(task.getStatus()).isEqualTo(taskDTO.status());
        }
    }
}