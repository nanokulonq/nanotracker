package com.nanokulon.nanotracker.mapper;

import com.nanokulon.nanotracker.dto.response.TrackerUserResponse;
import com.nanokulon.nanotracker.entity.TrackerUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = TaskMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TrackerUserMapper {

    TrackerUserResponse toTrackerUserResponse(TrackerUser trackerUser);
}
