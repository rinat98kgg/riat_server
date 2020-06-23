package com.riatServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PeriodicTask")
@ToString
@EqualsAndHashCode(of = {"id"})
public class PeriodicTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(updatable = true, nullable = true)
    private  boolean active;

    @Column(updatable = false, insertable = false, nullable = false, name = "Task_id")
    private  long TaskId;

    @ManyToOne
    @JoinColumn(name = "Task_id")
    private com.riatServer.domain.Task Task;

    @Column(updatable = false, insertable = false, nullable = false, name = "Mark_id")
    private  long MarkId;

    @ManyToOne
    @JoinColumn(name = "Mark_id")
    private com.riatServer.domain.Mark Mark;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getTaskId() {
        return TaskId;
    }

    public void setTaskId(long taskId) {
        TaskId = taskId;
    }

    public com.riatServer.domain.Task getTask() {
        return Task;
    }

    public void setTask(com.riatServer.domain.Task task) {
        Task = task;
    }

    public long getMarkId() {
        return MarkId;
    }

    public void setMarkId(long markId) {
        MarkId = markId;
    }

    public com.riatServer.domain.Mark getMark() {
        return Mark;
    }

    public void setMark(com.riatServer.domain.Mark mark) {
        Mark = mark;
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
}
