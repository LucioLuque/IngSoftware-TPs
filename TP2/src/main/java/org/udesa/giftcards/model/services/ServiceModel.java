package org.udesa.giftcards.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.model.entities.EntityModel;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public abstract class ServiceModel<M extends EntityModel, R extends JpaRepository<M, Long >>  {
    @Autowired protected R repository;

    @Transactional( readOnly = true )
    public List<M> findAll( ) {
        return StreamSupport.stream( repository.findAll( ).spliterator( ), false ).toList( );
    }

    @Transactional( readOnly = true )
    public M getById( long id, Supplier<? extends M> supplier ) {
        return repository.findById( id ).orElseGet( supplier );
    }

    public M getById( long id ) {
        return getById( id, ( ) -> {
            throw new RuntimeException( "Object of class " + getModelClass( ) + " and id: " + id + " not found" );
        } );
    }

    protected Class<M> getModelClass( ) {
        return ( Class<M> ) ( ( ParameterizedType ) getClass( ).getGenericSuperclass( ) ).getActualTypeArguments( )[ 0 ];
    }

    public M save( M model ) {
        return repository.save( model );
    }

    public M update( Long id, M updatedObject ) {
        M object = getById( id );
        updateData( object, updatedObject );
        return save( object );
    }

    public void delete( long id ) {
        repository.deleteById( id );
    }

    public void delete( M model ) {
        delete( model.getId( ) );
    }

    public void cleanAllWithPrefix( String prefix ){
        this.findAll( )
            .stream( )
            .filter( model -> getStringIdentificator( model ).startsWith( prefix ) )
            .forEach( this::delete );
    }

    protected abstract String getStringIdentificator( M model );

    protected abstract void updateData( M existingObject, M updatedObject );
}
