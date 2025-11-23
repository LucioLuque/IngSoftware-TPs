package org.udesa.giftcards.model.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.model.GiftCardFacade;
import org.udesa.giftcards.model.repositories.UserRepository;
import org.udesa.giftcards.model.entities.UserVault;

@Service
public class UserService extends ServiceModel<UserVault, UserRepository> {

    @Transactional( readOnly = true )
    public UserVault findByName( String name ) {
        return repository.findByName( name ).orElseThrow( ( ) ->
                new RuntimeException( "Object of class " + getModelClass( ) + " and name: " + name + " not found" ) );
    }

    public void validateUser(String userName, String password) {
        repository.findByName( userName )
                .filter( u -> u.getPassword( ).equals( password ) )
                .orElseThrow( ( ) -> new RuntimeException( GiftCardFacade.InvalidUser ) );
    }

    protected String getStringIdentificator( UserVault model ) {
        return model.getName( );
    }

    protected void updateData( UserVault existingObject, UserVault updatedObject ) {
        existingObject.setName( updatedObject.getName( ) );
        existingObject.setPassword( updatedObject.getPassword( ) );
    }
}