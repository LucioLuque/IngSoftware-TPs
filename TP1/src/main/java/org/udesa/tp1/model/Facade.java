package org.udesa.tp1.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facade {
    public static String userNameOrPasswordNotValid = "User name or password not valid";
    public static String invalidToken = "Invalid token";
    public static String invalidGiftCard = "Invalid gift card";
    public static String invalidMerchantKey = "Invalid Merchant Key";
    public static String expiredToken = "Expired token";
    public static String saleUnsuccessful = "Sale Unsuccessful";


    private Map<String, String>   validUsers;
    private Map<String, GiftCard> giftCards;
    private List<String> merchantKeys;
    private Clock clock;

    private Map<Integer, Session> sessions = new HashMap<>( );

    private int nextToken = 1;

    public Facade( Map<String, String> validUsers, Map<String, GiftCard> validCards, List<String> merchantKeys, Clock clock ) {
        this.validUsers = validUsers;
        this.giftCards = new HashMap<>(validCards);
        this.merchantKeys = merchantKeys;
        this.clock = clock;
    }

    public int createTokenFor( String userName, String password ) {
        assertValidUserNameAndPassword( userName, password );

        return sessions.entrySet( ).stream( )
                .filter(entry -> entry.getValue( ).getUserName( ).equals( userName ) )
                .findFirst( )
                .map(entry -> {
                    entry.getValue( ).updateLastAccessTime( );
                    return entry.getKey();
                })
                .orElseGet( ( ) -> {
                    int token = nextToken++;
                    sessions.put( token, new Session( userName, clock ) );
                    return token;
                });
    }

    public Facade claimGiftCard( int token, String cardName ) {
        giftCards.put( cardName, getGiftCard( cardName ).claim( getUserName( token ) ) );
        return this;
    }

    public Facade registerSales( String merchantKey, String userName, String giftCardName, int price ) {
        try {
            assertMerchantKey( merchantKey );
            getGiftCard( giftCardName ).charge( userName, price, clock );

        } catch ( Exception e ) {
            throw new RuntimeException( saleUnsuccessful );
        }
        return this;
    }

    public List<Expense> getExpensesOn( int token, String giftCardName ) {
        return getGiftCard( giftCardName ).getExpenses( getUserName( token ) );
    }

    public int getBalanceOn( int token, String giftCardName ) {
        return getGiftCard( giftCardName ).getBalance( getUserName( token ) );
    }

    private String getUserName( int token ) {
        assertToken( token );
        return sessions.get( token ).updateLastAccessTime( ).getUserName( );
    }

    private GiftCard getGiftCard( String giftCardName ) {
        assertGiftCard( giftCardName );
        return giftCards.get( giftCardName );
    }

    private void assertValidUserNameAndPassword( String userName, String password ) {
        assertUserName( userName );
        assertPassword( userName, password );
    }

    private void assertPassword( String userName, String password ) {
        if ( !validUsers.get( userName ).equals( password ) ) throw new RuntimeException( userNameOrPasswordNotValid );
    }

    private void assertUserName( String userName ) {
        if ( !validUsers.containsKey( userName ) ) throw new RuntimeException( userNameOrPasswordNotValid );
    }

    private void assertGiftCard( String giftCardName ) {
        if ( !giftCards.containsKey( giftCardName ) ) throw new RuntimeException( invalidGiftCard );
    }

    private void assertToken( int token ) {
        if ( !sessions.containsKey( token ) ) throw new RuntimeException( invalidToken );
        if ( !sessions.get( token ).isActive( ) ) {
            sessions.remove( token );
            throw new RuntimeException( expiredToken );
        }
    }

    private void assertMerchantKey( String merchantKey ) {
        if ( !merchantKeys.contains( merchantKey ) ) throw new RuntimeException( invalidMerchantKey );
    }
}
