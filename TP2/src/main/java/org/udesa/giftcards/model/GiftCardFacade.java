package org.udesa.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udesa.giftcards.model.entities.Charge;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.services.GiftCardService;
import org.udesa.giftcards.model.services.MerchantService;
import org.udesa.giftcards.model.services.UserService;

import java.util.*;

@Service
public class GiftCardFacade {
    public static final String InvalidUser = "InvalidUser";
    public static final String InvalidMerchant = "InvalidMerchant";
    public static final String InvalidToken = "InvalidToken";

    @Autowired private UserService userService;
    @Autowired private MerchantService merchantService;
    @Autowired private GiftCardService giftCardService;
    @Autowired private Clock clock;

    private Map<UUID, UserSession> sessions = new HashMap( );

    public GiftCardFacade( ) { }

    public UUID login( String userKey, String pass ) {
        userService.validateUser( userKey, pass );
        UUID token = UUID.randomUUID( );
        sessions.put( token, new UserSession( userKey, clock ) );
        return token;
    }

    public void redeem( UUID token, String cardName ) {
        giftCardService.redeem( cardName, findUser( token ) );
    }

    public int balance( UUID token, String cardId ) {
        return ownedCard( token, cardId ).getBalance( );
    }

    public void charge( String merchantKey, String cardId, int amount, String description ) {
        if ( !merchantService.contains( merchantKey ) ) throw new RuntimeException( InvalidMerchant );
        giftCardService.charge( cardId, amount, description );
    }

    public List<Charge> details( UUID token, String cardId ) {
        return ownedCard( token, cardId ).getCharges( );
    }

    private GiftCard ownedCard( UUID token, String cardName ) {
        GiftCard card = giftCardService.findByCardId( cardName );
        if ( !giftCardService.cardIsOwnedBy( card, findUser( token ) ) ) throw new RuntimeException( InvalidToken );
        return card;
    }

    private String findUser( UUID token ) {
        return sessions.computeIfAbsent( token, key -> { throw new RuntimeException( InvalidToken ); } )
                       .userAliveAt( clock );
    }
}
