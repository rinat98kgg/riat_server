package com.riatServer.dto;

import com.riatServer.domain.Department;
import lombok.Data;

@Data
public class DepartmentListDto {
    private Long id;
    private String name;

    public static DepartmentListDto fromJson(Department department){
        DepartmentListDto departmentListDto = new DepartmentListDto();
        departmentListDto.setId(department.getId());
        departmentListDto.setName(department.getName());
        return departmentListDto;
    }


}