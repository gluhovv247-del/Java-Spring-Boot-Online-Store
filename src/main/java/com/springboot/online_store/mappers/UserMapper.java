package com.springboot.online_store.mappers;

import com.springboot.online_store.dtos.user.UserCreateDto;
import com.springboot.online_store.dtos.user.UserInfo;
import com.springboot.online_store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserInfo toUserInfo(User user);
}
