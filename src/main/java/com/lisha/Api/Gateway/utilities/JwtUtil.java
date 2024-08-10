package com.lisha.Api.Gateway.utilities;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
public class JwtUtil {

    //Add the method to validate if the JWT token is valid or not
    public static final String SECRET = PropertiesReader.getProperty(StringConstants.SECRET);

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(StringConstants.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(StringConstants.BEARER_HEADER)) {
            return bearerToken.substring(7); // Remove Bearer prefix
        }
        return null;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    public void validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException(StringConstants.UNAUTHORIZED_ACCESS, ex);
        } catch (ExpiredJwtException ex) {
            throw new CredentialsExpiredException(StringConstants.TOKEN_EXPIRED, ex);
        }

    }
}
