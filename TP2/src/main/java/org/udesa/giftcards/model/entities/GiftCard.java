package org.udesa.giftcards.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class GiftCard extends EntityModel {
    @Column private int balance;
    @Column( unique = true) protected String cardId;

    @ManyToOne( )
    @JoinColumn( name = "owner_id" )
    private UserVault owner;

    @OneToMany( mappedBy ="giftCard", cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.EAGER)
    private List<Charge> charges = new ArrayList<>();

    public GiftCard( ) { }

    public GiftCard( String cardId, int initialBalance) {
        this.cardId = cardId;
        this.balance = initialBalance;
    }

    protected boolean same( Object o ) {
        return cardId.equals( getClass( ).cast( o ).getCardId( ) );
    }
}
