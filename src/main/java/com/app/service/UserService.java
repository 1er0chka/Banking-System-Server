package com.app.service;

import com.app.DAO.UserDAO;
import com.app.cryptography.Hash;
import com.app.model.User;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserService implements Service<User> {

    public User authorization(User user) throws NoSuchAlgorithmException {
        for (User tempUser : getAll()) {
            if (tempUser.getLogin().equals(user.getLogin()) && (Hash.hashUser(user).getPassword()).equals(tempUser.getPassword())) {
                user = tempUser;
                return user;
            }
        }
        return null;
    }

    @Override
    public User get(int id) {
        for (User tempUser : getAll()) {
            if (tempUser.getId() == id) {
                return tempUser;
            }
        }
        return null;
    }

    @Override
    public ArrayList<User> getAll() {
        UserDAO userDAO = new UserDAO();
        return userDAO.readAll();
    }

    @Override
    public String add(User user) throws NoSuchAlgorithmException {
        for (User tempUser : getAll()) {
            if ((tempUser.getSurname().equals(user.getSurname()) && tempUser.getName().equals(user.getName()) && tempUser.getSecondName().equals(user.getSecondName()))) {
                return "fio";
            } else if (tempUser.getLogin().equals(user.getLogin())) {
                return "login";
            }
        }
        UserDAO userDAO = new UserDAO();
        userDAO.create(Hash.hashUser(user));
        return "true";
    }

    @Override
    public String update(User user) {
        UserDAO userDAO = new UserDAO();
        userDAO.update(user);
        return null;
    }

    @Override
    public void delete(User user) {
        UserDAO userDAO = new UserDAO();
        userDAO.delete(user);
    }
}