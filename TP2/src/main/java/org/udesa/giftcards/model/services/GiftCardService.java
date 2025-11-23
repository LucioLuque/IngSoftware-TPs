package org.udesa.giftcards.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.model.entities.Charge;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.repositories.GiftCardRepository;

@Service
public class GiftCardService extends ServiceModel<GiftCard, GiftCardRepository> {
    public static final String CargoImposible = "CargoImposible";
    public static final String InvalidCard = "InvalidCard";

    @Autowired private UserService userService;

    @Transactional( readOnly = true )
    public GiftCard findByCardId( String cardId ) {
        return repository.findByCardId( cardId ).orElseThrow( ( ) ->
                new RuntimeException( "Object of class " + getModelClass( ) + " and card Id: " + cardId + " not found" ) );
    }

    protected void updateData( GiftCard existingObject, GiftCard updatedObject ) {
        existingObject.setCardId( updatedObject.getCardId( ) );
        existingObject.setBalance( updatedObject.getBalance( ) );
        existingObject.setCharges( updatedObject.getCharges( ) );
    }

    public GiftCard charge( GiftCard model, int anAmount, String description ) {
        if ( !owned( model ) || model.getBalance( ) - anAmount < 0 )  throw new RuntimeException( CargoImposible );

        model.setBalance( model.getBalance( ) - anAmount );
        Charge charge = new Charge( description );
        model.getCharges( ).add( charge );
        charge.setGiftCard( model );
        return save( model );
    }

    public GiftCard charge( String cardId, int anAmount, String description ) {
        return charge( findByCardId( cardId ), anAmount, description );
    }

    public GiftCard redeem( GiftCard model, String newOwner ) {
        if ( owned( model ) ) throw new RuntimeException( InvalidCard );
        model.setOwner( userService.findByName( newOwner ) );
        return save( model );
    }

    public GiftCard redeem( String cardId, String newOwner ) {
        return redeem( findByCardId( cardId ), newOwner );
    }

    public boolean cardIsOwnedBy( GiftCard model, String user ) {
        return model.getOwner( ).getId( ) == userService.findByName( user ).getId( );
    }

    public static boolean owned( GiftCard model ) {
        return model.getOwner( ) != null;
    }

    @Override protected String getStringIdentificator( GiftCard model ) {
        return model.getCardId( );
    }

}
