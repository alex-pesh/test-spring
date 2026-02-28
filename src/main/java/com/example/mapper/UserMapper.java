package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.example.dto.UserDto;
import com.example.entity.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

	User toEntity(UserDto userDto);

	UserDto toUserDto(User user);

	List<UserDto> toUsersDto(List<User> users);

}