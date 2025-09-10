package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FacadeTest implements AssertHelpers {

    //Tarjetero y cada tarjeta sus movimientos!!
    Facade facade;
    @BeforeEach public void  beforeEach() {
        facade = newFacade();
    }

    @Test public void test01UserNotValid(){
        var facade = newFacade();
        assertThrowsLike( ( ) -> facade.createTokenFor("John", "passLucio"), facade.userNameOrPasswordNotValid);
    }

    @Test public void test02PasswordNotValid(){
        var facade = newFacade();
        assertThrowsLike( ( ) -> facade.createTokenFor("Lucio", "passTeo"), facade.userNameOrPasswordNotValid);
    }

    @Test public void test03CanNotUseInvalidToken(){
        var facade = newFacade();
        int token = facade.createTokenFor("Lucio", "passLucio");
        assertThrowsLike( ( ) -> facade.claimGiftCard(1000, "G1"), facade.invalidToken);
    }

    @Test public void test04CanNotClaimInvalidGiftCard(){
        var facade = newFacade();
        int token = facade.createTokenFor("Lucio", "passLucio");
        assertThrowsLike ( ( ) -> facade.claimGiftCard(token, "G3"), facade.invalidGiftCard);
    }

    @Test public void test05CanAskForClaimedGiftCard(){
        int token = facade.createTokenFor("Lucio", "passLucio");
        facade.claimGiftCard(token, "G1");
        assertEquals(1000, facade.requestBalance(token, "G1"));
    }

    @Test public void test06CanNotRequestInfoFromNotClaimedGiftCard(){ //hacer!!!
        int token = facade.createTokenFor("Lucio", "passLucio");
        facade.claimGiftCard(token, "G1");
        int token2 = facade.createTokenFor("Teo", "passTeo");
        assertThrowsLike ( ( ) -> facade.requestBalance(token2, "G1"), facade.invalidGiftCard );
    }

//    @Test public void test04CanAskForClaimedGiftCard(){
//        var facade = newFacade();
//        String token = facade.createTokenFor("Lucio", "passLucio");
//        facade.claimGiftCard(token, 1).requestGiftCard()
//
//
//    }



    private static Facade newFacade() {
        return new Facade(Map.of("Lucio", new User("Lucio", "passLucio"), "Teo", new User("teo", "passTeo")),
                Map.of("G1", new GiftCard("G1", 1000), "G2", new GiftCard("G2", 500))
        );
    }
}
