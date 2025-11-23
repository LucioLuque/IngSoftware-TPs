package org.udesa.giftcards.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udesa.giftcards.model.entities.GiftCard;

import java.util.Optional;

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {
    Optional<GiftCard> findByCardId( String cardId );
}
