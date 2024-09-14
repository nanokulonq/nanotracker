package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.annotation.ApiDocumentationAnnotations;
import com.nanokulon.nanotracker.dto.response.TrackerUserResponse;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Работа с пользователем", description = "Точка входа для получения данных о пользователе")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserService userService;

    @ApiDocumentationAnnotations.OperationFindUser
    @GetMapping
    public ResponseEntity<TrackerUserResponse> findUser(
            @AuthenticationPrincipal TrackerUserDetails userDetails) {
        return ResponseEntity.ok(
                this.userService.findUserById(userDetails.getId()));
    }
}
