package com.riatServer.repo;

import com.riatServer.domain.ListOfEmployees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ListOfEmployeesRepo extends JpaRepository<ListOfEmployees, Long> {
    @Query("select u from ListOfEmployees u where u.userId = ?1 and u.active = ?2")
    List<ListOfEmployees> listOfAllActiveTaskByEmployee(Long userId, boolean active);


    @Query("select u from ListOfEmployees u where u.userId = ?1 and u.active = ?2 and u.taskId = ?3")
    ListOfEmployees activeTask(Long userId, boolean active, Long taskId);

    @Query("select u from ListOfEmployees u where u.taskId = ?1 and u.userId = ?2")
    ListOfEmployees changeTaskStatus(Long taskId, Long UserId);

    @Query("select u from ListOfEmployees u where u.userId = ?1")
    List<ListOfEmployees> statistic(Long UserId);

//    @Query("select l from ListOfEmployees l join User t on t.id = l.userId " +
//            "where lower(t.firstName) like lower(concat('%', :searchTerm, '%'))" +
//            " or lower(t.lastName) like lower(concat('%', :searchTerm, '%'))" +
//            " or lower(t.patronymic) like lower(concat('%', :searchTerm, '%'))" +
//            " order by l.createDate desc"
//    )
    @Query("select l from ListOfEmployees l join User t on t.id = l.userId " +
            "where lower(t.firstName) like lower(concat('%', :searchTerm, '%'))" +
            " or lower(t.lastName) like lower(concat('%', :searchTerm, '%'))" +
            " or lower(t.patronymic) like lower(concat('%', :searchTerm, '%'))" +
            " order by l.createDate desc"
    )
    List<ListOfEmployees> search(@Param("searchTerm") String searchTerm);



    @Query("SELECT t  FROM ListOfEmployees as t JOIN Task as ta on t.taskId = ta.id WHERE not EXISTS(SELECT ta FROM ListOfTask as l WHERE l.subtaskId = ta.id) and lower(ta.Name) like lower(concat('%', ?1, '%')) order by t.createDate desc"
    )
    List<ListOfEmployees> allRootTasks(String s);

    @Query("SELECT t FROM ListOfEmployees as t JOIN Task as ta on t.taskId = ta.id JOIN ListOfTask as l ON ta.id = l.subtaskId WHERE l.topId  = ?1 and lower(ta.Name) like lower(concat('%', ?2, '%')) order by t.createDate desc"
    )
    List<ListOfEmployees> getAllChildTasks(long id, String s);

    @Query("SELECT li FROM ListOfEmployees as li WHERE li.taskId = ?1 and li.userId = ?2")
    List<ListOfEmployees> checkIsInstructData(long id, long id1);



    @Query("SELECT t  FROM ListOfEmployees as t JOIN Task as ta on t.taskId = ta.id join User as u on t.userId = u.id WHERE u.name = ?2 and not EXISTS(SELECT ta FROM ListOfTask as l WHERE l.subtaskId = ta.id) and lower(ta.Name) like lower(concat('%', ?1, '%')) order by t.createDate desc"
    )
    List<ListOfEmployees> allRootTasksForCurrentUser(String s, String name);

    @Query("SELECT t FROM ListOfEmployees as t JOIN Task as ta on t.taskId = ta.id JOIN ListOfTask as l ON ta.id = l.subtaskId join User as u on t.userId = u.id WHERE u.name = ?3 and l.topId  = ?1 and lower(ta.Name) like lower(concat('%', ?2, '%')) order by t.createDate desc"
    )
    List<ListOfEmployees> getAllChildTasksForCurrentUser(long taskId, String s, String name);

    @Query("SELECT t  FROM ListOfEmployees as t JOIN Task as ta on t.taskId = ta.id join User as u on t.userId = u.id WHERE u.name = ?2 and t.taskStatusId = ?1 and not EXISTS(SELECT ta FROM ListOfTask as l WHERE l.subtaskId = ta.id) order by t.createDate desc"
    )
    List<ListOfEmployees> allRootTasksForCurrentUserByTaskStatus(long id, String name);

    @Query("SELECT t FROM ListOfEmployees as t JOIN Task as ta on t.taskId = ta.id JOIN ListOfTask as l ON ta.id = l.subtaskId join User as u on t.userId = u.id WHERE u.name = ?3 and t.taskStatusId = ?2 and l.topId  = ?1  order by t.createDate desc"
    )
    List<ListOfEmployees> getAllChildTasksForCurrentUserByTaskStatus(long taskId, long id, String name);


//    @Query("SELECT us.lastName, us.firstName, ta.Name, li.createDate, ta.termDate FROM ListOfEmployees as li" +
//            " JOIN Task as ta ON li.taskId = ta.id JOIN User as us ON li.userId = us.id " +
//            "WHERE li.taskStatusId = ?1 and (?2 < ta.createDate and ta.termDate > ?3)")
//    List<ListOfEmployees> getAllTaskByStatus(Long lo, String str1, String str2);

//    @Query("SELECT us.lastName, us.firstName, ta.Name, li.createDate, ta.termDate FROM ListOfEmployees as li" +
//            " JOIN Task as ta ON li.taskId = ta.id JOIN User as us ON li.userId = us.id " +
//            "WHERE li.taskStatusId = ?1 and (?2 < ta.createDate and ta.termDate > ?3)")

    @Query(value = "SELECT li FROM  ListOfEmployees as li join User as u on li.userId = u.id WHERE li.taskStatusId = ?1 and (li.createDate >= ?2 and li.createDate <= ?3) and (li.updateDate >= ?4 and li.updateDate <= ?5) and li.userId in (select u.id from User as u where u.name in ?6)")
    List<ListOfEmployees> getTheUserTasksByStatus(long id, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromUpdateDate, LocalDateTime toUpdateDate, String name);



}