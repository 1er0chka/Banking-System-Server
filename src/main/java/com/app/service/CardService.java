package com.app.service;

import com.app.DAO.CardDAO;
import com.app.model.Card;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CardService implements Service<Card> {
    public Card findSender(Card card) {
        for (Card tempCard : getAll()) {
            if (card.getCardNumber().equals(tempCard.getCardNumber())) {
                if (card.getValidity().equals(tempCard.getValidity()) && card.getSecurityCode() == tempCard.getSecurityCode()) {
                    return tempCard;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public Card findRecipient(Card card) {
        for (Card tempCard : getAll()) {
            if (card.getCardNumber().equals(tempCard.getCardNumber())) {
                if (tempCard.getValidity().equals(card.getValidity())) {
                    return tempCard;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Card get(int id) {
        for (Card tempCard : getAll()) {
            if (tempCard.getId() == id) {
                return tempCard;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Card> getAll() {
        CardDAO cardDAO = new CardDAO();
        return cardDAO.readAll();
    }

    @Override
    public String add(Card card) throws NoSuchAlgorithmException {
        if (getAll() != null) {
            for (Card tempCard : getAll()) {
                if (tempCard.getCardNumber().equals(card.getCardNumber())) {
                    return "false";
                }
            }
        }
        CardDAO cardDAO = new CardDAO();
        cardDAO.create(card);
        return "true";
    }

    @Override
    public String update(Card card) {
        CardDAO cardDAO = new CardDAO();
        cardDAO.update(card);
        return null;
    }

    @Override
    public void delete(Card card) {
        CardDAO cardDAO = new CardDAO();
        cardDAO.delete(card);
    }
}
