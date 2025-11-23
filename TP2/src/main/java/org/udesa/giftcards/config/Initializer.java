package org.udesa.giftcards.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.udesa.giftcards.model.entities.GiftCard;
import org.udesa.giftcards.model.entities.MerchantVault;
import org.udesa.giftcards.model.entities.UserVault;
import org.udesa.giftcards.model.services.GiftCardService;
import org.udesa.giftcards.model.services.MerchantService;
import org.udesa.giftcards.model.services.UserService;

@Configuration
public class Initializer {

    @Bean
    public CommandLineRunner init(UserService userService,
                                  GiftCardService giftCardService,
                                  MerchantService merchantService) {

        return args -> {

            try {
                userService.findByName("Teo");
            } catch (RuntimeException e) {
                userService.save(new UserVault("Teo", "TeoPass"));
            }

            try {
                giftCardService.findByCardId("GiftCard1");
            } catch (RuntimeException e) {
                giftCardService.save(new GiftCard("GiftCard1", 20));
            }

            try {
                merchantService.findByMerchantKey("Merchant1");
            } catch (RuntimeException e) {
                merchantService.save(new MerchantVault("Merchant1"));
            }
        };
    }
}
