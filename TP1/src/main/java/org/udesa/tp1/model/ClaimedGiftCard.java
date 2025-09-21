package org.udesa.tp1.model;

import java.util.ArrayList;
import java.util.List;

public class ClaimedGiftCard extends GiftCard{
    public static String cardAlreadyClaimed = "Card already claimed";
    public static String invalidPriceCharge = "Price charge cannot be negative";
    public static String insufficientFunds = "Insufficient funds";
    public static String cardDoesNotBelongToUser = "Card does not belong to user";

    private List<Expense> expenses = new ArrayList<>( );

    public ClaimedGiftCard( String ownerName, int balance ) {
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public GiftCard claim( String userName ) { throw new RuntimeException( cardAlreadyClaimed ); }

    public GiftCard charge( String userName, int price, Clock clock ) {
        assertOwner( userName );
        assertPrice( price );
        this.balance -= price;
        expenses.add( new Expense( price, clock ) );
        return this;
    }

    public List<Expense> getExpenses( String userName ) {
        assertOwner( userName );
        return expenses;
    }

    public int getBalance( String userName ) {
        assertOwner( userName );
        return balance;
    }

    private void assertOwner( String userName ) {
        if ( !this.ownerName.equals( userName ) ) throw new RuntimeException( cardDoesNotBelongToUser );
    }

    private void assertPrice( int price ) {
        if ( price < 0 )            throw new RuntimeException( invalidPriceCharge );
        if ( price > this.balance ) throw new RuntimeException( insufficientFunds  );
    }

}
