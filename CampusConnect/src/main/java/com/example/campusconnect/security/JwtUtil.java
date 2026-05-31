package com.example.campusconnect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24h - token complet
    private static final long PENDING_EXPIRATION = 1000 * 60 * 5;     // 5 min - token intermediar MFA

    /** Token complet, emis DOAR dupa trecerea cu succes de pasul MFA. */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("mfa", "ok")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Token intermediar emis dupa verificarea parolei, inainte de pasul MFA.
     * Are durata scurta si claim-ul mfa="pending" - nu da acces la resurse.
     */
    public String generatePendingToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .claim("mfa", "pending")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + PENDING_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public String extractRol(String token) {
        return (String) parse(token).get("rol");
    }

    /** True doar daca tokenul este unul intermediar valid (mfa="pending"). */
    public boolean isPendingToken(String token) {
        try {
            return "pending".equals(parse(token).get("mfa"));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }
}
