package com.riatServer.dto;

import com.riatServer.domain.Role;
import com.riatServer.domain.User;
import lombok.Data;

@Data
public class AdminUserDto {
    private Long id;
    private String username;
    private String telephone;
    private String status;

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setName(username);
        user.setTelephon(telephone);
        user.setStatus(Role.Status.valueOf(status));
        return user;
    }

    public static AdminUserDto fromUser(User user) {
        AdminUserDto adminUserDto = new AdminUserDto();
        adminUserDto.setId(user.getId());
        adminUserDto.setUsername(user.getName());
        adminUserDto.setTelephone(user.getTelephone());
        adminUserDto.setStatus(user.getStatus().name());
        return adminUserDto;
    }
}