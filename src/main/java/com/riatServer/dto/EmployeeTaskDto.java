package com.riatServer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeTaskDto {
    Long id;
    boolean active;
    String description;
    String name;
    float procent;
    Long taskId;
    Long userId;
    Long taskStatusId;
    Long ownerId;
    boolean templateTask;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime termDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateDate;
    String fileName;
    String firstName;
    String LastName;

    public EmployeeTaskDto toUser(){
        EmployeeTaskDto user = new EmployeeTaskDto();
        user.setId(id);
        user.setProcent(procent);
        user.setTermDate(termDate);
        user.setDescription(description);
        user.setTaskId(taskId);
        user.setUserId(userId);
        user.setTaskStatusId(taskStatusId);
        user.setOwnerId(ownerId);
        user.setTemplateTask(templateTask);
        user.setCreateDate(createDate);
        user.setUpdateDate(updateDate);
        user.setName(name);
        return user;
    }

    public static EmployeeTaskDto fromUser(ListOfEmployees taskEmployee, Task task, User user) {
        EmployeeTaskDto employeeTaskDto = new EmployeeTaskDto();
        employeeTaskDto.setActive(taskEmployee.isActive());
        employeeTaskDto.setId(taskEmployee.getId());
        employeeTaskDto.setCreateDate(taskEmployee.getCreateDate());
        employeeTaskDto.setUpdateDate(taskEmployee.getUpdateDate());
        employeeTaskDto.setTaskId(taskEmployee.getTaskId());
        employeeTaskDto.setUserId(taskEmployee.getUserId());
        employeeTaskDto.setTaskStatusId(taskEmployee.getTaskStatusId());
        employeeTaskDto.setOwnerId(taskEmployee.getOwnerId());
        employeeTaskDto.setDescription(task.getDescription());
        employeeTaskDto.setProcent(task.getProcent());
        employeeTaskDto.setTermDate(task.getTermDate());
        employeeTaskDto.setName(task.getName());
        employeeTaskDto.setTemplateTask(task.isTemplateTask());
        employeeTaskDto.setFileName(user.getFileName());
        employeeTaskDto.setFirstName(user.getFirstName());
        employeeTaskDto.setLastName(user.getLastName());
        return employeeTaskDto;
    }

}