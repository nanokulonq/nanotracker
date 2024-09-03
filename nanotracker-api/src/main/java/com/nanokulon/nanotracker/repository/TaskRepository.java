package com.nanokulon.nanotracker.repository;

import com.nanokulon.nanotracker.entity.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Integer> {

    List<Task> findAllByUserId(int userId);
}
