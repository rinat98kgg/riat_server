package com.riatServer.service;

import com.riatServer.domain.*;
import com.riatServer.dto.EmployeeTaskDto;
import com.riatServer.dto.TasksForReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    void assignTaskToEmployee(User user, Long taskId);
    void assignTaskToDepartment(Department department, Long taskId);
    void changeTaskProgress(Long taskId, Long taskStatusId, Long userId);
    List<EmployeeTaskDto> listOfAllActiveTaskByEmployee(Long userId);
    List<Task> listOfAllActiveTaskByDepartment(Long userId);
    List<EmployeeTaskDto> listOfAllInactiveTaskByEmployee(Long userId);
    List<Task> listOfAllInactiveTaskByDepartment(Long userId);
    //void addFileToTask(File file);
    //void addCommentToTask(File file)
    List<Task> getAllSubTaskToTask(Long taskId);
    void addSubTaskToTask(Task task, Long taskId);
    List<Task> getAllTemplateTask();
    void setTemplateTask(Long taskId);
    void setPeriodicTask(Long markId, Long taskId);
    public Task getById(Long id);

    void delete(Task task);

    Task save(Task task);

    Task create(Task task);

    List<Task> getAll(String value);

    List<Task> getAll();

    List<Task> getRootTasks(String s);

    List<Task> getChildTasks(Task task, String s);

    List<Task> getTasksByStatus(long statusId, LocalDateTime fromCreateDate, LocalDateTime toCreateDate,
                                LocalDateTime fromTermDate, LocalDateTime toTermDate);

    void addCreatedSubTaskToTaskAndInstructToUserThis(Task task, ListOfEmployees value);


}
