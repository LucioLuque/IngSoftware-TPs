package org.udesa.giftcards.model.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.model.repositories.MerchantRepository;
import org.udesa.giftcards.model.entities.MerchantVault;

@Service
public class MerchantService extends ServiceModel<MerchantVault, MerchantRepository> {

    @Transactional( readOnly = true )
    public MerchantVault findByMerchantKey( String merchantKey ) {
        return repository.findByMerchantKey( merchantKey ).orElseThrow( ( ) ->
                new RuntimeException( "Object of class " + getModelClass( ) + " and merchant key: " + merchantKey + " not found" ) );
    }

    protected void updateData( MerchantVault existingObject, MerchantVault updatedObject ) {
        existingObject.setMerchantKey( updatedObject.getMerchantKey( ) );
    }

    public boolean contains( String merchantKey ) {
        return repository.existsByMerchantKey( merchantKey );
    }

    protected String getStringIdentificator( MerchantVault model ) {
        return model.getMerchantKey( );
    }

}
