package com.applaudo.login.service;

import com.applaudo.login.model.entity.User;

import java.util.List;

public interface UserService {

    List<User> listUsers();
    User saveUser(User purchase);
    User findUserById(Long id);
    User updateUser(User user);
    void deleteUser(Long id);

    User findUserByEmail(String email);
}
