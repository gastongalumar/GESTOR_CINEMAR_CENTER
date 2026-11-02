package GESTOR_CINEMAR_CENTER.DEV.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            if (keyBytes.length >= 32) {
                return Keys.hmacShaKeyFor(keyBytes);
            }
        } catch (IllegalArgumentException ignored) {
        }
        return Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String tipo, Long id) {
        return generateToken(email, tipo, id, null, null, null);
    }

    public String generateToken(String email, String tipo, Long id, String nombre, String apellido) {
        return generateToken(email, tipo, id, nombre, apellido, null);
    }

    public String generateToken(String email, String tipo, Long id, String nombre, String apellido, String fotoPerfilUrl) {
        String nombreLimpio = nombre != null ? nombre.trim() : "";
        String apellidoLimpio = apellido != null ? apellido.trim() : "";
        String nombreCompleto = (nombreLimpio + " " + apellidoLimpio).trim();
        if (nombreCompleto.isBlank()) {
            nombreCompleto = email != null ? email : "";
        }

        var builder = Jwts.builder()
                .setSubject(email)
                .claim("tipo", tipo)
                .claim("userId", id)
                .claim("nombre", nombreLimpio.isBlank() ? nombreCompleto : nombreLimpio)
                .claim("apellido", apellidoLimpio)
                .claim("nombreCompleto", nombreCompleto);

        if (fotoPerfilUrl != null && !fotoPerfilUrl.isBlank()) {
            builder.claim("fotoPerfilUrl", fotoPerfilUrl.trim());
        }

        return builder
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}