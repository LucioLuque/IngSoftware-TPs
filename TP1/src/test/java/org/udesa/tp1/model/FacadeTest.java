package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FacadeTest implements AssertHelpers {
    public static final String validGiftCardNameOne = "G1";
    public static final String validGiftCardNameTwo = "G2";
    public static final String invalidGiftCardName = "G3";

    public static final String validMerchantKey = "M1";
    public static final String validUserNameOne = "Lucio";
    public static final String validUserNameTwo = "Teo";

    Facade facade;
    @BeforeEach public void  beforeEach( ) {
        facade = newFacade( );
    }

    @Test public void test01UserNotValid( ) {
        assertThrowsLike( ( ) -> facade.createTokenFor( "John", "passLucio" ),
                          facade.userNameOrPasswordNotValid );
    }

    @Test public void test02PasswordNotValid ( ) {
        assertThrowsLike( ( ) -> facade.createTokenFor( "Lucio", "passTeo" ),
                          facade.userNameOrPasswordNotValid );
    }

    @Test public void test03CanNotUseInvalidToken( ) {
        claimAndTestGiftCardOperations( 1000, validGiftCardNameOne, facade.invalidToken );
    }

    @Test public void test04CanNotOperateOnInvalidGiftCard( ) {
        claimAndTestGiftCardOperations(getValidTokenForValidUserOne( ), invalidGiftCardName, facade.invalidGiftCard);
    }

    @Test public void test05CanNotOperateOnUnclaimedGiftCard( ) {
        testGiftCardOperations( getValidTokenForValidUserOne( ), validGiftCardNameTwo, AvailableGiftCard.giftCardHasNotBeenClaimed );
    }

    @Test public void test06CanNotRequestClaimedGiftCard( ) {
        facade.claimGiftCard( getValidTokenForValidUserOne( ), validGiftCardNameOne );
        assertThrowsLike( ( ) -> facade.claimGiftCard( getValidTokenForValidUserTwo( ), validGiftCardNameOne ) ,
                ClaimedGiftCard.cardAlreadyClaimed );
    }

    @Test public void test07CanNotOperateOnUnownedClaimedGiftCard( ) {
        facade.claimGiftCard( getValidTokenForValidUserOne( ), validGiftCardNameOne );
        int otherToken = getValidTokenForValidUserTwo( );
        assertThrowsLike ( ( ) -> facade.getBalanceOn( otherToken, validGiftCardNameOne ), ClaimedGiftCard.cardDoesNotBelongToUser );
        assertThrowsLike ( ( ) -> facade.getExpensesOn( otherToken, validGiftCardNameOne ), ClaimedGiftCard.cardDoesNotBelongToUser );
    }

    @Test public void test08SaleUpdatesBalanceAndExpensesOnGiftCard( ) {
        int token = getValidTokenForValidUserOne( );
        facade.claimGiftCard( token, validGiftCardNameOne );

        assertEquals( facade.getBalanceOn( token, validGiftCardNameOne ), 1000 );
        assertTrue( facade.getExpensesOn( token, validGiftCardNameOne).isEmpty( ) );

        facade.registerSales( validMerchantKey, validUserNameOne, validGiftCardNameOne, 10 );

        List<Expense> expenses = facade.getExpensesOn( token, validGiftCardNameOne );
        assertFalse( expenses.isEmpty( ) );
        assertEquals( expenses.getFirst( ).getValue( ), 10 );

        assertEquals( facade.getBalanceOn( token, validGiftCardNameOne ), 990 );
    }

    @Test public void test09RegisterSalesFails( ) {
        facade.claimGiftCard( getValidTokenForValidUserOne( ), validGiftCardNameOne );
        testSaleError( "M4",             validUserNameOne, validGiftCardNameOne, 10   );
        testSaleError( validMerchantKey, "Emilio",         validGiftCardNameOne, 10   );
        testSaleError( validMerchantKey, validUserNameTwo, validGiftCardNameOne, 10   );
        testSaleError( validMerchantKey, validUserNameOne, invalidGiftCardName , 10   );
        testSaleError( validMerchantKey, validUserNameOne, validGiftCardNameTwo, 10   );
        testSaleError( validMerchantKey, validUserNameOne, validGiftCardNameOne, -1   );
        testSaleError( validMerchantKey, validUserNameOne, validGiftCardNameOne, 1001 );
    }

    @Test public void test10RequestingTwoTokensReturnsSameToken( ) {
        int token1 = getValidTokenForValidUserOne( );
        int token2 = getValidTokenForValidUserOne( );
        assertEquals( token1, token2 );
        facade.claimGiftCard( token1, validGiftCardNameOne );
        assertEquals( facade.getBalanceOn( token2, validGiftCardNameOne ), 1000 );
    }

    @Test public void test11TokenDoesntLiveMoreThan5Minutes( ) {
        Facade facade = newFacade( getClockWithOffsets( List.of( 6 ) ) );
        int token = facade.createTokenFor( "Lucio", "passLucio" );
        assertThrowsLike( ( ) -> facade.claimGiftCard(token, validGiftCardNameOne), facade.expiredToken);
        assertThrowsLike( ( ) -> facade.claimGiftCard(token, validGiftCardNameOne), facade.invalidToken);
    }

    @Test public void test12CanGetNewTokenAfterFirstOneExpires( ) {
        Facade facade = newFacade( getClockWithOffsets( List.of( 6, 6, 6, 6 ) ) );
        int token1 = facade.createTokenFor( "Lucio", "passLucio" );
        assertThrowsLike( ( ) -> facade.claimGiftCard(token1, validGiftCardNameOne), facade.expiredToken);
        int token2 = facade.createTokenFor( "Lucio", "passLucio" );
        facade.claimGiftCard( token2, validGiftCardNameOne );
        assertNotEquals( token1, token2 );
    }

    @Test public void test13TokenTimeLimitUpdatesWithActions( ) {
        Facade facade = newFacade( getClockWithOffsets( List.of( 4, 4, 8, 8, 12, 12, 16, 16 ) ) );
        int token = facade.createTokenFor( "Lucio", "passLucio" );
        facade.claimGiftCard( token, validGiftCardNameOne );
        facade.getBalanceOn(  token, validGiftCardNameOne );
        facade.getExpensesOn( token, validGiftCardNameOne );
        facade.getExpensesOn( token, validGiftCardNameOne );
    }

    @Test public void test14AskingForTokenRightAfterExpirationRenewsIt( ) {
        Facade facade = newFacade( getClockWithOffsets( List.of( 6, 6, 6, 6, 6 ) ) );
        int token1 = facade.createTokenFor( "Lucio", "passLucio" );
        int token2 = facade.createTokenFor( "Lucio", "passLucio" );
        assertEquals( token1, token2 );
        facade.claimGiftCard( token1, validGiftCardNameOne );
        assertEquals( facade.getBalanceOn( token2, validGiftCardNameOne ), 1000 );
    }

    @Test public void test15CanOperateOnMultipleGiftCards ( ) {
        int token = getValidTokenForValidUserOne( );
        facade.claimGiftCard( token, validGiftCardNameOne );
        facade.claimGiftCard( token, validGiftCardNameTwo );
        assertEquals( facade.getBalanceOn( token, validGiftCardNameOne ), 1000);
        assertEquals( facade.getBalanceOn( token, validGiftCardNameTwo ), 500);
    }

    @Test public void test16CanSupportMultipleUsers( ) {
        int token1 = getValidTokenForValidUserOne( );
        int token2 = getValidTokenForValidUserTwo( );

        facade.claimGiftCard( token1, validGiftCardNameOne );
        facade.claimGiftCard( token2, validGiftCardNameTwo );

        assertEquals( facade.getBalanceOn( token1, validGiftCardNameOne ), 1000 );
        assertEquals( facade.getBalanceOn( token2, validGiftCardNameTwo ), 500  );

        facade.registerSales( "M1", "Lucio", validGiftCardNameOne, 10 );
        facade.registerSales( "M2", "Teo"  , validGiftCardNameTwo, 20 );

        assertEquals( facade.getBalanceOn( token1, validGiftCardNameOne ), 990 );
        assertEquals( facade.getBalanceOn( token2, validGiftCardNameTwo ), 480 );

        assertEquals( facade.getExpensesOn( token1, validGiftCardNameOne ).getFirst( ).getValue( ), 10 );
        assertEquals( facade.getExpensesOn( token2, validGiftCardNameTwo ).getFirst( ).getValue( ), 20 );
    }


    private int getValidTokenForValidUserOne( ) {
        return facade.createTokenFor("Lucio", "passLucio" );
    }

    private int getValidTokenForValidUserTwo( ) {
        return facade.createTokenFor("Teo", "passTeo" );
    }

    private void claimAndTestGiftCardOperations( int token, String giftCardName, String message ) {
        assertThrowsLike( ( ) -> facade.claimGiftCard( token, giftCardName ), message );
        testGiftCardOperations(token, giftCardName, message);
    }

    private void testGiftCardOperations(int token, String giftCardName, String message) {
        assertThrowsLike( ( ) -> facade.getBalanceOn(token, giftCardName), message);
        assertThrowsLike( ( ) -> facade.getExpensesOn(token, giftCardName), message);
    }

    private void testSaleError( String merchantKey, String userName, String giftCardName, int price ) {
        assertThrowsLike( ( ) -> facade.registerSales( merchantKey, userName, giftCardName, price ), facade.saleUnsuccessful );
    }

    private static Clock getClockWithOffsets( List<Integer> offsets ) {
        return new Clock( ) {
            Iterator<LocalDateTime> it = Stream.concat(
                    Stream.of( LocalDateTime.now( ) ),
                    offsets.stream( ).map( offset -> LocalDateTime.now( ).plusMinutes( offset ) )
            ).toList( ).iterator( );

            public LocalDateTime now( ) {
                return it.next( );
            }
        };
    }

    private static Facade newFacade( Clock clock ) {
        return new Facade( Map.of("Lucio", "passLucio", "Teo", "passTeo" ),
                Map.of( "G1", new AvailableGiftCard(1000), "G2", new AvailableGiftCard( 500 ) ),
                List.of( "M1" , "M2", "M3" ),
                clock );
    }

    private static Facade newFacade( ) {
        return newFacade( new Clock( ) );
    }
}
