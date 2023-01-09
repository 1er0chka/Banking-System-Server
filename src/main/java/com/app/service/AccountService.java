package com.app.service;

import com.app.DAO.AccountDAO;
import com.app.DAO.UserDAO;
import com.app.model.Account;
import com.app.model.User;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AccountService implements Service<Account> {
    public Account getByNumber(String number) {
        for (Account tempAccount : getAll()) {
            if (tempAccount.getAccountNumber().equals(number)) {
                return tempAccount;
            }
        }
        return null;
    }

    @Override
    public Account get(int id) {
        for (Account tempAccount : getAll()) {
            if (tempAccount.getId() == id) {
                return tempAccount;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Account> getAll() {
        AccountDAO accountDAO = new AccountDAO();
        return accountDAO.readAll();
    }

    @Override
    public String add(Account account) throws NoSuchAlgorithmException {
        for (Account tempAccount : getAll()) {
            if (tempAccount.getAccountNumber().equals(account.getAccountNumber())) {
                return "number";
            }
        }
        UserService userService = new UserService();
        System.out.println(account.getUser().getId());
        User user = userService.get(account.getUser().getId());
        if (user.getAccounts() != null) {
            for (Account tempAccountDTO : user.getAccounts()) {
                if (tempAccountDTO.getAccountName().equals(account.getAccountName())) {
                    return "name";
                }
            }
        }
        AccountDAO accountDAO = new AccountDAO();
        account.setUser(user);
        accountDAO.create(account);
        return "true";
    }

    public void updateAmount(Account account) {
        AccountDAO accountDAO = new AccountDAO();
        accountDAO.update(account);
    }

    @Override
    public String update(Account account) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.read(account.getUser().getId());
        if (user.getAccounts() != null) {
            for (Account tempAccountDTO : user.getAccounts()) {
                if (tempAccountDTO.getAccountName().equals(account.getAccountName())) {
                    return "false";
                }
            }
        }
        AccountDAO accountDAO = new AccountDAO();
        accountDAO.update(account);
        return "true";
    }

    @Override
    public void delete(Account account) {
        AccountDAO accountDAO = new AccountDAO();
        accountDAO.delete(account);
    }
}
