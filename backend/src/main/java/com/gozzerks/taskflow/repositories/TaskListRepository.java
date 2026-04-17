package com.gozzerks.taskflow.repositories;

import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, UUID> {

    List<TaskList> findAllByOwner(User owner);

    Optional<TaskList> findByIdAndOwner(UUID id, User owner);
}