package com.app.DAO;

import com.app.model.Operation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class OperationDAO implements DAO<Operation> {
    private SessionFactory sessionFactory;

    public OperationDAO() {
        Configuration configuration = new Configuration();
        this.sessionFactory = configuration.configure().buildSessionFactory();
    }

    @Override
    public void create(Operation operation) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(operation);
            session.getTransaction().commit();
            session.close();
        }
    }

    @Override
    public Operation read(int index) {
        ArrayList<Operation> operations = readAll();
        for (Operation operation : operations) {
            if (operation.getId() == index) {
                return operation;
            }
        }
        return new Operation();
    }

    @Override
    public ArrayList<Operation> readAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ArrayList<Operation> operations = (ArrayList<Operation>) session.createQuery("FROM Operation").list();
        session.getTransaction().commit();
        session.close();
        return operations;
    }

    @Override
    public void update(Operation operation) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(operation);
            session.getTransaction().commit();
            session.close();
        }
    }

    @Override
    public void delete(Operation operation) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(operation);
            session.getTransaction().commit();
            session.close();
        }
    }
}