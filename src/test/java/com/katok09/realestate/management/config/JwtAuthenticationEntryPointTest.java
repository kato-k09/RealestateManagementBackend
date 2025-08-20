package com.katok09.realestate.management.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationEntryPointTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private AuthenticationException authException;

  private JwtAuthenticationEntryPoint sut;

  @BeforeEach
  void before() {
    sut = new JwtAuthenticationEntryPoint();
  }

  @Test
  void Authorizationヘッダーが無い場合に適切なレスポンスが返ること() throws Exception {

    when(request.getRequestURI()).thenReturn("/api/auth/login");
    when(request.getHeader("Authorization")).thenReturn(null);

    ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = getServletOutputStream(captureStream);
    when(response.getOutputStream()).thenReturn(servletOutputStream);

    sut.commence(request, response, authException);

    String responseJson = captureStream.toString(StandardCharsets.UTF_8);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> responseMap = mapper.readValue(responseJson, Map.class);

    assertThat(responseMap.get("error")).isEqualTo("Unauthorized");
    assertThat(responseMap.get("message")).isEqualTo(
        "認証が必要です。有効なJWTトークンを提供してください。");
    assertThat(responseMap.get("status")).isEqualTo(401);
    assertThat(responseMap.get("path")).isEqualTo("/api/auth/login");
    assertThat(responseMap.get("details")).isEqualTo("Authorizationヘッダーが存在しません");
    assertThat(responseMap.get("timestamp")).isNotNull();
  }

  @Test
  void Authorizationヘッダーの文頭にBearerが無い場合に適切なレスポンスが返ること()
      throws Exception {

    when(request.getRequestURI()).thenReturn("/api/auth/login");
    when(request.getHeader("Authorization")).thenReturn("DummyHeader");

    ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = getServletOutputStream(captureStream);
    when(response.getOutputStream()).thenReturn(servletOutputStream);

    sut.commence(request, response, authException);

    String responseJson = captureStream.toString(StandardCharsets.UTF_8);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> responseMap = mapper.readValue(responseJson, Map.class);

    assertThat(responseMap.get("error")).isEqualTo("Unauthorized");
    assertThat(responseMap.get("message")).isEqualTo(
        "認証が必要です。有効なJWTトークンを提供してください。");
    assertThat(responseMap.get("status")).isEqualTo(401);
    assertThat(responseMap.get("path")).isEqualTo("/api/auth/login");
    assertThat(responseMap.get("details")).isEqualTo(
        "Authorizationヘッダーの形式が正しくありません（Bearer　トークン　が必要)");
    assertThat(responseMap.get("timestamp")).isNotNull();
  }

  @Test
  void 無効または期限切れのトークンの場合に適切なレスポンスが返ること()
      throws Exception {

    when(request.getRequestURI()).thenReturn("/api/auth/login");
    when(request.getHeader("Authorization")).thenReturn("Bearer DummyToken");

    ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = getServletOutputStream(captureStream);
    when(response.getOutputStream()).thenReturn(servletOutputStream);

    sut.commence(request, response, authException);

    String responseJson = captureStream.toString(StandardCharsets.UTF_8);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> responseMap = mapper.readValue(responseJson, Map.class);

    assertThat(responseMap.get("error")).isEqualTo("Unauthorized");
    assertThat(responseMap.get("message")).isEqualTo(
        "認証が必要です。有効なJWTトークンを提供してください。");
    assertThat(responseMap.get("status")).isEqualTo(401);
    assertThat(responseMap.get("path")).isEqualTo("/api/auth/login");
    assertThat(responseMap.get("details")).isEqualTo(
        "JWTトークンが無効または期限切れです");
    assertThat(responseMap.get("timestamp")).isNotNull();
  }

  private static ServletOutputStream getServletOutputStream(ByteArrayOutputStream captureStream) {
    ServletOutputStream servletOutputStream = new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        captureStream.write(b);
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {
      }
    };
    return servletOutputStream;
  }
}
