package com.katok09.realestate.management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

  @Value("${jwt.secret:mySecretKey12345678901234567890123456789012345678901234567890}")
  private String secret;

  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      throw new RuntimeException("JWTトークンが期限切れです", e);
    } catch (UnsupportedJwtException e) {
      throw new RuntimeException("サポートされていないJWTトークンです", e);
    } catch (MalformedJwtException e) {
      throw new RuntimeException("不正なJWTトークンです", e);
    } catch (SecurityException e) {
      throw new RuntimeException("JWT署名が無効です", e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("JWTトークンが空です", e);
    }
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  public String generateToken(String username, String role, Long userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    claims.put("userId", userId);
    return createToken(claims, username);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    try {
      final String username = getUsernameFromToken(token);
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } catch (Exception e) {
      return false;
    }
  }

  public String getRoleFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);
    return claims.get("role", String.class);
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);
    return claims.get("userId", Long.class);
  }

  public Boolean isTokenValid(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  public Date getIssuedAtFromToken(String token) {
    return getClaimFromToken(token, Claims::getIssuedAt);
  }

  public long getRemainingTimeInMinutes(String token) {
    Date expiration = getExpirationDateFromToken(token);
    Date now = new Date();
    long remainingTime = expiration.getTime() - now.getTime();
    return remainingTime / (60 * 1000);
  }
}
