package com.iesvdc.pizza.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la gestión de tokens JWT.
 * Esta clase proporciona métodos para generar, validar y extraer información de tokens JWT.
 * Se usa en la autenticación y autorización de usuarios en la aplicación.
 */
@Component
public class JwtService {

    // Clave secreta para firmar los tokens JWT.
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Genera un token JWT con el nombre de usuario, ID de usuario y rol
    public String generateToken(String userName, String userId, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("rol", rol);
        return createToken(claims, userName);
    }

    //  Crea un token JWT con los claims y el sujeto especificado.
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token valid for 30 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Obtiene la clave de firma para el token JWT.
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extrae el nombre de usuario de un token JWT.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae la fecha de expiración de un token JWT.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrae un claim específico del token JWT.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //  Extrae todos los claims de un token JWT.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    // Verifica si un token JWT ha expirado.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Valida un token JWT comparando su información con la de un usuario y verificando su expiración.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //  Extrae el rol del usuario desde el token JWT.
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class); // 🔹 Extrae el rol como String
    }
}