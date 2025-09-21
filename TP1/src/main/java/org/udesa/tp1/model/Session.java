package org.udesa.tp1.model;

import java.time.LocalDateTime;

public class Session {
    private Clock clock;
    private String userName;
    private LocalDateTime lastAccess;
    private int maxMinutesInactive = 5;

    public Session(String userName , Clock clock ) {
        this.userName = userName;
        this.clock = clock;
        this.lastAccess = clock.now( );
    }

    public boolean isActive( ) {
        return lastAccess.plusMinutes( maxMinutesInactive ).isAfter( clock.now( ) );
    }

    public Session updateLastAccessTime( ) {
        this.lastAccess = clock.now( );
        return this;
    }

    public String getUserName( ) { return  userName; }
}
