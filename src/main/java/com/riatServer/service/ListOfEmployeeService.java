package com.riatServer.service;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.TaskStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface ListOfEmployeeService {
    ListOfEmployees taskInfo(Long userId, boolean active, Long TaskId);

    void delete(ListOfEmployees listOfEmployees);

    ListOfEmployees save(ListOfEmployees listOfEmployees);

    ListOfEmployees create(ListOfEmployees listOfEmployees);

    ListOfEmployees create2(ListOfEmployees listOfEmployees);

    List<ListOfEmployees> getAll(String value);

    List<ListOfEmployees> getRootTasks(String s);

    List<ListOfEmployees> getChildTasks(ListOfEmployees listOfEmployees, String s);

    List<ListOfEmployees> checkUniqueInstruct(long id, long id1);

    List<ListOfEmployees> getRootTasksForCurrentUser(String value);

    List<ListOfEmployees> getChildTasksForCurrentUser(ListOfEmployees listOfEmployees, String value);

    List<ListOfEmployees> getRootTasksForCurrentUserByTaskStatus(TaskStatus taskStatus);

    List<ListOfEmployees> getChildTasksForCurrentUserByTaskStatus(ListOfEmployees listOfEmployees, TaskStatus taskStatus);

    List<ListOfEmployees> getTheUserTasksByStatus(long id, LocalDateTime value, LocalDateTime value1, LocalDateTime value2, LocalDateTime value3);

//    List<TasksForReportDto> getTasksByStatus(Long lo, String str1, String str2);
}
