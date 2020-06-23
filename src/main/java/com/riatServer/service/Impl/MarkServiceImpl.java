package com.riatServer.service.Impl;

import com.riatServer.domain.Mark;
import com.riatServer.domain.Mark;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.MarksRepo;
import com.riatServer.service.EntityService;
import com.riatServer.service.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarkServiceImpl implements MarkService, EntityService<Mark,Long> {
    @Autowired
    MarksRepo markRepo;
    @Override
    public List<Mark> getAll() {
        return  markRepo.findAll();
    }

    @Override
    public Mark getById(Long id) {
        return markRepo.findById(id).orElse(null);
    }

    @Override
    public Mark save(Mark mark) {
        mark.setUpdateDate(LocalDateTime.now());
        return markRepo.save(mark);
    }

    @Override
    public Mark create(Mark mark) {
        mark.setCreateDate(LocalDateTime.now());
        mark.setUpdateDate(LocalDateTime.now());
        return markRepo.save(mark);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        Mark mark = getById(id);
        markRepo.deleteById(id);
    }
}
