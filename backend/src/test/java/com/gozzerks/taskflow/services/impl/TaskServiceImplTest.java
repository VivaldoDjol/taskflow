package com.gozzerks.taskflow.services.impl;

import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import com.gozzerks.taskflow.repositories.TaskListRepository;
import com.gozzerks.taskflow.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceImpl Tests")
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskList taskList;
    private UUID taskId;
    private UUID taskListId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        taskListId = UUID.randomUUID();

        taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setTitle("Test Task List");
        taskList.setDescription("Test Description");
        taskList.setCreated(LocalDateTime.now());
        taskList.setUpdated(LocalDateTime.now());

        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.OPEN);
        task.setTaskList(taskList);
        task.setCreated(LocalDateTime.now());
        task.setUpdated(LocalDateTime.now());
    }

    @Nested
    @DisplayName("List Tasks Tests")
    class ListTasksTests {

        @Test
        @DisplayName("Should return all tasks for a task list")
        void shouldReturnAllTasksForTaskList() {
            // Arrange
            Task task2 = new Task();
            task2.setId(UUID.randomUUID());
            task2.setTitle("Second Task");
            task2.setTaskList(taskList);

            List<Task> tasks = List.of(task, task2);
            when(taskRepository.findByTaskListId(taskListId)).thenReturn(tasks);

            // Act
            List<Task> result = taskService.listTasks(taskListId);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(task, task2);
            verify(taskRepository).findByTaskListId(taskListId);
        }

        @Test
        @DisplayName("Should return empty list when no tasks exist")
        void shouldReturnEmptyListWhenNoTasksExist() {
            // Arrange
            when(taskRepository.findByTaskListId(taskListId)).thenReturn(List.of());

            // Act
            List<Task> result = taskService.listTasks(taskListId);

            // Assert
            assertThat(result).isEmpty();
            verify(taskRepository).findByTaskListId(taskListId);
        }
    }

    @Nested
    @DisplayName("Get Task Tests")
    class GetTaskTests {

        @Test
        @DisplayName("Should return task when both IDs match")
        void shouldReturnTaskWhenBothIdsMatch() {
            // Arrange
            when(taskRepository.findByTaskListIdAndId(taskListId, taskId)).thenReturn(Optional.of(task));

            // Act
            Optional<Task> result = taskService.getTask(taskListId, taskId);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(task);
            verify(taskRepository).findByTaskListIdAndId(taskListId, taskId);
        }

        @Test
        @DisplayName("Should return empty when task not found")
        void shouldReturnEmptyWhenTaskNotFound() {
            // Arrange
            when(taskRepository.findByTaskListIdAndId(taskListId, taskId)).thenReturn(Optional.empty());

            // Act
            Optional<Task> result = taskService.getTask(taskListId, taskId);

            // Assert
            assertThat(result).isEmpty();
            verify(taskRepository).findByTaskListIdAndId(taskListId, taskId);
        }
    }

    @Nested
    @DisplayName("Create Task Tests")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task successfully with valid task list")
        void shouldCreateTaskSuccessfully() {
            // Arrange
            Task newTask = new Task();
            newTask.setTitle("New Task");
            newTask.setDescription("New Description");
            newTask.setPriority(TaskPriority.MEDIUM);

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
                Task saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // Act
            Task result = taskService.createTask(taskListId, newTask);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("New Task");
            assertThat(result.getStatus()).isEqualTo(TaskStatus.OPEN);
            assertThat(result.getTaskList()).isEqualTo(taskList);
            verify(taskListRepository).findById(taskListId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("Should default priority to MEDIUM when not provided")
        void shouldDefaultPriorityToMedium() {
            // Arrange
            Task newTask = new Task();
            newTask.setTitle("Task Without Priority");

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskService.createTask(taskListId, newTask);

            // Assert
            verify(taskRepository).save(argThat(saved ->
                saved.getPriority() == TaskPriority.MEDIUM
            ));
        }

        @Test
        @DisplayName("Should always set status to OPEN for new tasks")
        void shouldAlwaysSetStatusToOpen() {
            // Arrange
            Task newTask = new Task();
            newTask.setTitle("Task With Status");
            newTask.setStatus(TaskStatus.CLOSED);

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskService.createTask(taskListId, newTask);

            // Assert
            verify(taskRepository).save(argThat(saved ->
                saved.getStatus() == TaskStatus.OPEN
            ));
        }

        @Test
        @DisplayName("Should throw exception when task already has an ID")
        void shouldThrowExceptionWhenTaskHasId() {
            // Arrange - task already has ID from setUp

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, task))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task already has an ID");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            // Arrange
            Task noTitleTask = new Task();
            noTitleTask.setDescription("Description only");

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, noTitleTask))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task must have a title");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when title is blank")
        void shouldThrowExceptionWhenTitleIsBlank() {
            // Arrange
            Task blankTitleTask = new Task();
            blankTitleTask.setTitle("   ");

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, blankTitleTask))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task must have a title");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when task list not found")
        void shouldThrowExceptionWhenTaskListNotFound() {
            // Arrange
            Task newTask = new Task();
            newTask.setTitle("New Task");

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, newTask))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid Task List ID");
            verify(taskListRepository).findById(taskListId);
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should set timestamps when creating task")
        void shouldSetTimestamps() {
            // Arrange
            Task newTask = new Task();
            newTask.setTitle("New Task");

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskService.createTask(taskListId, newTask);

            // Assert
            verify(taskRepository).save(argThat(saved ->
                saved.getCreated() != null && saved.getUpdated() != null
            ));
        }
    }

    @Nested
    @DisplayName("Update Task Tests")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update task with new values")
        void shouldUpdateTaskWithNewValues() {
            // Arrange
            Task updates = new Task();
            updates.setId(taskId);
            updates.setTitle("Updated Title");
            updates.setDescription("Updated Description");
            updates.setPriority(TaskPriority.LOW);
            updates.setStatus(TaskStatus.CLOSED);

            when(taskRepository.findByTaskListIdAndId(taskListId, taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Task result = taskService.updateTask(taskListId, taskId, updates);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getDescription()).isEqualTo("Updated Description");
            assertThat(result.getPriority()).isEqualTo(TaskPriority.LOW);
            assertThat(result.getStatus()).isEqualTo(TaskStatus.CLOSED);
            verify(taskRepository).findByTaskListIdAndId(taskListId, taskId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("Should throw exception when task not found")
        void shouldThrowExceptionWhenTaskNotFound() {
            // Arrange
            Task updates = new Task();
            updates.setId(taskId);
            updates.setTitle("Updated");
            updates.setPriority(TaskPriority.MEDIUM);
            updates.setStatus(TaskStatus.OPEN);

            when(taskRepository.findByTaskListIdAndId(taskListId, taskId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskListId, taskId, updates))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task not found");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when task has no ID")
        void shouldThrowExceptionWhenNoId() {
            // Arrange
            Task updates = new Task();
            updates.setTitle("Updated");
            updates.setPriority(TaskPriority.MEDIUM);
            updates.setStatus(TaskStatus.OPEN);

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskListId, taskId, updates))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task must have an ID");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when task ID does not match path ID")
        void shouldThrowExceptionWhenIdMismatch() {
            // Arrange
            UUID differentId = UUID.randomUUID();
            Task updates = new Task();
            updates.setId(differentId);
            updates.setTitle("Updated");
            updates.setPriority(TaskPriority.MEDIUM);
            updates.setStatus(TaskStatus.OPEN);

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskListId, taskId, updates))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task ID does not match");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when priority is null")
        void shouldThrowExceptionWhenPriorityNull() {
            // Arrange
            Task updates = new Task();
            updates.setId(taskId);
            updates.setTitle("Updated");
            updates.setStatus(TaskStatus.OPEN);

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskListId, taskId, updates))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("priority");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when status is null")
        void shouldThrowExceptionWhenStatusNull() {
            // Arrange
            Task updates = new Task();
            updates.setId(taskId);
            updates.setTitle("Updated");
            updates.setPriority(TaskPriority.MEDIUM);

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskListId, taskId, updates))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("status");
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update the updated timestamp")
        void shouldUpdateTimestamp() {
            // Arrange
            LocalDateTime originalUpdated = task.getUpdated();

            Task updates = new Task();
            updates.setId(taskId);
            updates.setTitle("Updated Title");
            updates.setPriority(TaskPriority.LOW);
            updates.setStatus(TaskStatus.CLOSED);

            when(taskRepository.findByTaskListIdAndId(taskListId, taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskService.updateTask(taskListId, taskId, updates);

            // Assert
            verify(taskRepository).save(argThat(saved ->
                saved.getUpdated() != null
            ));
        }
    }

    @Nested
    @DisplayName("Delete Task Tests")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delete task successfully")
        void shouldDeleteTaskSuccessfully() {
            // Arrange
            doNothing().when(taskRepository).deleteByTaskListIdAndId(taskListId, taskId);

            // Act
            taskService.deleteTask(taskListId, taskId);

            // Assert
            verify(taskRepository).deleteByTaskListIdAndId(taskListId, taskId);
        }

        @Test
        @DisplayName("Should call deleteByTaskListIdAndId exactly once")
        void shouldCallDeleteOnce() {
            // Arrange
            doNothing().when(taskRepository).deleteByTaskListIdAndId(taskListId, taskId);

            // Act
            taskService.deleteTask(taskListId, taskId);

            // Assert
            verify(taskRepository, times(1)).deleteByTaskListIdAndId(taskListId, taskId);
        }
    }
}
