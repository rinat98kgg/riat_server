package com.riatServer.service.Impl;

import com.riatServer.domain.Department;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.DepartmentsRepo;
import com.riatServer.service.DepartmentService;
import com.riatServer.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService, EntityService<Department, Long> {
    @Autowired
    DepartmentsRepo departmentRepo;


    @Override
    public List<Department> getAll() {
        return  departmentRepo.findAll();
    }

//    @Override
//    public List<Department> listToAdd() {
//        return departmentRepo.selectListToAdd();
//    }

    @Override
    public Department getById(Long id) {
        return departmentRepo.findById(id).orElse(null);
    }

    @Override
    public List<Department> getAll(String value) {
        if(value == null || value.isEmpty()){
            return departmentRepo.findAll();
        } else {
            return departmentRepo.search(value);
        }
    }

    @Override
    public void delete(Department department) {
        departmentRepo.delete(department);
    }

    @Override
    public Department save(Department department) {
        department.setUpdateDate(LocalDateTime.now());
        return departmentRepo.save(department);
    }

    @Override
    public Department create(Department department) {
        department.setCreateDate(LocalDateTime.now());
        department.setUpdateDate(LocalDateTime.now());
        return departmentRepo.save(department);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        Department department = getById(id);
        departmentRepo.deleteById(id);
    }
}
