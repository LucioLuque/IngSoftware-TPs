package org.udesa.giftcards.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter @Setter
public class Charge extends EntityModel {
    private String description;
    @JsonIgnore
    @ManyToOne @JoinColumn( name = "giftCard_id" )
    private GiftCard giftCard;

    public Charge( ) { }

    public Charge( String description ) {
        this.description = description;
    }

}
