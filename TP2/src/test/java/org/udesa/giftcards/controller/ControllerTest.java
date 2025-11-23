package org.udesa.giftcards.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.giftcards.model.entities.Charge;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.MerchantVault;
import org.udesa.giftcards.model.entities.UserVault;
import org.udesa.giftcards.model.services.GiftCardService;
import org.udesa.giftcards.model.services.MerchantService;
import org.udesa.giftcards.model.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.udesa.giftcards.model.EntityDrawer.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;
    @Autowired private GiftCardService giftCardService;
    @Autowired private MerchantService merchantService;

    private String baseURL = "/api/giftCards/";
    public UserVault aUser;
    public MerchantVault aMerchant;
    public GiftCard aGiftCard;

    @BeforeEach public void beforeEach( ) {
        aUser = userService.save( newUser( ) );
        aGiftCard = giftCardService.save( newGiftCard( ) );
    }

    @BeforeAll public void beforeAll( ) {
        aMerchant = merchantService.save( newMerchant( ) );
    }

    @AfterAll public void afterAll( ) {
        giftCardService.cleanAllWithPrefix( giftCardNamePrefix );
        userService.cleanAllWithPrefix( userNamePrefix );
        merchantService.delete( aMerchant );
    }

    @Test void test01LoginSuccess( ) throws Exception {
        assertNotNull( getToken( ) );
    }

    @Test void test02InvalidLogin( ) throws Exception {
        getTokenFails( "Ringo", aUser.getPassword( ) );
        getTokenFails( aUser.getName( ), "passRingo" );
    }

    @Test void test03RedeemCard( ) throws Exception {
        redeem( getToken( ) );
//        assertEquals( aGiftCard.getOwner( ).getName( ) ==  );
        // mismo nombre
    }

    @Test void test04InvalidRedeemCard( ) throws Exception {
        redeemFails( "InvalidToken", aGiftCard.getCardId( ) );
        redeemFails( getToken( ), "InvalidGiftCardId" );
    }

    @Test void test05CheckBalance( ) throws Exception {
        String token = getToken( );
        redeem( token );
        assertEquals( 10, getBalance( token ) );
    }

    @Test void test06InvalidCheckBalance( ) throws Exception {
        getBalanceFails( "InvalidToken", aGiftCard.getCardId( ) );
        getBalanceFails( getToken( ), "InvalidGiftCardId" );
    }

    @Test void test07CheckEmptyDetails( ) throws Exception {
        String token = getToken( );
        redeem( token );
        assertTrue( getDetails( token ).isEmpty( ) );
    }

    @Test void test08InvalidCheckDetails( ) throws Exception {
        getDetailsFails( "InvalidToken", aGiftCard.getCardId( ) );
        getDetailsFails( getToken( ), "InvalidGiftCardId" );
    }

    @Test void test09CheckChargesDetails( ) throws Exception {
        String token = getToken( );
        redeem( token );
        charge( 2, "UnCargo" );
        assertEquals( "UnCargo", getDetails( token ).getLast( ).getDescription( ) );
    }

    @Test void test10InvalidCharge( ) throws Exception {
        chargeFails( "InvalidGiftCardId", aMerchant.getMerchantKey( ), 2, "UnCargo" );
        chargeFails( aGiftCard.getCardId( ), "InvalidMerchantKey", 2, "UnCargo" );
        chargeFails( aGiftCard.getCardId( ), aMerchant.getMerchantKey( ), aGiftCard.getBalance( ) + 1, "UnCargo" );
        assertTrue( aGiftCard.getCharges( ).isEmpty( ) );
    }

    private void charge( int amount, String description ) throws Exception {
        mockMvc.perform( post( baseURL + aGiftCard.getCardId( ) + "/charge" )
                         .param( "merchant", aMerchant.getMerchantKey( ) )
                         .param( "amount", String.valueOf( amount ) )
                         .param( "description", description )
                         .contentType( MediaType.APPLICATION_JSON ) )
               .andDo( print( ) )
               .andExpect( status( ).is( 200 ) );
    }

    private void chargeFails( String giftCardName, String merchant, int amount, String description ) throws Exception {
        mockMvc.perform( post( baseURL + giftCardName + "/charge" )
                         .param( "merchant", merchant )
                         .param( "amount", String.valueOf( amount ) )
                         .param( "description", description )
                         .contentType( MediaType.APPLICATION_JSON ) )
               .andDo( print( ) )
               .andExpect( status( ).is( 500 ) );
    }

    private List<Charge> getDetails( String token ) throws Exception {
        return  new ObjectMapper( ).readValue(
                mockMvc.perform( get( baseURL + aGiftCard.getCardId( ) + "/details")
                                 .header( "Authorization", "Bearer " + token ) )
                       .andDo( print( ) )
                       .andExpect( status( ).is( 200 ) )
                       .andReturn( )
                       .getResponse( )
                       .getContentAsString( ), new TypeReference<Map<String, List<Charge>>>( ) { } ).get( "details" );
    }

    private void getDetailsFails( String token, String giftCardName ) throws Exception {
        mockMvc.perform( get( baseURL + giftCardName + "/details")
                         .header( "Authorization", "Bearer " + token ) )
               .andDo( print( ) )
               .andExpect( status( ).is( 500 ) );
    }

    private int getBalance( String token ) throws Exception {
        return (Integer) new ObjectMapper( ).readValue(
                mockMvc.perform( get( baseURL + aGiftCard.getCardId( ) + "/balance" )
                                 .header( "Authorization", "Bearer " + token ) )
                       .andExpect( status( ).is( 200 ) )
                       .andReturn( )
                       .getResponse( )
                       .getContentAsString( ), HashMap.class ).get( "balance" );
    }

    private void getBalanceFails( String token, String giftCardName ) throws Exception {
        mockMvc.perform( get( baseURL + giftCardName + "/balance" )
                         .header( "Authorization", "Bearer " + token ) )
               .andDo( print( ) )
               .andExpect( status( ).is( 500 ) );
    }

    private void redeem( String token )throws Exception {
          mockMvc.perform( post( baseURL + aGiftCard.getCardId( ) + "/redeem" )
                           .header( "Authorization", "Bearer " + token )
                           .contentType( MediaType.APPLICATION_JSON ) )
                 .andDo( print( ) )
                 .andExpect( status( ).is( 200 ) );
    }

    private void redeemFails( String token, String giftCardName )throws Exception {
        mockMvc.perform( post( baseURL + giftCardName + "/redeem" )
                         .header( "Authorization", "Bearer " + token )
                         .contentType( MediaType.APPLICATION_JSON ) )
               .andDo( print( ) )
               .andExpect( status( ).is( 500 ) );
    }

    private String getToken( ) throws Exception {
        return  (String) new ObjectMapper(  )
                .readValue( mockMvc.perform( post( baseURL + "login" )
                                             .param( "user", aUser.getName( ) )
                                             .param( "pass", aUser.getPassword( ) ) )
                                   .andExpect( status( ).is( 200 ) )
                                   .andExpect( content( ).string( containsString ( "token" ) ) )
                                   .andReturn( )
                                   .getResponse( )
                                   .getContentAsString( ),
                            HashMap.class ).get( "token" );
    }

    private void getTokenFails( String userName, String password ) throws Exception {
        mockMvc.perform( post( baseURL + "login" )
                         .param( "user", userName )
                         .param( "pass", password ) )
               .andDo( print( ) )
               .andExpect( status( ).is( 500 ) );
    }
}
