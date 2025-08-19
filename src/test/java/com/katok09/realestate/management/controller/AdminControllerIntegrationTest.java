package com.katok09.realestate.management.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.StatusRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void before() {
    jdbcTemplate.execute("DROP ALL OBJECTS");
    jdbcTemplate.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
    jdbcTemplate.execute("RUNSCRIPT FROM 'classpath:data.sql'");
  }

  @Test
  void 削除済み以外の全ユーザー情報を取得できること() {
    // ログインリクエスト
    String token = performLogin("admin", "password123");

    // 削除済み以外の全ユーザー情報を取得
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<List<User>> userResponse = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, new ParameterizedTypeReference<List<User>>() {
        });

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userResponse.getBody().size()).isEqualTo(6);
  }

  @Test
  void ADMINロールを持たないユーザーで全ユーザー情報を取得した時401エラーが返ること() {
    // ログインリクエスト
    String token = performLogin("user1", "password123");

    // 削除済み以外の全ユーザー情報を取得
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> userResponse = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userResponse.getBody()).contains("JWTトークンが無効または期限切れです");
  }

  @Test
  void 無効なトークンで全ユーザー情報を取得した時401エラーが返ること() {
    // ログインリクエスト
    String token = performLogin("user1", "password123");

    // 削除済み以外の全ユーザー情報を取得
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> userResponse = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userResponse.getBody()).contains("JWTトークンが無効または期限切れです");
  }

  @Test
  void トークンなしで全ユーザー情報を取得した時401エラーが返ること() {
    // ログインリクエスト
    String token = performLogin("user1", "password123");

    // 削除済み以外の全ユーザー情報を取得
    HttpHeaders headers = new HttpHeaders();
    // headers.setBearerAuth(token); 認証トークンをヘッダーに付与しない
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> userResponse = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userResponse.getBody()).contains("Authorizationヘッダーが存在しません");
  }

  @Test
  void 指定したユーザーのステータスを変更できること() {
    String token = performLogin("admin", "password123");

    // ステータス更新情報作成
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");
    statusRequest.setEnabled(false); // enabledを変更
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    // 指定したユーザーのステータスを変更
    int userId = 2;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<StatusRequest> entity = new HttpEntity<>(statusRequest, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/admin/users/" + userId + "/updateStatus",
        HttpMethod.PUT,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 削除済み以外の全ユーザー情報を取得
    ResponseEntity<List<User>> userList = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, new ParameterizedTypeReference<List<User>>() {
        });

    User filtered = userList.getBody().stream()
        .filter(p -> p.getId() == userId)
        .findFirst()
        .orElse(null);

    assertThat(filtered.isEnabled()).isFalse();
  }

  @Test
  void 自身のステータスを変更した時400エラーが返ること() {
    String token = performLogin("admin", "password123");

    // ステータス更新情報作成
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");
    statusRequest.setEnabled(false); // enabledを変更
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    // 指定したユーザーのステータスを変更
    int userId = 1; // 自身のユーザーID
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<StatusRequest> entity = new HttpEntity<>(statusRequest, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/admin/users/" + userId + "/updateStatus",
        HttpMethod.PUT,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    // 削除済み以外の全ユーザー情報を取得
    ResponseEntity<List<User>> userList = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, new ParameterizedTypeReference<List<User>>() {
        });

    User filtered = userList.getBody().stream()
        .filter(p -> p.getId() == userId)
        .findFirst()
        .orElse(null);

    // ステータスが変更されていないことを検証
    assertThat(filtered.isEnabled()).isTrue();
    assertThat(userResponse.getBody()).contains("自身のステータスは変更できません。");
  }

  @Test
  void ADMINロールを持たないユーザーが指定したユーザーのステータスをした時に401エラーが返ること() {
    // ログインリクエスト
    String token = performLogin("user1", "password123");

    // ステータス更新情報作成
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");
    statusRequest.setEnabled(false); // enabledを変更
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    // 指定したユーザーのステータスを変更
    int userId = 1;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<StatusRequest> entity = new HttpEntity<>(statusRequest, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/admin/users/" + userId + "/updateStatus",
        HttpMethod.PUT,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userResponse.getBody()).contains("JWTトークンが無効または期限切れです");

    // ステータスが変更されていないことを確認
    verifyNotUpdateStatus(userId);
  }

  @Test
  void 無効なトークンで指定したユーザーのステータスをした時に401エラーが返ること() {
    // ログインリクエスト
    String token = performLogin("admin", "password123");

    // ステータス更新情報作成
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");
    statusRequest.setEnabled(false); // enabledを変更
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    // 指定したユーザーのステータスを変更
    int userId = 2;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<StatusRequest> entity = new HttpEntity<>(statusRequest, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/admin/users/" + userId + "/updateStatus",
        HttpMethod.PUT,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userResponse.getBody()).contains("JWTトークンが無効または期限切れです");

    // ステータスが変更されていないことを確認
    verifyNotUpdateStatus(userId);
  }

  @Test
  void トークンなしで指定したユーザーのステータスをした時に401エラーが返ること() {
    // ログインリクエスト
    String token = performLogin("admin", "password123");

    // ステータス更新情報作成
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");
    statusRequest.setEnabled(false); // enabledを変更
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    // 指定したユーザーのステータスを変更
    int userId = 2;
    HttpHeaders headers = new HttpHeaders();
    // headers.setBearerAuth(token); 認証トークンをヘッダーに付与しない
    HttpEntity<StatusRequest> entity = new HttpEntity<>(statusRequest, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/admin/users/" + userId + "/updateStatus",
        HttpMethod.PUT,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userResponse.getBody()).contains("Authorizationヘッダーが存在しません");

    // ステータスが変更されていないことを確認
    verifyNotUpdateStatus(userId);
  }

  @Test
  void 存在しないユーザーのステータスを変更した時404エラーが返ること() {
    String token = performLogin("admin", "password123");

    // ステータス更新情報作成
    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");
    statusRequest.setEnabled(false); // enabledを変更
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    // 指定したユーザーのステータスを変更
    int userId = 999;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<StatusRequest> entity = new HttpEntity<>(statusRequest, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/admin/users/" + userId + "/updateStatus",
        HttpMethod.PUT,
        entity, String.class);

    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(userResponse.getBody()).contains("ユーザーが見つかりません。");
  }

  /**
   * ユーザー名とパスワードから認証しトークンを取得
   *
   * @param username ユーザー名
   * @param password パスワード
   * @return トークン
   */
  private String performLogin(String username, String password) {
    // ログインリクエスト
    LoginRequest request = new LoginRequest(username, password);
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();
    return token;
  }

  /**
   * 指定IDユーザーのenabledがtrueのままかを検証
   *
   * @param userId 確認するユーザーID
   */
  private void verifyNotUpdateStatus(int userId) {
    // ADMINロールユーザーでログインリクエスト
    String token = performLogin("admin", "password123");

    // 削除済み以外の全ユーザー情報を取得
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<List<User>> userList = restTemplate.exchange("/api/admin/users",
        HttpMethod.GET,
        entity, new ParameterizedTypeReference<List<User>>() {
        });

    User filtered = userList.getBody().stream()
        .filter(p -> p.getId() == userId)
        .findFirst()
        .orElse(null);

    // enabledがtrueのままであることを確認
    assertThat(filtered.isEnabled()).isTrue();
  }

}
