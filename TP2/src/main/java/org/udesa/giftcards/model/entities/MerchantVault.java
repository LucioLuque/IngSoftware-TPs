package org.udesa.giftcards.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class MerchantVault extends EntityModel {
    @Column( unique = true) protected String merchantKey;

    public MerchantVault( ) { }

    public MerchantVault( String merchantKey ) {
        this.merchantKey = merchantKey;
    }

    protected boolean same( Object o ) {
        return merchantKey.equals( getClass( ).cast( o ).getMerchantKey( ) );
    }
}
