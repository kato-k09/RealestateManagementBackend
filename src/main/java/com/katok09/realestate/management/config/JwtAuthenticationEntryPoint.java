package com.katok09.realestate.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * トークン認証失敗時の処理。ケースごとに詳細なエラーレスポンスを返します。
 */
@Component
public class JwtAuthenticationEntryPoint implements
    AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error", "Unauthorized");
    errorDetails.put("message", "認証が必要です。有効なJWTトークンを提供してください。");
    errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    errorDetails.put("path", request.getRequestURI());
    errorDetails.put("timestamp", System.currentTimeMillis());

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null) {
      errorDetails.put("details", "Authorizationヘッダーが存在しません");
    } else if (!authHeader.startsWith("Bearer ")) {
      errorDetails.put("details",
          "Authorizationヘッダーの形式が正しくありません（Bearer　トークン　が必要)");
    } else {
      errorDetails.put("details", "JWTトークンが無効または期限切れです");
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), errorDetails);

  }
}
