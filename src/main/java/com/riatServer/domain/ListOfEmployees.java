package com.riatServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "listOfEmployees")
@ToString
@EqualsAndHashCode(of = {"id"})
public class ListOfEmployees implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(updatable = false, insertable = false, nullable = false, name = "task_id")
    private  long taskId;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task_id;

    @Column(updatable = false, insertable = false, nullable = false, name = "user_id")
    private  long userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    @Column(updatable = false, insertable = false, nullable = false, name = "owner_id")
    private  long ownerId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner_id;

    @Column(updatable = true, nullable = true)
    private  boolean active;

    @Column(updatable = false, insertable = false, nullable = false, name = "taskStatus_id")
    private  long taskStatusId;

    @ManyToOne
    @JoinColumn(name = "taskStatus_id")
    private TaskStatus taskStatus_id;

    @Column(updatable = false, nullable = false, name="create_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Column(name="update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    //getters and setter

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public Task getTask_id() {
        return task_id;
    }

    public void setTask_id(Task task_id) {
        this.task_id = task_id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public long getTaskStatusId() {
        return taskStatusId;
    }

    public void setTaskStatusId(long taskStatusId) {
        this.taskStatusId = taskStatusId;
    }

    public TaskStatus getTaskStatus_id() {
        return taskStatus_id;
    }

    public void setTaskStatus_id(TaskStatus taskStatus_id) {
        this.taskStatus_id = taskStatus_id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public User getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(User owner_id) {
        this.owner_id = owner_id;
    }
}
