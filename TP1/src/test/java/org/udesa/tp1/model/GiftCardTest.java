package org.udesa.tp1.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCardTest implements AssertHelpers{
    private String validOwner   = "Lucio";
    private String invalidOwner = "Emilio";
    private Clock clock = new Clock( );

    @Test public void test01CanNotCreateAvailableGiftCardWithZeroBalance( ) {
        testInvalidBalance( 0 );
    }

    @Test public void test02CanNotCreateAvailableGiftCardWithNegativeBalance( ) {
        testInvalidBalance( -1 );
    }

    @Test public void test03CanNotOperateOnAvailableGiftCard( ) {
        testGiftCardNotOwnedByUser( newValidAvailableGiftCard( ), validOwner, AvailableGiftCard.giftCardHasNotBeenClaimed );
    }

    @Test public void test04NewClaimedGiftCardHasNoExpenses( ) {
        assertTrue( newValidClaimedGiftCard( ).getExpenses( validOwner ).isEmpty( ) );
    }

    @Test public void test05CanNotClaimClaimedGiftCard( ) {
        assertThrowsLike( ( ) -> newValidClaimedGiftCard( ).claim( invalidOwner ), ClaimedGiftCard.cardAlreadyClaimed );
    }

    @Test public void test06CanNotApplyNegativeCharge( ) {
        testInvalidPrice( -1, ClaimedGiftCard.invalidPriceCharge );
    }

    @Test public void test07CanNotChargeOverBalance( ) {
        testInvalidPrice( 43, ClaimedGiftCard.insufficientFunds );
    }

    @Test public void test08CanChargePriceZero( ) {
        chargeClaimedGiftCard( 0 );
    }

    @Test public void test09ChargingCreatesExpense( ) {
        Clock clock  = new Clock( ) {
                Iterator<LocalDateTime> it = List.of( LocalDateTime.now( ),
                                                      LocalDateTime.now( ) )
                                             .iterator( );

                public LocalDateTime now( ) {
                    return it.next( );
                }
            };

        List<Expense> expenses = chargeClaimedGiftCard(5, clock ).getExpenses( validOwner );
        assertFalse(  expenses.isEmpty( ) );
        assertEquals( expenses.getFirst( ).getValue( ), 5 );
        assertEquals( expenses.getFirst( ).getTime( ), clock.now( ) );
    }

    @Test public void test10CanNotOperateOnUnownedGiftCard( ) {
        testGiftCardNotOwnedByUser( newValidClaimedGiftCard( ), invalidOwner, ClaimedGiftCard.cardDoesNotBelongToUser );
    }

    private void testInvalidBalance( int balance ) {
        assertThrowsLike( ( ) -> newAvailableGiftCard( balance ), AvailableGiftCard.invalidBalance );
    }

    private void testInvalidPrice( int price, String message ) {
        assertThrowsLike( ( ) -> chargeClaimedGiftCard( price ), message );
    }

    private GiftCard chargeClaimedGiftCard( int price, Clock clock ) {
        return newValidClaimedGiftCard( ).charge( validOwner, price, clock );
    }

    private GiftCard chargeClaimedGiftCard( int price ) {
        return chargeClaimedGiftCard( price, clock);
    }

    private void testGiftCardNotOwnedByUser( GiftCard giftCard, String userName, String message ) {
        assertThrowsLike( ( ) -> giftCard.charge( userName, 5, clock ), message );
        assertThrowsLike( ( ) -> giftCard.getExpenses( userName ), message );
        assertThrowsLike( ( ) -> giftCard.getBalance( userName ), message );
    }


    private GiftCard newValidAvailableGiftCard( ) {
        return newAvailableGiftCard(42 );
    }

    private GiftCard newAvailableGiftCard( int balance ) {
        return new AvailableGiftCard( balance );
    }

    private GiftCard newValidClaimedGiftCard( ) { return newValidAvailableGiftCard( ).claim( validOwner ); }
}
