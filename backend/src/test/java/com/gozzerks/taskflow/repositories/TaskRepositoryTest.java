package com.gozzerks.taskflow.repositories;

import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("TaskRepository Tests")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    private TaskList taskList;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        taskList = new TaskList();
        taskList.setTitle("Test List");
        taskList.setDescription("Test Description");
        taskList.setCreated(now);
        taskList.setUpdated(now);
        taskList = entityManager.persistAndFlush(taskList);

        // Arrange
        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(TaskStatus.OPEN);
        task1.setPriority(TaskPriority.HIGH);
        task1.setTaskList(taskList);
        task1.setCreated(now);
        task1.setUpdated(now);
        task1 = entityManager.persistAndFlush(task1);

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.CLOSED);
        task2.setPriority(TaskPriority.LOW);
        task2.setTaskList(taskList);
        task2.setCreated(now);
        task2.setUpdated(now);
        task2 = entityManager.persistAndFlush(task2);

        entityManager.clear();
    }

    @Nested
    @DisplayName("findByTaskListId Tests")
    class FindByTaskListIdTests {

        @Test
        @DisplayName("Should find all tasks for given task list ID")
        void shouldFindAllTasksByTaskListId() {
            // Act
            List<Task> foundTasks = taskRepository.findByTaskListId(taskList.getId());

            // Assert
            assertThat(foundTasks).hasSize(2);
            assertThat(foundTasks).extracting(Task::getTitle)
                    .containsExactlyInAnyOrder("Task 1", "Task 2");
        }

        @Test
        @DisplayName("Should return empty list when task list has no tasks")
        void shouldReturnEmptyListWhenNoTasks() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            TaskList emptyList = new TaskList();
            emptyList.setTitle("Empty List");
            emptyList.setDescription("No tasks");
            emptyList.setCreated(now);
            emptyList.setUpdated(now);
            emptyList = entityManager.persistAndFlush(emptyList);

            // Act
            List<Task> foundTasks = taskRepository.findByTaskListId(emptyList.getId());

            // Assert
            assertThat(foundTasks).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list for non-existent task list ID")
        void shouldReturnEmptyListForNonExistentTaskListId() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act
            List<Task> foundTasks = taskRepository.findByTaskListId(nonExistentId);

            // Assert
            assertThat(foundTasks).isEmpty();
        }

        @Test
        @DisplayName("Should only return tasks for specified task list")
        void shouldOnlyReturnTasksForSpecifiedTaskList() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            TaskList anotherList = new TaskList();
            anotherList.setTitle("Another List");
            anotherList.setDescription("Different list");
            anotherList.setCreated(now);
            anotherList.setUpdated(now);
            anotherList = entityManager.persistAndFlush(anotherList);

            Task anotherTask = new Task();
            anotherTask.setTitle("Another Task");
            anotherTask.setDescription("Different task");
            anotherTask.setStatus(TaskStatus.OPEN);
            anotherTask.setPriority(TaskPriority.MEDIUM);
            anotherTask.setTaskList(anotherList);
            anotherTask.setCreated(now);
            anotherTask.setUpdated(now);
            entityManager.persistAndFlush(anotherTask);

            // Act
            List<Task> foundTasks = taskRepository.findByTaskListId(taskList.getId());

            // Assert
            assertThat(foundTasks).hasSize(2);
            assertThat(foundTasks).extracting(Task::getTitle)
                    .doesNotContain("Another Task");
        }
    }

    @Nested
    @DisplayName("findByTaskListIdAndId Tests")
    class FindByTaskListIdAndIdTests {

        @Test
        @DisplayName("Should find task by task list ID and task ID")
        void shouldFindTaskByTaskListIdAndTaskId() {
            // Act
            Optional<Task> foundTask = taskRepository.findByTaskListIdAndId(
                    taskList.getId(),
                    task1.getId()
            );

            // Assert
            assertThat(foundTask).isPresent();
            assertThat(foundTask.get().getTitle()).isEqualTo("Task 1");
            assertThat(foundTask.get().getTaskList().getId()).isEqualTo(taskList.getId());
        }

        @Test
        @DisplayName("Should return empty when task exists but belongs to different list")
        void shouldReturnEmptyWhenTaskBelongsToDifferentList() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            TaskList anotherList = new TaskList();
            anotherList.setTitle("Another List");
            anotherList.setDescription("Different list");
            anotherList.setCreated(now);
            anotherList.setUpdated(now);
            anotherList = entityManager.persistAndFlush(anotherList);

            // Act
            Optional<Task> foundTask = taskRepository.findByTaskListIdAndId(
                    anotherList.getId(),
                    task1.getId()
            );

            // Assert
            assertThat(foundTask).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when task ID doesn't exist")
        void shouldReturnEmptyWhenTaskIdDoesNotExist() {
            // Arrange
            UUID nonExistentTaskId = UUID.randomUUID();

            // Act
            Optional<Task> foundTask = taskRepository.findByTaskListIdAndId(
                    taskList.getId(),
                    nonExistentTaskId
            );

            // Assert
            assertThat(foundTask).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when task list ID doesn't exist")
        void shouldReturnEmptyWhenTaskListIdDoesNotExist() {
            // Arrange
            UUID nonExistentListId = UUID.randomUUID();

            // Act
            Optional<Task> foundTask = taskRepository.findByTaskListIdAndId(
                    nonExistentListId,
                    task1.getId()
            );

            // Assert
            assertThat(foundTask).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when both IDs don't exist")
        void shouldReturnEmptyWhenBothIdsDoNotExist() {
            // Arrange
            UUID nonExistentListId = UUID.randomUUID();
            UUID nonExistentTaskId = UUID.randomUUID();

            // Act
            Optional<Task> foundTask = taskRepository.findByTaskListIdAndId(
                    nonExistentListId,
                    nonExistentTaskId
            );

            // Assert
            assertThat(foundTask).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByTaskListIdAndId Tests")
    class DeleteByTaskListIdAndIdTests {

        @Test
        @DisplayName("Should delete task when both IDs match")
        void shouldDeleteTaskWhenBothIdsMatch() {
            // Arrange
            UUID taskListId = taskList.getId();
            UUID taskId = task1.getId();

            // Act
            taskRepository.deleteByTaskListIdAndId(taskListId, taskId);
            entityManager.flush();

            // Assert
            Optional<Task> deletedTask = taskRepository.findById(taskId);
            assertThat(deletedTask).isEmpty();

            Optional<Task> otherTask = taskRepository.findById(task2.getId());
            assertThat(otherTask).isPresent();
        }

        @Test
        @DisplayName("Should not delete when task belongs to different list")
        void shouldNotDeleteWhenTaskBelongsToDifferentList() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            TaskList anotherList = new TaskList();
            anotherList.setTitle("Another List");
            anotherList.setDescription("Different list");
            anotherList.setCreated(now);
            anotherList.setUpdated(now);
            anotherList = entityManager.persistAndFlush(anotherList);

            // Act
            taskRepository.deleteByTaskListIdAndId(anotherList.getId(), task1.getId());
            entityManager.flush();

            // Assert
            Optional<Task> stillExists = taskRepository.findById(task1.getId());
            assertThat(stillExists).isPresent();
        }

        @Test
        @DisplayName("Should not throw exception when task ID doesn't exist")
        void shouldNotThrowExceptionWhenTaskIdDoesNotExist() {
            // Arrange
            UUID nonExistentTaskId = UUID.randomUUID();

            // Act & Assert
            taskRepository.deleteByTaskListIdAndId(taskList.getId(), nonExistentTaskId);
            entityManager.flush();
        }

        @Test
        @DisplayName("Should not throw exception when task list ID doesn't exist")
        void shouldNotThrowExceptionWhenTaskListIdDoesNotExist() {
            // Arrange
            UUID nonExistentListId = UUID.randomUUID();

            // Act & Assert
            taskRepository.deleteByTaskListIdAndId(nonExistentListId, task1.getId());
            entityManager.flush();

            Optional<Task> stillExists = taskRepository.findById(task1.getId());
            assertThat(stillExists).isPresent();
        }
    }

    @Nested
    @DisplayName("Task-TaskList Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should cascade task list deletion to tasks")
        void shouldCascadeTaskListDeletionToTasks() {
            // Arrange
            UUID taskId1 = task1.getId();
            UUID taskId2 = task2.getId();

            // Act
            taskListRepository.deleteById(taskList.getId());
            entityManager.flush();

            // Assert
            assertThat(taskRepository.findById(taskId1)).isEmpty();
            assertThat(taskRepository.findById(taskId2)).isEmpty();
        }

        @Test
        @DisplayName("Should maintain task list reference after task update")
        void shouldMaintainTaskListReferenceAfterUpdate() {
            // Arrange
            task1.setTitle("Updated Title");
            task1.setDescription("Updated Description");

            // Act
            Task updatedTask = taskRepository.save(task1);
            entityManager.flush();

            // Assert
            assertThat(updatedTask.getTaskList().getId()).isEqualTo(taskList.getId());
            assertThat(updatedTask.getTaskList().getTitle()).isEqualTo("Test List");
        }
    }
}