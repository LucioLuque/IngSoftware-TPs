package org.udesa.giftcards.model;

import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.MerchantVault;
import org.udesa.giftcards.model.entities.UserVault;

import java.time.Instant;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntityDrawer {
    public static String userNamePrefix = "Bob";
    public static String userPassword = userNamePrefix + "Pass";
    public static String giftCardNamePrefix = "GC";
    public static String merchantNamePrefix = "Merchant";

    public static Random randomStream = new Random( Instant.now( ).getEpochSecond( ) );

    public static int nextKey( ) {
        return randomStream.nextInt( ) ;
    }

    public static String newRandomName( String prefix ) {
        return prefix + IntStream.range( 0, 30 - prefix.length( ) )
                                 .mapToObj( i -> String.valueOf( ( char ) ('A' + Math.abs( nextKey( ) ) % 26 ) ) )
                                 .collect( Collectors.joining( ) );
    }

    public static UserVault newUser( ) {
        return new UserVault( newRandomName( userNamePrefix ), userNamePrefix + userPassword );
    }

    public static GiftCard newGiftCard( ) {
        return new GiftCard( newRandomName( giftCardNamePrefix ), 10 );
    }

    public static GiftCard newGiftCard2( ) {
        return new GiftCard( newRandomName( giftCardNamePrefix ), 5 );
    }

    public static MerchantVault newMerchant( ) {
        return new MerchantVault( newRandomName( merchantNamePrefix ) );
    }
}
