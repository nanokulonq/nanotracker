package com.nanokulon.nanotracker.annotation;

import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "404",
        description = "Задача не найдена",
        content = @Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(
                        type = "object",
                        properties = {
                                @StringToClassMapItem(key = "type", value = String.class),
                                @StringToClassMapItem(key = "title", value = String.class),
                                @StringToClassMapItem(key = "status", value = Integer.class),
                                @StringToClassMapItem(key = "detail", value = String.class),
                                @StringToClassMapItem(key = "instance", value = String.class)
                        },
                        example = """
                {
                    "type": "about:blank",
                    "title": "Not Found",
                    "status": 404,
                    "detail": "Task not found",
                    "instance": "/api/v1/tasks/108"
                }
                """
                )
        )
)
public @interface ApiResponse404NotFound {
}
