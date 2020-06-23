package com.riatServer.service;

import com.riatServer.domain.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    void delete(TaskStatus taskStatus);

    TaskStatus save(TaskStatus taskStatus);

    TaskStatus create(TaskStatus taskStatus);

    List<TaskStatus> getAll(String value);

    List<TaskStatus> getAll();

    TaskStatus getByName(String name);
}
