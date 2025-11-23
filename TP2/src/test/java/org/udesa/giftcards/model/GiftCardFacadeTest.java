package org.udesa.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.udesa.giftcards.model.EntityDrawer.*;

import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.MerchantVault;
import org.udesa.giftcards.model.entities.UserVault;
import org.udesa.giftcards.model.services.GiftCardService;
import org.udesa.giftcards.model.services.MerchantService;
import org.udesa.giftcards.model.services.UserService;

@SpringBootTest
public class GiftCardFacadeTest {
    // Se espera que el usuario pueda inciar sesion con usuario y password y obtener un token
    //    debe poder usar el token para gestionar la tarjeta.
    //    el token se vence a los 5'

    // las giftcards ya estan definidas en el sistema.
    //    el usuario las reclama, pueden ser varias
    //    puede consultar el saldo y el detalle de gastos de sus tarjetas

    // los merchants pueden hacer cargos en las tarjetas que hayan sido reclamadas.
    //    los cargos se actualizan en el balance de las tarjetas

    @Autowired private GiftCardFacade facade;
    @Autowired private UserService userService;
    @Autowired private GiftCardService giftCardService;
    @Autowired private MerchantService merchantService;

    @MockBean Clock clock;

    public UserVault aUser;
    public MerchantVault aMerchant;
    public GiftCard aGiftCard;

    @BeforeAll public void beforeAll(  ) {
          aMerchant = merchantService.save( newMerchant( ) );
    }

    @BeforeEach public void beforeEach( ) {
        when( clock.now( ) ).then( it -> LocalDateTime.now( ) );
        aUser = userService.save( newUser( ) );
        aGiftCard = giftCardService.save( newGiftCard( ) );
    }

    @AfterAll public void afterAll( ) {
        giftCardService.cleanAllWithPrefix( giftCardNamePrefix );
        userService.cleanAllWithPrefix( userNamePrefix );
        merchantService.delete( aMerchant );
    }

    @Test public void userCanOpenASession( ) {
        assertNotNull( facade.login( aUser.getName( ), aUser.getPassword( ) ) );
    }

    @Test public void unknownUserCanNotOpenASession( ) {
        assertThrows( RuntimeException.class, ( ) -> facade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidToken( ) {
        assertThrows( RuntimeException.class, ( ) -> facade.redeem( UUID.randomUUID( ),  aGiftCard.getCardId( ) ) );
        assertThrows( RuntimeException.class, ( ) -> facade.balance( UUID.randomUUID( ),  aGiftCard.getCardId( ) ) );
        assertThrows( RuntimeException.class, ( ) -> facade.details( UUID.randomUUID( ),  aGiftCard.getCardId( ) ) );
    }

    @Test public void userCannotCheckOnAlienCard( ) {
        UUID token = facade.login( aUser.getName( ) , aUser.getPassword( ) );
        assertThrows( RuntimeException.class, ( ) -> facade.balance( token,  aGiftCard.getCardId( ) ) );
    }

    @Test public void userCanRedeemACard( ) {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );
        facade.redeem( token, aGiftCard.getCardId( ) );
        assertEquals( 10, facade.balance( token, aGiftCard.getCardId( ) ) );
    }

    @Test public void userCanRedeemASecondCard( ) {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );

        GiftCard aGiftCard2 = giftCardService.save( newGiftCard2( ) );

        facade.redeem( token, aGiftCard.getCardId( ) );
        facade.redeem( token, aGiftCard2.getCardId( ) );

        assertEquals( 10, facade.balance( token, aGiftCard.getCardId( ) ) );
        assertEquals( 5, facade.balance( token, aGiftCard2.getCardId( ) ) );
    }

    @Test public void multipleUsersCanRedeemACard( ) {
        UUID aUserToken1 = facade.login( aUser.getName( ), aUser.getPassword( ) );
        UserVault aUser2 = userService.save( newUser( ) );
        UUID aUserToken2 = facade.login( aUser2.getName( ), aUser2.getPassword( ) );

        GiftCard aGiftCard2 = giftCardService.save( newGiftCard2( ) );

        facade.redeem( aUserToken1, aGiftCard.getCardId( ) );
        facade.redeem( aUserToken2, aGiftCard2.getCardId( ) );

        assertEquals( 10, facade.balance( aUserToken1,  aGiftCard.getCardId( ) ) );
        assertEquals( 5, facade.balance( aUserToken2,  aGiftCard2.getCardId( ) ) );
    }

    @Test public void unknownMerchantCantCharge( ) {
        assertThrows( RuntimeException.class, ( ) -> facade.charge( "Mx",  aGiftCard.getCardId( ), 2, "UnCargo" ) );
    }

    @Test public void merchantCantChargeUnredeemedCard( ) {
        assertThrows( RuntimeException.class, ( ) -> facade.charge( aMerchant.getMerchantKey( ),  aGiftCard.getCardId( ), 2, "UnCargo" ) );
    }

    @Test public void merchantCanChargeARedeemedCard( ) {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );

        facade.redeem( token,  aGiftCard.getCardId( ) );
        facade.charge( aMerchant.getMerchantKey( ),  aGiftCard.getCardId( ), 2, "UnCargo" );

        assertEquals( 8, facade.balance( token,  aGiftCard.getCardId( ) ) );
    }

    @Test public void merchantCannotOverchargeACard( ) {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );

        facade.redeem( token,  aGiftCard.getCardId( ) );
        assertThrows( RuntimeException.class, ( ) -> facade.charge( aMerchant.getMerchantKey( ),  aGiftCard.getCardId( ), 11, "UnCargo" ) );
    }

    @Test public void userCanCheckHisEmptyCharges( ) {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );

        facade.redeem( token,  aGiftCard.getCardId( ) );

        assertTrue( facade.details( token,  aGiftCard.getCardId( ) ).isEmpty( ) );
    }

    @Test public void userCanCheckHisCharges( ) {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );

        facade.redeem( token,  aGiftCard.getCardId( ) );
        facade.charge( aMerchant.getMerchantKey( ),  aGiftCard.getCardId( ), 2, "UnCargo" );

        assertEquals( "UnCargo", facade.details( token,  aGiftCard.getCardId( ) ).getLast( ).getDescription( ) );
    }

    @Test public void userCannotCheckOthersCharges() {
        facade.redeem( facade.login( aUser.getName( ), aUser.getPassword( ) ),  aGiftCard.getCardId( ) );

        UserVault aUser2 = userService.save( newUser( ) );
        UUID token = facade.login( aUser2.getName( ), aUser2.getPassword( ) );

        assertThrows( RuntimeException.class, () -> facade.details( token,  aGiftCard.getCardId( ) ) );
    }

    @Test public void tokenExpires() {
        UUID token = facade.login( aUser.getName( ), aUser.getPassword( ) );
        when( clock.now( ) ).then( it -> LocalDateTime.now( ).plusMinutes( 16 ) );
        assertThrows( RuntimeException.class, () -> facade.redeem( token,  aGiftCard.getCardId( ) ) );
    }

}
