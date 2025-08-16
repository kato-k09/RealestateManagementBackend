package com.katok09.realestate.management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Jwtトークンの生成、解析、検証をするユーティリティクラス
 */
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expirationInSeconds;

  /**
   * トークンを生成します。
   *
   * @param username ユーザー名
   * @param role     ロール
   * @param userId   ユーザーID
   * @return トークン文字列
   */
  public String generateToken(String username, String role, int userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    claims.put("userId", userId);
    return createToken(claims, username);
  }

  /**
   * トークンからユーザーIDを抽出します。
   *
   * @param token トークン
   * @return ユーザーID
   */
  public int getUserIdFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);
    return (Integer) claims.get("userId");
  }

  /**
   * トークンからユーザー名を抽出します。
   *
   * @param token トークン
   * @return ユーザー名
   */
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  /**
   * トークンからトークン有効期限を抽出します。
   *
   * @param token トークン
   * @return トークン有効期限
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  /**
   * トークンから任意の値を抽出します。
   *
   * @param token          トークン
   * @param claimsResolver クレーム情報から特定の情報を抽出する関数型インターフェース
   * @param <T>            戻り値の型
   * @return クレームから抽出された値
   */
  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * トークンから全てのクレームを抽出します。
   *
   * @param token トークン
   * @return クレーム
   */
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

  /**
   * 指定された値を元に秘密鍵を生成します。
   *
   * @return 秘密鍵
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * クレーム、指定された文字列からトークンを生成します。expirationInSecondsで指定された秒数がトークンの有効期限となります。
   *
   * @param claims  クレーム（ロール、ユーザーIDが含まれます）
   * @param subject ユーザー名
   * @return
   */
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationInSeconds * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * 有効なトークンか検証します。
   *
   * @param token       トークン
   * @param userDetails ユーザー詳細情報
   * @return 有効なトークンであればtrue、そうでなければfalseが返ります。
   */
  public boolean validateToken(String token, UserDetails userDetails) {
    try {
      final String username = getUsernameFromToken(token);
      return (username.equals(userDetails.getUsername()) && isTokenExpired(token));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * トークンの有効期限が切れているかを検証します。
   *
   * @param token トークン
   * @return 有効期限が切れていなければtrue、切れているまたは例外発生時はfalseを返します。
   */
  public boolean isTokenExpired(String token) {
    try {
      final Date expiration = getExpirationDateFromToken(token);
      return !expiration.before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * トークンから残り有効期限を抽出します。
   *
   * @param token トークン
   * @return トークン残り有効期限分数
   */
  public long getRemainingTimeInMinutes(String token) {
    Date expiration = getExpirationDateFromToken(token);
    Date now = new Date();
    long remainingTime = expiration.getTime() - now.getTime();
    return remainingTime / (60 * 1000);
  }

  /**
   * リクエストからJWTトークンを抽出します。
   *
   * @param request HTTPリクエスト
   * @return JWTトークン（Bearer プレフィックスなし）
   */
  public String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

  /**
   * エラーレスポンスを作成します。
   *
   * @param errorCode エラーコード
   * @param message   エラーメッセージ
   * @return エラーレスポンス
   */
  public Map<String, Object> createErrorResponse(String errorCode, String message) {
    Map<String, Object> error = new HashMap<>();
    error.put("error", true);
    error.put("errorCode", errorCode);
    error.put("message", message);
    error.put("timestamp", System.currentTimeMillis());
    return error;
  }
}
