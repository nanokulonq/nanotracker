package com.nanokulon.nanotracker.repository;

import com.nanokulon.nanotracker.entity.TrackerUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TrackerUserRepository extends CrudRepository<TrackerUser, Integer> {

    Optional<TrackerUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
