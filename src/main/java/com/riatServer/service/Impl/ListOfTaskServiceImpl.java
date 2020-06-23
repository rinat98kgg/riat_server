package com.riatServer.service.Impl;

import com.riatServer.domain.ListOfTask;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.ListOfTasksRepo;
import com.riatServer.service.EntityService;
import com.riatServer.service.ListOfTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListOfTaskServiceImpl implements ListOfTaskService, EntityService<ListOfTask, Long> {
    @Autowired
    ListOfTasksRepo listOfTaskRepo;
    

    @Override
    public List<ListOfTask> getAll() {
        return  listOfTaskRepo.findAll();
    }

    @Override
    public ListOfTask getById(Long id) {
        return listOfTaskRepo.findById(id).orElse(null);
    }

    @Override
    public ListOfTask save(ListOfTask listOfTask) {
        listOfTask.setUpdateDate(LocalDateTime.now());
        return listOfTaskRepo.save(listOfTask);
    }

    @Override
    public ListOfTask create(ListOfTask listOfTask) {
        listOfTask.setCreateDate(LocalDateTime.now());
        listOfTask.setUpdateDate(LocalDateTime.now());
        return listOfTaskRepo.save(listOfTask);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        ListOfTask listOfTask = getById(id);
        listOfTaskRepo.deleteById(id);
    }

}
