package org.udesa.giftcards.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class EntityModel {
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Id private long id;

    public boolean equals( Object o ) {
        return this == o ||
                o != null && id != 0 &&
                        getClass( ) == o.getClass( ) && id == getClass( ).cast( o ).getId( ) &&
                        same( o );
    }

    public int hashCode( ) {
        return Long.hashCode( id );
    }

    protected boolean same( Object o ) { return true; }
}
