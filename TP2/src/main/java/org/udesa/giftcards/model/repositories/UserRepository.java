package org.udesa.giftcards.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udesa.giftcards.model.entities.UserVault;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserVault, Long> {
    Optional<UserVault> findByName( String name );
}
