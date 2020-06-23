package com.riatServer.repo;

import com.riatServer.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TasksRepo extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.templateTask = ?1 order by t.createDate asc")
    List<Task> allTemplateTask(boolean active);

    @Query("select t from Task t " +
            "where lower(t.Name) like lower(concat('%', :searchTerm, '%')) order by t.createDate desc"
    )
    List<Task> search(@Param("searchTerm") String searchTerm);

    @Query("SELECT t  FROM Task as t WHERE not EXISTS(SELECT t FROM ListOfTask as l WHERE l.subtaskId = t.id) " +
            "and lower(t.Name) like lower(concat('%', ?1, '%')) order by t.createDate desc"
    )
    List<Task> allRootTasks(String s);

    @Query("SELECT t  FROM Task as t JOIN ListOfTask as l ON  t.id = l.subtaskId WHERE l.topId  = ?1 " +
            "and lower(t.Name) like lower(concat('%', ?2, '%')) order by t.createDate desc"
    )
    List<Task> getAllChildTasks(Long TopId, String s);

    @Query("SELECT DISTINCT ta FROM ListOfEmployees as li" +
            " JOIN Task as ta ON li.taskId = ta.id " +
            "WHERE li.taskStatusId = ?1 and (ta.createDate >= ?2 and ta.createDate <= ?3) and (ta.termDate >= ?4 and ta.termDate <= ?5) order by ta.createDate asc")
    List<Task> getTaskByStatus(long statusId, LocalDateTime fromCreateDate, LocalDateTime toCreateDate,
                               LocalDateTime fromTermDate, LocalDateTime toTermDate);


}
