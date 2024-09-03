package com.nanokulon.nanotracker.mapper;

import com.nanokulon.nanotracker.dto.request.TaskCreateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.entity.Task;
import com.nanokulon.nanotracker.entity.TrackerUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponse toTaskResponse(Task task);

    @Mapping(source = "user", target = "user")
    @Mapping(target = "id", ignore = true)
    Task toTask(TaskCreateRequest taskCreateRequest, TrackerUser user,
                LocalDateTime creationDate, boolean isCompleted);
}
