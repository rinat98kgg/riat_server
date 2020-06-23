package com.riatServer.repo;

import com.riatServer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public interface UsersRepo extends JpaRepository<User, Long> {
    User findByName(String Name);

    @Query("select u from User u " +
            "where u.name <> :allTerm and lower(u.firstName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(u.lastName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(u.patronymic) like lower(concat('%', :searchTerm, '%')) " +
            "order by u.createDate asc"
    )
    List<User> search(@Param("searchTerm") String searchTerm, @Param("allTerm") String allTerm);

    @Query("select u from User u " +
            "where u.name <> :allTerm order by u.createDate asc"
    )
    List<User> searchAll(@Param("allTerm") String allTerm);

    @Query("SELECT u from User as u where  not EXISTS (select ds from DepartmentStaff as ds where ds.departmentId = ?1 and ds.userId = u.id) order by u.createDate desc"
    )
    List<User> selectListToAddDepartment(Long departmentId);
}