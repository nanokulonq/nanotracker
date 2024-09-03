package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.response.TrackerUserResponse;
import com.nanokulon.nanotracker.entity.TrackerUser;
import com.nanokulon.nanotracker.mapper.TrackerUserMapper;
import com.nanokulon.nanotracker.repository.TrackerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final TrackerUserRepository trackerUserRepository;
    private final TrackerUserMapper trackerUserMapper;

    @Override
    @Transactional(readOnly = true)
    public TrackerUserResponse findUserById(int userId) {
        TrackerUser user = this.trackerUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return this.trackerUserMapper.toTrackerUserResponse(user);
    }
}
