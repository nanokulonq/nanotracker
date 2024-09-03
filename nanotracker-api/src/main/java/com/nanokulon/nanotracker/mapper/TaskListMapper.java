package com.nanokulon.nanotracker.mapper;

import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.entity.Task;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = TaskMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TaskListMapper {

    List<TaskResponse> toTaskResponse(List<Task> taskList);
}
