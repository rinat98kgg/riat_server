package com.riatServer.service;

import com.riatServer.domain.DepartmentStaff;

import java.util.List;

public interface DepartmentStaffService {
    List<DepartmentStaff> getAll(String value);

    void delete(DepartmentStaff departmentStaff);

    DepartmentStaff save(DepartmentStaff departmentStaff);

    DepartmentStaff create(DepartmentStaff departmentStaff);

    DepartmentStaff create2(DepartmentStaff departmentStaff);

    DepartmentStaff getByUserId(Long id);
}
