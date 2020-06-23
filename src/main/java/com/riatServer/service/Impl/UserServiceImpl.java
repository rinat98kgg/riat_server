package com.riatServer.service.Impl;


import com.riatServer.domain.Role;
import com.riatServer.domain.User;
import com.riatServer.exception.ServiceException;
import com.riatServer.repo.RolesRepo;
import com.riatServer.repo.UsersRepo;
import com.riatServer.service.EntityService;
import com.riatServer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements  UserService, EntityService<User, Long> {

    @Autowired
    private UsersRepo userRepo;
    @Autowired
    private RolesRepo roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return  userRepo.findAll();
    }

    @Override
    public User getByName(String name) {
        return userRepo.findByName(name);
    }

    @Override
    public List<User> getListById(Long departmentId) {
        return userRepo.selectListToAddDepartment(departmentId);
    }


    @Override
    public List<User> getAll(String value) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(value == null || value.isEmpty()){
            return userRepo.searchAll(authentication.getName());
        } else {
            return userRepo.search(value, authentication.getName());
        }
    }

    @Override
    public User getById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User save(User user) {
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUpdateDate(LocalDateTime.now());
        System.out.println("saved");
        return userRepo.save(user);
    }

    @Override
    public void delete(User user) {
        userRepo.delete(user);
    }

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        System.out.println("created");
        return userRepo.save(user);
    }

    @Override
    public void delete(Long id) throws IOException, ServiceException {
        User user = getById(id);
        userRepo.deleteById(id);
    }

    @Override
    public User register(User user) {
        Role roleUser = roleRepository.findByName("ROLE_USER");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Role.Status.ACTIVE);

        User registeredUser = userRepo.save(user);

        System.out.println("IN register - user: {} successfully registered" + registeredUser);

        return registeredUser;
    }
}
