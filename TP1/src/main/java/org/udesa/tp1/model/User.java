package org.udesa.tp1.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName;
    private String userPassword;
    private Map<String, GiftCard> cards =  new HashMap<>();

    public User(String name, String password) {
        this.userName = name;
        this.userPassword = password;
    }

    public boolean validatePassword(String password) {
        return this.userPassword.equals(password);
    }

    public void addGiftCard(String cardName, GiftCard giftCard) {
        this.cards.put(cardName, giftCard);
    }

    public int getBalance(String cardName) {
        return this.cards.get(cardName).getBalance();
    }
}
