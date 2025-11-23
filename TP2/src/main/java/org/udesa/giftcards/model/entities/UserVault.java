package org.udesa.giftcards.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class UserVault extends EntityModel {
    @Column private String password;
    @Column( unique = true) protected String name;

    public UserVault( ) { }

    public UserVault( String name, String password ) {
        this.name = name;
        this.password = password;
    }

    protected boolean same( Object o ) {
        return name.equals( getClass( ).cast( o ).getName( ) );
    }
}
