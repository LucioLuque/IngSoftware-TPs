package org.udesa.tp1.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facade {
    public static String userNameOrPasswordNotValid = "User name or password not valid";
    public static String invalidToken = "Invalid token";
    public static String invalidGiftCard = "Invalid gift card";


    private Map<String, User> validUsers;
    private Map<Integer, String> validTokens = new HashMap<>();
    private Map<String, GiftCard> validCards;
    private int nextId = 1;


    public Facade(Map<String, User> validUsers, Map<String, GiftCard> validCards) {
        this.validUsers = validUsers;
        this.validCards = validCards;
    }

    public int createTokenFor(String userName, String password) {
        assertPassword(userName, password);
        int id = nextId++;
        validTokens.put(id, userName);
        return id;
    }

    private void assertPassword(String userName, String password) {
        if (!validUsers.containsKey(userName) || !validUsers.get(userName).validatePassword(password)) {throw new RuntimeException(userNameOrPasswordNotValid);}
    }

//    private Map<String, User> validUsers;
//    private Map<Integer, String> validTokens = new HashMap<>();
//    private Map<String, GiftCard> validCards;

    public Facade claimGiftCard(int token, String cardName) {
        assertToken(token);
        assertGiftCard(cardName);
        //assertUser
        getUser(token).addGiftCard(cardName, new GiftCard(validCards.get(cardName)));
        return this;
    }

    private void assertGiftCard(String name) {
        if (!validCards.containsKey(name)) throw new RuntimeException(invalidGiftCard);
    }

    private void assertToken(int token) {
        if (!validTokens.containsKey(token)) throw new RuntimeException(invalidToken);
    }

    public int requestBalance(int token, String cardName) {
        assertToken(token);
        assertGiftCard(cardName);
        return getUser(token).getBalance(cardName);
    }

    private User getUser(int token) {
        return validUsers.get(validTokens.get(token));
    }
}
