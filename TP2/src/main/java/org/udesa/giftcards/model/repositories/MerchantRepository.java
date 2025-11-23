package org.udesa.giftcards.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udesa.giftcards.model.entities.MerchantVault;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<MerchantVault, Long> {
    Optional<MerchantVault> findByMerchantKey( String merchantKey );
    boolean existsByMerchantKey( String merchantKey );
}
