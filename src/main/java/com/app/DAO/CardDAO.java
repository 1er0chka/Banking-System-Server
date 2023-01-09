package com.app.DAO;

import com.app.model.Card;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class CardDAO implements DAO<Card> {
    private SessionFactory sessionFactory;

    public CardDAO() {
        Configuration configuration = new Configuration();
        this.sessionFactory = configuration.configure().buildSessionFactory();
    }

    @Override
    public void create(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(card);
            session.getTransaction().commit();
            session.close();
        }
    }

    @Override
    public Card read(int index) {
        ArrayList<Card> cards = readAll();
        for (Card card : cards) {
            if (card.getId() == index) {
                return card;
            }
        }
        return new Card();
    }

    @Override
    public ArrayList<Card> readAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ArrayList<Card> cards = (ArrayList<Card>) session.createQuery("FROM Card").list();
        session.getTransaction().commit();
        session.close();
        return cards;
    }

    @Override
    public void update(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(card);
            session.getTransaction().commit();
            session.close();
        }
    }

    @Override
    public void delete(Card card) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.remove(card);
        session.getTransaction().commit();
        session.close();
    }
}