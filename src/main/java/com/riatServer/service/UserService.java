package com.riatServer.service;

import com.riatServer.domain.User;

import java.util.List;

public interface UserService {
    User register(User user);

    List<User> getAll(String value);

    User save(User user);
    User create(User user);

    void delete(User user);

    List<User> getAll();

    User getByName(String name);

    List<User> getListById(Long departmentId);
}
