package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = CardInfoMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserDto dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "cards", target = "cards")
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(UserDto dto, @MappingTarget User user);

    @org.mapstruct.AfterMapping
    default void linkCards(@MappingTarget User user) {
        if (user.getCards() != null) {
            user.getCards().forEach(card -> card.setUser(user));
        }
    }
}
