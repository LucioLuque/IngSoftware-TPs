package org.udesa.tp1.model;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface AssertHelpers {
    default void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message, assertThrows(Exception.class, executable).getMessage( ) );
    }
}
