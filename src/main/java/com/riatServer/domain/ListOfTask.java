package com.riatServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "listOfTasks")
@ToString
@EqualsAndHashCode(of = {"id"})
public class ListOfTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(updatable = false, insertable = false, nullable = false, name = "top_id")
    private  long topId;

    @ManyToOne
    @JoinColumn(name = "top_id")
    private Task topTask;

    @Column(updatable = false, insertable = false, nullable = false, name = "subtask_id")
    private  long subtaskId;

    @ManyToOne
    @JoinColumn(name = "subtask_id")
    private Task subtask;


    @Column(name="create_date")
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

    public long getTopId() {
        return topId;
    }

    public void setTopId(long topId) {
        this.topId = topId;
    }

    public Task getTopTask() {
        return topTask;
    }

    public void setTopTask(Task topTask) {
        this.topTask = topTask;
    }

    public long getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(long subtaskId) {
        this.subtaskId = subtaskId;
    }

    public Task getSubtask() {
        return subtask;
    }

    public void setSubtask(Task subtask) {
        this.subtask = subtask;
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
