package com.katok09.realestate.management.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.service.UserDetailsServiceImpl;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterTest {

  @Mock
  UserDetailsServiceImpl userDetailsService;
  @Mock
  JwtUtil jwtUtil;
  @Mock
  HttpServletRequest request;
  @Mock
  HttpServletResponse response;
  @Mock
  FilterChain chain;

  private JwtRequestFilter sut;

  @BeforeEach
  void before() {
    SecurityContextHolder.clearContext();
    sut = new JwtRequestFilter(userDetailsService, jwtUtil);
  }

  @Test
  void 有効なトークンの時認証情報をSecurityContextHolderに設定すること()
      throws ServletException, IOException {

    UserDetails userDetails = mock(UserDetails.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer DummyToken");
    when(jwtUtil.getUsernameFromToken("DummyToken")).thenReturn("DummyUser");
    when(userDetailsService.loadUserByUsername("DummyUser")).thenReturn(userDetails);
    when(jwtUtil.validateToken("DummyToken", userDetails)).thenReturn(true);

    sut.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(
        userDetails);
    assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void Authorizationヘッダーが無い時認証処理をしないこと()
      throws ServletException, IOException {

    when(request.getHeader("Authorization")).thenReturn(null);

    sut.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(jwtUtil, never()).getUsernameFromToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void トークンの文頭にBearerが無い時認証処理をしないこと()
      throws ServletException, IOException {

    when(request.getHeader("Authorization")).thenReturn("DummyToken");

    sut.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(jwtUtil, never()).getUsernameFromToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void 無効なトークンの時認証処理をしないこと()
      throws ServletException, IOException {

    UserDetails userDetails = mock(UserDetails.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer DummyToken");
    when(jwtUtil.getUsernameFromToken("DummyToken")).thenReturn("DummyUser");
    when(userDetailsService.loadUserByUsername("DummyUser")).thenReturn(userDetails);
    when(jwtUtil.validateToken("DummyToken", userDetails)).thenReturn(false);

    sut.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void トークン解析で例外が発生した時認証処理をしないこと()
      throws ServletException, IOException {

    UserDetails userDetails = mock(UserDetails.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer DummyToken");
    when(jwtUtil.getUsernameFromToken("DummyToken")).thenThrow(
        new RuntimeException("Invalid Token"));

    sut.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void 既に認証済みの時認証処理をしないこと()
      throws ServletException, IOException {

    SecurityContextHolder.getContext()
        .setAuthentication(mock(org.springframework.security.core.Authentication.class));

    when(request.getHeader("Authorization")).thenReturn("Bearer DummyToken");
    when(jwtUtil.getUsernameFromToken("DummyToken")).thenReturn("DummyUser");

    sut.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    verify(chain, times(1)).doFilter(request, response);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/searchRealestate", "/registerRealestate", "/updateRealestate",
      "/deleteRealestate", "/api/auth/validate", "/api/auth/me", "/api/auth/updateUserInfo",
      "/api/auth/deleteUser", "/api/admin/users"})
  void 認証の必要なエンドポイントへのリクエストはフィルターを通ること(String uri)
      throws ServletException {
    when(request.getRequestURI()).thenReturn(uri);

    boolean actual = sut.shouldNotFilter(request);

    assertThat(actual).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/auth/login", "/api/auth/guest-login", "/api/auth/register",
      "/swagger-ui/", "/v3/api-docs/", "/h2-console/"})
  void 認証の不要なエンドポイントへのリクエストはフィルターを通らないこと(String uri)
      throws ServletException {
    when(request.getRequestURI()).thenReturn(uri);

    boolean actual = sut.shouldNotFilter(request);

    assertThat(actual).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "/", "/unknown", "/api/auth", "/api/authx"})
  void 未定義のパスはフィルターを通ること(String uri)
      throws ServletException {
    when(request.getRequestURI()).thenReturn(uri);

    boolean actual = sut.shouldNotFilter(request);

    assertThat(actual).isFalse();
  }

  @Test
  void パスの大文字小文字を区別すること()
      throws ServletException {
    when(request.getRequestURI()).thenReturn("/API/AUTH/LOGIN");

    boolean actual = sut.shouldNotFilter(request);

    assertThat(actual).isFalse();
  }


}
