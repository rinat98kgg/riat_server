package com.riatServer.dto;

import com.riatServer.domain.DepartmentStaff;
import com.riatServer.domain.User;
import lombok.Data;

@Data
public class EmployeeListDto {
    private Long id;
    private String name;
    private Long orderId;

    public static EmployeeListDto fromJson(User department, DepartmentStaff departmentStaff){
        EmployeeListDto departmentListDto = new EmployeeListDto();
        departmentListDto.setId(department.getId());
        departmentListDto.setName(department.getName());
        departmentListDto.setOrderId(departmentStaff.getDepartmentId());
        return departmentListDto;
    }


}