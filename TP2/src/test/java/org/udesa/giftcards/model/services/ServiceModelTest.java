package org.udesa.giftcards.model.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.udesa.giftcards.model.entities.EntityModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ServiceModelTest<M extends EntityModel, S extends ServiceModel<M, ? extends JpaRepository<M, Long>>> {
    @Autowired protected S service;
    public M model;

    @BeforeEach public void beforeEach( ) {
        model = savedSample( );
    }

    @AfterAll public void afterAll( ) {
        service.cleanAllWithPrefix( reservedPrefixTestName( ) );
    }

    protected abstract M newSample( );

    protected abstract String reservedPrefixTestName( );

    protected M savedSample( ) {
        return service.save( newSample( ) );
    }

    @Test public void testEntitySave( ) {
        model = newSample( );
        M retrieved = service.save( model );
        assertNotNull( retrieved.getId( ) );
        assertNotNull( model.getId( ) );
        assertEquals( retrieved, model );
    }

    @Test public void testEntityUpdate( ) {
        model = savedSample( );
        service.update( model.getId( ), model );
        M retrieved = service.getById( model.getId( ) );
        assertEquals( model, retrieved );
    }

    @Test public void testDeletionByObject( ) {
        service.delete( model );
        assertThrows( RuntimeException.class, ( ) -> service.getById( model.getId( ) ) );
    }

    @Test public void testDeletionById( ) {
        service.delete( model.getId( ) );
        assertThrows( RuntimeException.class, ( ) -> service.getById( model.getId( ) ) );
    }

    @Test public void testDeletionByProxy( ) throws Exception {
        M proxy = (M) model.getClass( ).getConstructor( ).newInstance( );
        proxy.setId( model.getId( ) );

        service.delete( proxy );
        assertThrows( RuntimeException.class, ( ) -> service.getById( model.getId( ) ) );
    }

    @Test public void testFindAll( ) {
        List list = service.findAll( );
        assertFalse( list.isEmpty( ) );
        assertTrue( list.contains( model ) );
    }

}
