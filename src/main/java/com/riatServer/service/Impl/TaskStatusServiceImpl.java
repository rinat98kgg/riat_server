package com.riatServer.service.Impl;

import com.riatServer.domain.TaskStatus;
import com.riatServer.domain.TaskStatus;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.TaskStatusRepo;
import com.riatServer.service.EntityService;
import com.riatServer.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskStatusServiceImpl implements TaskStatusService, EntityService<TaskStatus, Long> {
    @Autowired
    TaskStatusRepo taskStatusRepo;

    @Override
    public List<TaskStatus> getAll() {
        return  taskStatusRepo.findAll();
    }

    @Override
    public TaskStatus getByName(String name) {
        return taskStatusRepo.findByStatus(name);
    }

    @Override
    public TaskStatus getById(Long id) {
        return taskStatusRepo.findById(id).orElse(null);
    }

    @Override
    public void delete(TaskStatus taskStatus) {
        taskStatusRepo.delete(taskStatus);
    }

    @Override
    public TaskStatus save(TaskStatus taskStatus) {
        taskStatus.setUpdateDate(LocalDateTime.now());
        return taskStatusRepo.save(taskStatus);
    }

    @Override
    public TaskStatus create(TaskStatus taskStatus) {
        taskStatus.setCreateDate(LocalDateTime.now());
        taskStatus.setUpdateDate(LocalDateTime.now());
        return taskStatusRepo.save(taskStatus);
    }

    @Override
    public List<TaskStatus> getAll(String value) {
        if(value == null || value.isEmpty()){
            return taskStatusRepo.findAll();
        } else {
            return taskStatusRepo.search(value);
        }
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        TaskStatus taskStatus = getById(id);
        taskStatusRepo.deleteById(id);
    }
}
