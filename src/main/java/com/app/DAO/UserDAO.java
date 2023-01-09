package com.app.DAO;

import com.app.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class UserDAO implements DAO<User> {
    private SessionFactory sessionFactory;

    public UserDAO() {
        Configuration configuration = new Configuration();
        this.sessionFactory = configuration.configure().buildSessionFactory();
    }

    @Override
    public void create(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public User read(int index) {
        ArrayList<User> users = readAll();
        for (User user : users) {
            if (user.getId() == index) {
                return user;
            }
        }
        return new User();
    }

    @Override
    public ArrayList<User> readAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ArrayList<User> users = (ArrayList<User>) session.createQuery("FROM User").list();
        session.getTransaction().commit();
        session.close();
        return users;
    }

    @Override
    public void update(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.remove(user);
        session.getTransaction().commit();
        session.close();
    }
}
