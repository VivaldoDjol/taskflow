package com.gozzerks.taskflow.services.impl;

import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.User;
import com.gozzerks.taskflow.exceptions.NotFoundException;
import com.gozzerks.taskflow.repositories.TaskListRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskListServiceImpl Tests")
class TaskListServiceImplTest {

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskListServiceImpl taskListService;

    private UUID taskListId;
    private TaskList taskList;
    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User(UUID.randomUUID(), "alice", "hash", LocalDateTime.now());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        currentUser,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        taskListId = UUID.randomUUID();

        taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setTitle("Test Task List");
        taskList.setDescription("Test Description");
        taskList.setOwner(currentUser);
        taskList.setCreated(LocalDateTime.now());
        taskList.setUpdated(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Find All Task Lists Tests")
    class FindAllTaskListsTests {

        @Test
        @DisplayName("Should return all task lists for the current user")
        void shouldReturnAllTaskLists() {
            // Arrange
            TaskList taskList2 = new TaskList();
            taskList2.setId(UUID.randomUUID());
            taskList2.setTitle("Second List");
            taskList2.setDescription("Second Description");
            taskList2.setOwner(currentUser);

            List<TaskList> expectedLists = Arrays.asList(taskList, taskList2);
            when(taskListRepository.findAllByOwner(currentUser)).thenReturn(expectedLists);

            // Act
            List<TaskList> actualLists = taskListService.listTaskLists();

            // Assert
            assertThat(actualLists)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder(taskList, taskList2);
            verify(taskListRepository, times(1)).findAllByOwner(currentUser);
        }

        @Test
        @DisplayName("Should return empty list when the current user has no lists")
        void shouldReturnEmptyListWhenNoTaskListsExist() {
            // Arrange
            when(taskListRepository.findAllByOwner(currentUser)).thenReturn(List.of());

            // Act
            List<TaskList> actualLists = taskListService.listTaskLists();

            // Assert
            assertThat(actualLists)
                    .isNotNull()
                    .isEmpty();
            verify(taskListRepository, times(1)).findAllByOwner(currentUser);
        }
    }

    @Nested
    @DisplayName("Get Task List Tests")
    class GetTaskListTests {

        @Test
        @DisplayName("Should return task list when ID exists and user owns it")
        void shouldReturnTaskListWhenIdExists() {
            // Arrange
            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.of(taskList));

            // Act
            Optional<TaskList> result = taskListService.getTaskList(taskListId);

            // Assert
            assertThat(result)
                    .isPresent()
                    .contains(taskList);
            verify(taskListRepository, times(1)).findByIdAndOwner(taskListId, currentUser);
        }

        @Test
        @DisplayName("Should return empty Optional when list is missing or owned by another user")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            // Arrange
            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.empty());

            // Act
            Optional<TaskList> result = taskListService.getTaskList(taskListId);

            // Assert
            assertThat(result).isEmpty();
            verify(taskListRepository, times(1)).findByIdAndOwner(taskListId, currentUser);
        }
    }

    @Nested
    @DisplayName("Create Task List Tests")
    class CreateTaskListTests {

        @Test
        @DisplayName("Should create task list successfully and assign current user as owner")
        void shouldCreateTaskListSuccessfully() {
            // Arrange
            TaskList newTaskList = new TaskList();
            newTaskList.setTitle("New List");
            newTaskList.setDescription("New Description");

            TaskList savedTaskList = new TaskList();
            savedTaskList.setId(UUID.randomUUID());
            savedTaskList.setTitle("New List");
            savedTaskList.setDescription("New Description");
            savedTaskList.setOwner(currentUser);
            savedTaskList.setCreated(LocalDateTime.now());
            savedTaskList.setUpdated(LocalDateTime.now());

            when(taskListRepository.save(any(TaskList.class))).thenReturn(savedTaskList);

            // Act
            TaskList result = taskListService.createTaskList(newTaskList);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .extracting(TaskList::getId, TaskList::getTitle, TaskList::getDescription, TaskList::getOwner)
                    .containsExactly(savedTaskList.getId(), "New List", "New Description", currentUser);
            verify(taskListRepository).save(argThat(saved -> currentUser.equals(saved.getOwner())));
        }

        @Test
        @DisplayName("Should throw exception when task list already has an ID")
        void shouldThrowExceptionWhenTaskListHasId() {
            // Arrange - taskList already has an ID from setUp

            // Act & Assert
            assertThatThrownBy(() -> taskListService.createTaskList(taskList))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task list already has an ID");
            verify(taskListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            // Arrange
            TaskList noTitleList = new TaskList();
            noTitleList.setDescription("Description only");

            // Act & Assert
            assertThatThrownBy(() -> taskListService.createTaskList(noTitleList))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task list title is required");
            verify(taskListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when title is blank")
        void shouldThrowExceptionWhenTitleIsBlank() {
            // Arrange
            TaskList blankTitleList = new TaskList();
            blankTitleList.setTitle("   ");

            // Act & Assert
            assertThatThrownBy(() -> taskListService.createTaskList(blankTitleList))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task list title is required");
            verify(taskListRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Task List Tests")
    class UpdateTaskListTests {

        @Test
        @DisplayName("Should update task list when ID exists and user owns it")
        void shouldUpdateTaskListWhenIdExists() {
            // Arrange
            TaskList existingTaskList = new TaskList();
            existingTaskList.setId(taskListId);
            existingTaskList.setTitle("Old Title");
            existingTaskList.setDescription("Old Description");
            existingTaskList.setOwner(currentUser);
            existingTaskList.setCreated(LocalDateTime.now().minusDays(1));
            existingTaskList.setUpdated(LocalDateTime.now().minusDays(1));

            TaskList updatedDetails = new TaskList();
            updatedDetails.setId(taskListId);
            updatedDetails.setTitle("Updated Title");
            updatedDetails.setDescription("Updated Description");

            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.of(existingTaskList));
            when(taskListRepository.save(any(TaskList.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            TaskList result = taskListService.updateTaskList(taskListId, updatedDetails);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getDescription()).isEqualTo("Updated Description");

            verify(taskListRepository, times(1)).findByIdAndOwner(taskListId, currentUser);
            verify(taskListRepository, times(1)).save(any(TaskList.class));
        }

        @Test
        @DisplayName("Should update the updatedAt timestamp")
        void shouldUpdateTimestamp() {
            // Arrange
            LocalDateTime originalUpdated = LocalDateTime.now().minusDays(1);

            TaskList existingTaskList = new TaskList();
            existingTaskList.setId(taskListId);
            existingTaskList.setTitle("Old Title");
            existingTaskList.setOwner(currentUser);
            existingTaskList.setCreated(LocalDateTime.now().minusDays(2));
            existingTaskList.setUpdated(originalUpdated);

            TaskList updatedDetails = new TaskList();
            updatedDetails.setId(taskListId);
            updatedDetails.setTitle("Updated Title");

            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.of(existingTaskList));
            when(taskListRepository.save(any(TaskList.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskListService.updateTaskList(taskListId, updatedDetails);

            // Assert
            verify(taskListRepository).save(argThat(saved ->
                saved.getUpdated().isAfter(originalUpdated)
            ));
        }

        @Test
        @DisplayName("Should throw NotFoundException when task list is missing or owned by another user")
        void shouldThrowNotFoundWhenMissing() {
            // Arrange
            TaskList updatedDetails = new TaskList();
            updatedDetails.setId(taskListId);
            updatedDetails.setTitle("Updated Title");

            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskListService.updateTaskList(taskListId, updatedDetails))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Task list not found");
            verify(taskListRepository, times(1)).findByIdAndOwner(taskListId, currentUser);
            verify(taskListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when task list has no ID")
        void shouldThrowExceptionWhenNoId() {
            // Arrange
            TaskList noIdList = new TaskList();
            noIdList.setTitle("Updated Title");

            // Act & Assert
            assertThatThrownBy(() -> taskListService.updateTaskList(taskListId, noIdList))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task list must have an ID");
            verify(taskListRepository, never()).findByIdAndOwner(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when IDs do not match")
        void shouldThrowExceptionWhenIdsMismatch() {
            // Arrange
            UUID differentId = UUID.randomUUID();
            TaskList mismatchList = new TaskList();
            mismatchList.setId(differentId);
            mismatchList.setTitle("Updated Title");

            // Act & Assert
            assertThatThrownBy(() -> taskListService.updateTaskList(taskListId, mismatchList))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not permitted");
            verify(taskListRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Task List Tests")
    class DeleteTaskListTests {

        @Test
        @DisplayName("Should delete task list when current user owns it")
        void shouldDeleteTaskListWhenIdExists() {
            // Arrange
            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.of(taskList));

            // Act
            taskListService.deleteTaskList(taskListId);

            // Assert
            verify(taskListRepository, times(1)).findByIdAndOwner(taskListId, currentUser);
            verify(taskListRepository, times(1)).delete(taskList);
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleting a list missing or owned by another user")
        void shouldThrowNotFoundWhenDeletingMissing() {
            // Arrange
            when(taskListRepository.findByIdAndOwner(taskListId, currentUser))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskListService.deleteTaskList(taskListId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Task list not found");
            verify(taskListRepository, never()).delete(any());
        }
    }
}
