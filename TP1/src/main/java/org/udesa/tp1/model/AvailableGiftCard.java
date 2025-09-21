package org.udesa.tp1.model;

import java.util.List;

public class AvailableGiftCard extends GiftCard {
    public static String invalidBalance = "Balance must be greater than 0";
    public static String giftCardHasNotBeenClaimed = "Gift card has not been claimed";

    public AvailableGiftCard( int balance ) {
        assertBalance( balance );
        this.balance = balance;
    }

    public GiftCard claim( String ownerName ) { return new ClaimedGiftCard( ownerName, balance ); }


    public GiftCard charge( String userName, int price, Clock clock ) {
        throw new RuntimeException( giftCardHasNotBeenClaimed );
    }

    public List<Expense> getExpenses( String userName ) {
        throw new RuntimeException( giftCardHasNotBeenClaimed );
    }

    public int getBalance( String userName ) { throw new RuntimeException( giftCardHasNotBeenClaimed ); }

    private void assertBalance( int balance ) {
        if ( balance <= 0 ) throw new RuntimeException( invalidBalance );
    }
}
