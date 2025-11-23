package org.udesa.giftcards.model.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.UserVault;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.udesa.giftcards.model.EntityDrawer.*;

@SpringBootTest
public class GiftCardServiceTest extends ServiceModelTest<GiftCard, GiftCardService> {
    @Autowired UserService userService;

    protected GiftCard newSample( ) {
        return newGiftCard( );
    }

    protected String reservedPrefixTestName( ) {
        return giftCardNamePrefix;
    }

    @Test public void testFindByGiftCardId( ) {
        GiftCard model2 = service.findByCardId( model.getCardId( ) );
        assertEquals( model, model2 );
    }

    @Test public void aSimpleCard( ) {
        assertEquals( 10, savedSample( ).getBalance( ) );
    }

    @Test public void aSimpleIsNotOwnedCard( ) {
        assertFalse( service.owned( savedSample( ) ) );
    }

    @Test public void canRedeemCard( ) {
        UserVault aUser = userService.save( newUser( ) );
        service.redeem( model, aUser.getName( ) );
        assertEquals( aUser.getName( ), model.getOwner( ).getName( ) );
    }

    @Test public void cannotChargeUnownedCards( ) {
        assertThrows( RuntimeException.class, ( ) -> service.charge( model, 2, "Un cargo" ) );
        assertEquals( 10, model.getBalance( ) );
        assertTrue( model.getCharges( ).isEmpty( ) );
    }

    @Test public void chargeACard( ) {
        UserVault aUser = userService.save( newUser( ) );
        service.redeem( model, aUser.getName( ) );
        service.charge( model, 2, "Un cargo" );
        assertEquals( 8, model.getBalance( ) );
        assertEquals( "Un cargo", model.getCharges( ).getLast( ).getDescription( ) );
        service.delete( model );
        userService.delete( aUser );
    }

    @Test public void cannotOverrunACard( ) {
        UserVault aUser = userService.save( newUser( ) );
        service.redeem( model, aUser.getName( ) );
        assertThrows( RuntimeException.class, ( ) -> service.charge( model, model.getBalance( ) + 1, "Un cargo" ) );
        assertEquals( 10, model.getBalance( ) );
        service.delete( model );
        userService.delete( aUser );
    }
}
