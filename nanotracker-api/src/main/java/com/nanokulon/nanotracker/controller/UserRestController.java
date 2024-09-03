package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.dto.response.TrackerUserResponse;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<TrackerUserResponse> getUser(
            @AuthenticationPrincipal TrackerUserDetails userDetails) {
        return ResponseEntity.ok(
                this.userService.findUserById(userDetails.getId()));
    }
}
