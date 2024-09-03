package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.response.TrackerUserResponse;

public interface UserService {

    TrackerUserResponse findUserById(int userId);
}
