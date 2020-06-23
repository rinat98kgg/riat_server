package com.riatServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode(of = {"id"})
public class User implements Serializable {

    //columns
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String name;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String firstName;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String lastName;

    @Column(updatable = true, nullable = true)
    private String patronymic;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String password;

    @NotEmpty
    @Column(updatable = true, nullable = true)
    private String telephone;

    @Column(updatable = false, insertable = false, nullable = false, name = "position_id")
    private long positionId;

    private boolean isEnabled;

    @Column(updatable = false, insertable = false, nullable = false, name = "fileName")
    private  String fileName;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position_id;

    @Column(updatable = false, nullable = false, name="create_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Column(name="update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Role.Status status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;
    //getter and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephon() {
        return telephone;
    }

    public void setTelephon(String telephon) {
        this.telephone = telephon;
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public Position getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Position position_id) {
        this.position_id = position_id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Role.Status getStatus() {
        return status;
    }

    public void setStatus(Role.Status status) {
        this.status = status;
    }

    public Set<Role> getRoles() {
        //roles.forEach(role -> System.out.println(role.getName()));
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getFullName(){
        return this.lastName + " " + this.firstName + " " + this.patronymic;
    }

    public String getGroup(){
        List<String> rolesName = new ArrayList<>();
        roles.forEach(role -> {
            rolesName.add(role.getName());
        });
        String joined;
        joined = String.join(", ", rolesName);
        return joined;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
