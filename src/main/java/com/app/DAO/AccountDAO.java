package com.app.DAO;

import com.app.model.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class AccountDAO implements DAO<Account> {
    private SessionFactory sessionFactory;

    public AccountDAO() {
        Configuration configuration = new Configuration();
        this.sessionFactory = configuration.configure().buildSessionFactory();
    }

    @Override
    public void create(Account account) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(account);
            session.getTransaction().commit();
            session.close();
        }
    }

    @Override
    public Account read(int index) {
        ArrayList<Account> accounts = readAll();
        for (Account account : accounts) {
            if (account.getId() == index) {
                return account;
            }
        }
        return new Account();
    }

    @Override
    public ArrayList<Account> readAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ArrayList<Account> accounts = (ArrayList<Account>) session.createQuery("FROM Account").list();
        session.getTransaction().commit();
        session.close();
        return accounts;
    }

    @Override
    public void update(Account account) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(account);
            session.getTransaction().commit();
            session.close();
        }
    }

    @Override
    public void delete(Account account) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(account);
            session.getTransaction().commit();
            session.close();
        }
    }
}