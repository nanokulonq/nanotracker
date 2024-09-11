package com.nanokulon.scheduler.repository;

import com.nanokulon.scheduler.entity.TrackerUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TrackerUserRepository extends CrudRepository<TrackerUser, Integer> {

    @Query(value = "SELECT u.* FROM user_management.t_user u" +
            " JOIN task_management.t_task task ON u.id = task.id_user" +
            " WHERE task.c_completed = true" +
            " AND task.c_completed_date::date = CURRENT_DATE", nativeQuery = true)
    List<TrackerUser> findUsersWithTasksCompletedToday();
}
