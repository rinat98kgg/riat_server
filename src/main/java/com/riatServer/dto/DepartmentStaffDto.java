package com.riatServer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.riatServer.domain.Message;
import com.riatServer.domain.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentStaffDto {
    long id;
    String firstName;
    String fileName;
    List<MessageDto> messages;

    public DepartmentStaffDto toDepartmentStaff(){
        DepartmentStaffDto departmentStaffDto = new DepartmentStaffDto();
        departmentStaffDto.setFirstName(firstName);
        departmentStaffDto.setId(id);
        departmentStaffDto.setMessages(messages);
        return departmentStaffDto;
    }

    public static DepartmentStaffDto fromDepartmentStaff(User user, List<Message> messages){
        DepartmentStaffDto departmentStaffDto = new DepartmentStaffDto();
        List<MessageDto> messageDto = new ArrayList<>();
        messageDto.add(MessageDto.fromMessage(messages));

        departmentStaffDto.setId(user.getId());
        departmentStaffDto.setFirstName(user.getFirstName());
        departmentStaffDto.setFileName(user.getFileName());
        departmentStaffDto.setMessages(messageDto);
        return departmentStaffDto;
    }

}