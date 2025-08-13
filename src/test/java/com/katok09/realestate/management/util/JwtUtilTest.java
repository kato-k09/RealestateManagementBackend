package com.katok09.realestate.management.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.service.UserDetailsServiceImpl.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

  private JwtUtil sut;

  @BeforeEach
  void before() {
    sut = new JwtUtil();
    ReflectionTestUtils.setField(sut, "secret",
        "0d23a3165a573697c6b07e432ea358c4fa099b143d442c1a0e40e9ac6bc0368e");
    ReflectionTestUtils.setField(sut, "expirationInSeconds", 86400L);
  }

  @Test
  void Jwtトークンを生成できること() {

    String actual = sut.generateToken("DummyUser", "USER", 1);

    assertThat(actual).isNotNull().isNotEmpty();
  }

  @Test
  void トークンからユーザーIDを取得できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);

    int actual = sut.getUserIdFromToken(token);

    assertThat(actual).isEqualTo(1);
  }


  @Test
  void トークンからユーザー名を取得できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);

    String actual = sut.getUsernameFromToken(token);

    assertThat(actual).isEqualTo("DummyUser");
  }

  @Test
  void トークンからトークン有効期限が取得できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);

    Date actual = sut.getExpirationDateFromToken(token);

    Date expected = new Date(System.currentTimeMillis() + 86400 * 1000);
    assertThat(actual).isCloseTo(expected, 10 * 1000);
  }

  @Test
  void トークンから任意の情報が取得できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);

    Date actual = sut.getClaimFromToken(token, Claims::getIssuedAt);

    Date expected = new Date(System.currentTimeMillis());
    assertThat(actual).isCloseTo(expected, 10 * 1000);
  }

  @Test
  void トークンとユーザー情報を照らし合わせ有効なトークンか確認できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);
    User user = new User();
    user.setUsername("DummyUser");
    CustomUserPrincipal userDetails = new CustomUserPrincipal(user);

    boolean actual = sut.validateToken(token, userDetails);

    assertThat(actual).isTrue();
  }

  @Test
  void トークンとユーザー情報を照らし合わせユーザー名が異なっている時に無効なトークンと確認できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);
    User user = new User();
    user.setUsername("FakeDummyUser");
    CustomUserPrincipal userDetails = new CustomUserPrincipal(user);

    boolean actual = sut.validateToken(token, userDetails);

    assertThat(actual).isFalse();
  }

  @Test
  void トークンとユーザー情報を照らし合わせ期限切れのトークンを無効なトークンと確認できること() {

    // tokenはgenerateToken("DummyUser", "USER", 1)で生成
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJJZCI6MSwic3ViIjoiRHVtbXlVc2VyIiwiaWF0IjoxNzU1MDcwMDY0LCJleHAiOjE3NTUwNzAwNjV9.rf6SOWcZiistJSd1iy0hpvNFmtp_aaUePBzTN8cLhog";
    User user = new User();
    user.setUsername("DummyUser");
    CustomUserPrincipal userDetails = new CustomUserPrincipal(user);

    boolean actual = sut.validateToken(token, userDetails);

    assertThat(actual).isFalse();
  }

  @Test
  void 期限切れのトークンでないことを確認できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);

    boolean actual = sut.isTokenValid(token);

    assertThat(actual).isTrue();
  }

  @Test
  void 期限切れのトークンであることを確認できること() {

    // tokenはgenerateToken("DummyUser", "USER", 1)で生成
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJJZCI6MSwic3ViIjoiRHVtbXlVc2VyIiwiaWF0IjoxNzU1MDcwMDY0LCJleHAiOjE3NTUwNzAwNjV9.rf6SOWcZiistJSd1iy0hpvNFmtp_aaUePBzTN8cLhog";

    boolean actual = sut.isTokenValid(token);

    assertThat(actual).isFalse();
  }

  @Test
  void トークンの残り有効期限分数を確認できること() {

    String token = sut.generateToken("DummyUser", "USER", 1);

    long actual = sut.getRemainingTimeInMinutes(token);

    assertThat(actual).isBetween(1439L, 1440L);
  }

  @Test
  void httpリクエストからトークンを取得できること() {

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer DummyToken");

    String actual = sut.extractTokenFromRequest(mockRequest);

    assertThat(actual).isEqualTo("DummyToken");
  }

  @Test
  void httpリクエストからトークン取得時にBearerが無い時nullが返ること() {

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("DummyToken");

    String actual = sut.extractTokenFromRequest(mockRequest);

    assertThat(actual).isNull();
  }

  @Test
  void エラーコードとエラーメッセージを渡すことでエラーレスポンスが返ること() {

    Map<String, Object> actual = sut.createErrorResponse("INVALID_TOKEN", "無効なトークンです");

    assertThat(actual.get("error")).isEqualTo(true);
    assertThat(actual.get("errorCode")).isEqualTo("INVALID_TOKEN");
    assertThat(actual.get("message")).isEqualTo("無効なトークンです");
    assertThat(actual.get("timestamp")).isNotNull();
  }

}
