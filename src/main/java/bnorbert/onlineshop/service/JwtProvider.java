package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.MyUserDetails;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springshop.jks");
            keyStore.load(resourceAsStream, "123456789jks".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new ResourceNotFoundException("Keystore loading problem");
        }

    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springshop", "123456789jks".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new ResourceNotFoundException("Exception occured while retrieving private key from keystore");
        }
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("springshop").getPublicKey();
        } catch (KeyStoreException e) {
            throw new ResourceNotFoundException("Exception occured while retrieving public key from keystore");
        }
    }


    Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);
    Date expirationDate = Date.from(expirationTime);

    public String generateToken(Authentication authentication) {
        MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getEmail())
                .signWith(getPrivateKey())
                .setExpiration(expirationDate)
                .compact();
    }

    public Boolean validateToken(String token) {
        parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token);

        return true;
    }

    public String getEmailFromJwt(String token) {
        Claims claims = parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}


