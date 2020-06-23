package com.riatServer.service.Impl;

import com.riatServer.domain.PeriodicTask;
import com.riatServer.domain.PeriodicTask;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.PeriodicTasksRepo;
import com.riatServer.service.EntityService;
import com.riatServer.service.PeriodicTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PeriodicTaskServiceImpl implements PeriodicTaskService, EntityService<PeriodicTask, Long> {
    @Autowired
    PeriodicTasksRepo periodicTaskRepo;

    @Override
    public List<PeriodicTask> getAll() {
        return  periodicTaskRepo.findAll();
    }

    @Override
    public PeriodicTask getById(Long id) {
        return periodicTaskRepo.findById(id).orElse(null);
    }

    @Override
    public PeriodicTask save(PeriodicTask periodicTask) {
        periodicTask.setUpdateDate(LocalDateTime.now());
        return periodicTaskRepo.save(periodicTask);
    }

    @Override
    public PeriodicTask create(PeriodicTask periodicTask) {
        periodicTask.setCreateDate(LocalDateTime.now());
        periodicTask.setUpdateDate(LocalDateTime.now());
        return periodicTaskRepo.save(periodicTask);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        PeriodicTask periodicTask = getById(id);
        periodicTaskRepo.deleteById(id);
    }
}
