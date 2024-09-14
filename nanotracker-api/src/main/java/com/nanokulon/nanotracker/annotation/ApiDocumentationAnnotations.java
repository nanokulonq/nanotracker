package com.nanokulon.nanotracker.annotation;

import com.nanokulon.nanotracker.dto.response.AuthenticationResponse;
import com.nanokulon.nanotracker.dto.response.TrackerUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class ApiDocumentationAnnotations {

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Поулчение списка задач",
            description = "Получает список задач аутентифицированного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Спсиок задач пользователя",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            type = "object",
                                                            properties = {
                                                                    @StringToClassMapItem(key = "id", value = Integer.class),
                                                                    @StringToClassMapItem(key = "title", value = String.class),
                                                                    @StringToClassMapItem(key = "details", value = String.class),
                                                                    @StringToClassMapItem(key = "isCompleted", value = Boolean.class)
                                                            }
                                                    )
                                            )
                                    )
                            }
                    )
            })
    public @interface OperationFindUserTasks {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Создание задачи",
            description = "Создаёт задачу",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания задачи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "title", value = String.class),
                                            @StringToClassMapItem(key = "details", value = String.class),
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Задача успешно создана",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(
                                                    type = "object",
                                                    properties = {
                                                            @StringToClassMapItem(key = "id", value = Integer.class),
                                                            @StringToClassMapItem(key = "title", value = String.class),
                                                            @StringToClassMapItem(key = "details", value = String.class),
                                                            @StringToClassMapItem(key = "isCompleted", value = Boolean.class)
                                                    }
                                            )
                                    )
                            }
                    )
            })
    @ApiResponse400BadRequest
    public @interface OperationCreateTask {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Получение задачи",
            description = "Возвращает информацию о задаче по её ID для аутентифицированного пользователя",
            parameters = {
                    @Parameter(
                            name = "taskId",
                            description = "ID задачи",
                            required = true,
                            schema = @Schema(type = "integer")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация о задаче",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            type = "object",
                                            properties = {
                                                    @StringToClassMapItem(key = "id", value = Integer.class),
                                                    @StringToClassMapItem(key = "title", value = String.class),
                                                    @StringToClassMapItem(key = "details", value = String.class),
                                                    @StringToClassMapItem(key = "isCompleted", value = Boolean.class)
                                            }
                                    )
                            )
                    )
            }
    )
    @ApiResponse404NotFound
    @ApiResponse403Forbidden
    public @interface OperationFindTask {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Обновление задачи",
            description = "Обновляет информацию о задаче по её ID для аутентифицированного пользователя",
            parameters = {
                    @Parameter(
                            name = "taskId",
                            description = "ID задачи",
                            required = true,
                            schema = @Schema(type = "integer")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления задачи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "title", value = String.class),
                                            @StringToClassMapItem(key = "details", value = String.class),
                                            @StringToClassMapItem(key = "isCompleted", value = Boolean.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Задача успешно обновлена"
                    )
            }
    )
    @ApiResponse404NotFound
    @ApiResponse403Forbidden
    @ApiResponse400BadRequest
    public @interface OperationUpdateTask {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Удаление задачи",
            description = "Удаляет задачу по её ID для аутентифицированного пользователя",
            parameters = {
                    @Parameter(
                            name = "taskId",
                            description = "ID задачи",
                            required = true,
                            schema = @Schema(type = "integer")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Задача успешно удалена"
                    )
            }
    )
    @ApiResponse404NotFound
    @ApiResponse403Forbidden
    public @interface OperationDeleteTask {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя с предоставленными регистрационными данными",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "username", value = String.class),
                                            @StringToClassMapItem(key = "password", value = String.class),
                                            @StringToClassMapItem(key = "email", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно зарегистрирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TrackerUserResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Пользователь с таким именем или email уже существует",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Пользователь уже аутентифицирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Пароль должен содержать хотя бы 1 цифру и 1 заглавную букву",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    public @interface OperationRegister {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Авторизует пользователя по предоставленным учетным данным",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для аутентификации",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "username", value = String.class),
                                            @StringToClassMapItem(key = "password", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно аутентифицирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = AuthenticationResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неверные учетные данные",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Пользователь уже аутентифицирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    public @interface OperationAuthenticate {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Получение информации о пользователе",
            description = "Возвращает информацию о пользователе на основе идентификатора аутентифицированного пользователя.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация о пользователе успешно возвращена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TrackerUserResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Пользователь не аутентифицирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    public @interface OperationFindUser {
    }
}
