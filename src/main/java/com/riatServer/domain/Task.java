package com.riatServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@ToString
@EqualsAndHashCode(of = {"id"})
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String Name;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String description;

    @Column(updatable = true, nullable = true)
    private boolean templateTask;

    @Column(updatable = true, nullable = true)
    private float procent;

    @Column(name="term_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime termDate;

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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTerm() {
        return termDate;
    }

    public void setTerm(LocalDateTime term) {
        this.termDate = term;
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

    public boolean isTemplateTask() {
        return templateTask;
    }

    public void setTemplateTask(boolean templateTask) {
        this.templateTask = templateTask;
    }

    public LocalDateTime getTermDate() {
        return termDate;
    }

    public void setTermDate(LocalDateTime termDate) {
        this.termDate = termDate;
    }

    public float getProcent() {
        return procent;
    }

    public void setProcent(float procent) {
        this.procent = procent;
    }


}