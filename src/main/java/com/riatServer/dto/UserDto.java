package com.riatServer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.riatServer.domain.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String username;
    private String telephone;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long position;
    private String fileName;


    public User toUser(){
        User user = new User();
        user.setId(id);
        user.setFileName(fileName);
        user.setName(username);
        user.setTelephon(telephone);
        user.setPatronymic(patronymic);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPositionId(position);
        return user;
    }

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getName());
        userDto.setTelephone(user.getTelephone());
        userDto.setFirstName(user.getFirstName());
        userDto.setPatronymic(user.getPatronymic());
        userDto.setLastName(user.getLastName());
        userDto.setPosition(user.getPositionId());
        userDto.setFileName(user.getFileName());
        return userDto;
    }
}