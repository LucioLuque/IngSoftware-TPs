package org.udesa.giftcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.udesa.giftcards.model.GiftCardFacade;

import java.util.*;

@RestController
@RequestMapping("/api/giftCards/")
public class GiftCardController {
    @Autowired GiftCardFacade giftCardFacade;

    @ExceptionHandler( RuntimeException.class )
    public ResponseEntity<String> handleIllegalArgument( RuntimeException ex ) {
        return ResponseEntity.internalServerError( ).body( ex.getMessage( ) );
    }

    private UUID getToken( String header ) {
        String tokenHeader = header.trim( );

        if ( tokenHeader.startsWith( "Bearer " ) ) {
            tokenHeader = tokenHeader.substring( 7 ).trim( );
        }

        return UUID.fromString( tokenHeader );
    }

    @Operation(
            summary = "Inicia sesión y obtiene un token de autenticación",
            description = "Valida las credenciales del usuario y devuelve un token que deberá enviarse en el header "
                    + "\"Authorization\" (formato: Bearer {token}) para operar sobre las gift cards."
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inicio de sesión exitoso. Se devuelve el token de autenticación.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario o contraseña inválidos"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping( value = "login", params = { "user", "pass" } )
    public ResponseEntity<Map<String, Object>> login(
            @Parameter( description = "Nombre de usuario" ) @RequestParam String user,
            @Parameter( description = "Contraseña del usuario" ) @RequestParam String pass ) {
        return ResponseEntity.ok( Map.of( "token", giftCardFacade.login( user, pass ).toString() ) );
    }

    @Operation(
            summary = "Redime una gift card para el usuario autenticado",
            description = "Marca una gift card como redimida utilizando el token de autenticación recibido en el login."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gift card redimida exitosamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o no autorizado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gift card no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping( value = "{cardId}/redeem" )
    public ResponseEntity<String> redeem(
            @Parameter( description = "Header de autorización con el token en formato Bearer" ) @RequestHeader( "Authorization" ) String header,
            @Parameter( description = "Identificador de la gift card" ) @PathVariable String cardId ) {
        giftCardFacade.redeem( getToken( header ), cardId );
        return ResponseEntity.ok( ).build( );
    }

    @Operation(
            summary = "Consulta el balance de una gift card",
            description = "Devuelve el saldo disponible de una gift card, validando el token de autenticación."
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o no autorizado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gift card no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping( "{cardId}/balance" )
    public ResponseEntity<Map<String, Object>> balance(
            @Parameter( description = "Header de autorización con el token en formato Bearer" ) @RequestHeader( "Authorization" ) String header,
            @Parameter( description = "Identificador de la gift card" ) @PathVariable String cardId ) {
        return ResponseEntity.ok( Map.of( "balance", giftCardFacade.balance( getToken( header ), cardId ) ) );
    }

    @Operation(
            summary = "Obtiene el detalle de una gift card",
            description = "Devuelve información detallada de una gift card (por ejemplo, movimientos o metadatos), "
                    + "validando previamente el token de autenticación."
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalles obtenidos correctamente",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o no autorizado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gift card no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping( "{cardId}/details" )
    public ResponseEntity<Map<String, Object>> details(
            @Parameter( description = "Header de autorización con el token en formato Bearer" ) @RequestHeader( "Authorization" ) String header,
            @Parameter( description = "Identificador de la gift card") @PathVariable String cardId ) {
        return ResponseEntity.ok( Map.of( "details", giftCardFacade.details( getToken( header ), cardId ) ) );
    }

    @Operation(
            summary = "Realiza un cargo sobre una gift card",
            description = "Permite efectuar un cargo (consumo) sobre una gift card, asociado a un comercio y con una descripción."
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cargo realizado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o monto no permitido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gift card no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping( "{cardId}/charge" )
    public ResponseEntity<String> charge(
            @Parameter( description = "Identificador del comercio que realiza el cargo" ) @RequestParam String merchant,
            @Parameter( description = "Monto del cargo a aplicar sobre la gift card" ) @RequestParam int amount,
            @Parameter( description = "Descripción o concepto del cargo" ) @RequestParam String description,
            @Parameter( description = "Identificador de la gift card" ) @PathVariable String cardId ) {
        giftCardFacade.charge( merchant, cardId, amount, description );
        return ResponseEntity.ok( ).build( );
    }
}
