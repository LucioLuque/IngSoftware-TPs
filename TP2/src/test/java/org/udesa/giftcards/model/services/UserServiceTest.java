package org.udesa.giftcards.model.services;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.UserVault;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.udesa.giftcards.model.EntityDrawer.giftCardNamePrefix;
import static org.udesa.giftcards.model.EntityDrawer.newUser;

@SpringBootTest
public class UserServiceTest extends ServiceModelTest<UserVault, UserService> {

    protected UserVault newSample( ) {
        return newUser( );
    }

    protected String reservedPrefixTestName( ) {
        return giftCardNamePrefix;
    }

    @Test public void testFindByName( ) {
        UserVault model2 = service.findByName( model.getName( ) );
        assertEquals( model, model2 );
    }
}
