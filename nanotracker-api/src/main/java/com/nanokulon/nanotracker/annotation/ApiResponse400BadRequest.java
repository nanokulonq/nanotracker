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
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(
                        type = "object",
                        properties = {
                                @StringToClassMapItem(key = "type", value = String.class),
                                @StringToClassMapItem(key = "title", value = String.class),
                                @StringToClassMapItem(key = "status", value = String.class),
                                @StringToClassMapItem(key = "detail", value = String.class),
                                @StringToClassMapItem(key = "instance", value = String.class),
                                @StringToClassMapItem(key = "errors", value = String.class)
                        },
                        example = """
            {
                "type": "about:blank",
                "title": "Bad Request",
                "status": 400,
                "detail": "Error 400",
                "instance": "/api/v1/tasks/create",
                "errors": [
                    "The task title must be between 3 and 100 characters"
                ]
            }
            """
                )
        )
)
public @interface ApiResponse400BadRequest {
}
