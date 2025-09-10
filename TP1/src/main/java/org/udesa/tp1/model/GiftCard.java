package org.udesa.tp1.model;

public class GiftCard {
    private int balance;
    private String cardName;


    public GiftCard(String name, int balance) {
        this.balance = balance;
        this.cardName = name;
    }

    public GiftCard(GiftCard giftCard) {
        //hacer!
        this.balance = giftCard.getBalance();
        this.cardName = giftCard.getName();
    }

    public String getName( ) { return cardName; }
    public int getBalance( ) { return balance;  }

}
