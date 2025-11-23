package org.udesa.giftcards.model.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.MerchantVault;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.udesa.giftcards.model.EntityDrawer.merchantNamePrefix;
import static org.udesa.giftcards.model.EntityDrawer.newMerchant;

@SpringBootTest
public class MerchantServiceTest extends ServiceModelTest<MerchantVault, MerchantService> {

    protected MerchantVault newSample( ) {
        return newMerchant( );
    }

    protected String reservedPrefixTestName( ) {
        return merchantNamePrefix;
    }

    @Test public void testFindByMerchantKey( ) {
        MerchantVault model2 = service.findByMerchantKey( model.getMerchantKey( ) );
        assertEquals( model, model2 );
    }
}
