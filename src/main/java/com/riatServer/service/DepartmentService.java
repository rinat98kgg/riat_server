package com.riatServer.service;

import com.riatServer.domain.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getAll(String value);

    void delete(Department department);

    Department save(Department department);

    Department create(Department department);

    List<Department> getAll();

//    List<Department> listToAdd();
}
