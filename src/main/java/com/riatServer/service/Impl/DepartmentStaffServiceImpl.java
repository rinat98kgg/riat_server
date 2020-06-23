package com.riatServer.service.Impl;

import com.riatServer.domain.Department;
import com.riatServer.domain.DepartmentStaff;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.DepartmentStaffsRepo;
import com.riatServer.repo.DepartmentsRepo;
import com.riatServer.service.DepartmentStaffService;
import com.riatServer.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentStaffServiceImpl implements DepartmentStaffService, EntityService<DepartmentStaff, Long> {
    @Autowired
    DepartmentsRepo departmentsRepo;
    @Autowired
    DepartmentStaffsRepo departmentStaffsRepo;

    @Override
    public List<DepartmentStaff> getAll() {
        return departmentStaffsRepo.findAll();
    }

    @Override
    public DepartmentStaff getById(Long id) {
        return departmentStaffsRepo.findById(id).orElse(null);
    }

    @Override
    public DepartmentStaff getByUserId(Long id) {
        return departmentStaffsRepo.findByUserId(id);
    }

    @Override
    public List<DepartmentStaff> getAll(String value) {
        if(value == null || value.isEmpty()){
            return departmentStaffsRepo.findAll();
        } else {
            return departmentStaffsRepo.search(value);
        }
    }

    @Override
    public void delete(DepartmentStaff departmentStaff) {
        departmentStaffsRepo.delete(departmentStaff);
    }

    @Override
    public DepartmentStaff save(DepartmentStaff departmentStaff) {
        departmentStaff.setUpdateDate(LocalDateTime.now());
        return departmentStaffsRepo.save(departmentStaff);
    }

    @Override
    public DepartmentStaff create(DepartmentStaff departmentStaff) {
        Department department = departmentsRepo.findById(departmentStaff.getDepartmentId()).orElse(null);
        departmentStaff.setDepartment_id(department);
        departmentStaff.setUpdateDate(LocalDateTime.now());
        departmentStaff.setCreateDate(LocalDateTime.now());
        return departmentStaffsRepo.save(departmentStaff);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        DepartmentStaff departmentStaff = getById(id);
        departmentStaffsRepo.delete(departmentStaff);
    }

    @Override
    public DepartmentStaff create2(DepartmentStaff departmentStaff) {
        departmentStaff.setUpdateDate(LocalDateTime.now());
        departmentStaff.setCreateDate(LocalDateTime.now());
        return departmentStaffsRepo.save(departmentStaff);
    }
}
