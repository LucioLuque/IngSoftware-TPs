package org.udesa.tp1.model;

import java.time.LocalDateTime;

public class Expense {
    private int value;
    private LocalDateTime time;

    public Expense( int value, Clock clock ) {
        this.value = value;
        this.time = clock.now( );
    }

    public int getValue( )          { return value; }
    public LocalDateTime getTime( ) { return time;  }
}
