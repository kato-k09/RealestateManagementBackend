package com.katok09.realestate.management.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.RegisterRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import com.katok09.realestate.management.dto.UserInfo;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

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
  void ログインとユーザー情報取得が正常に行えること() {

    // ログインリクエスト
    LoginRequest request = new LoginRequest("user1", "password123");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // トークンを使い保護されたAPIを実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<UserInfo> userResponse = restTemplate.exchange("/api/auth/me", HttpMethod.GET,
        entity, UserInfo.class);

    // ユーザー情報取得成功確認
    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userResponse.getBody().getUsername()).isEqualTo("user1");
  }

  @Test
  void 無効なユーザー名でログインした時に401エラーが返ること() {
    LoginRequest request = new LoginRequest("DummyUser", "password123");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login",
        request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("ユーザー名またはパスワードが間違っています。");
  }

  @Test
  void 無効なパスワードでログインした時に401エラーが返ること() {
    LoginRequest request = new LoginRequest("user1", "DummyPassword");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login",
        request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("ユーザー名またはパスワードが間違っています。");
  }

  @Test
  void 空のユーザー名でログインした時に400エラーが返ること() {
    LoginRequest request = new LoginRequest("", "password123");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login",
        request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("ユーザー名を入力してください。");
  }

  @Test
  void 空のパスワードでログインした時に400エラーが返ること() {
    LoginRequest request = new LoginRequest("user1", "");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login",
        request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("パスワードを入力してください。");
  }

  @Test
  void アカウントロックされているユーザーでログインした時に423エラーが返ること() {

    // ログインリクエスト
    LoginRequest request = new LoginRequest("accountLockedUser", "password123");
    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login",
        request, String.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
    assertThat(response.getBody()).contains("アカウントがロックされています。");
  }

  @Test
  void 無効なトークンで保護されたAPIにアクセスした時に401エラーが返ること() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange("/api/auth/me", HttpMethod.GET,
        entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("JWTトークンが無効または期限切れです");
  }

  @Test
  void トークンなしで保護されたAPIにアクセスした時に401エラーが返ること() {
    ResponseEntity<String> response = restTemplate.getForEntity("/api/auth/me", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");
  }

  @Test
  void ゲストログインとユーザー情報取得が正常に行えること() {

    // ゲストログインリクエスト
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/guest-login",
        null, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // トークンを使い保護されたAPIを実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<UserInfo> userResponse = restTemplate.exchange("/api/auth/me", HttpMethod.GET,
        entity, UserInfo.class);

    // ユーザー情報取得成功確認
    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userResponse.getBody().getUsername()).isEqualTo("guest");
  }

  @Test
  void ユーザー登録が正常に行えログインが行えること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("NewUser");
    registerRequest.setPassword("NewUserPassword");
    registerRequest.setEmail("newuser@example.com");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<Map> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, Map.class);

    // ユーザー登録成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().get("success")).isEqualTo(true);
    assertThat(response.getBody().get("message")).isEqualTo("ユーザー登録が完了しました");

    // ログインリクエスト
    LoginRequest request = new LoginRequest("NewUser", "NewUserPassword");
    ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void 重複したユーザー名で登録をした時に400エラーが返ること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("user1");
    registerRequest.setPassword("NewUserPassword");
    registerRequest.setEmail("newuser@example.com");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, String.class);

    // ユーザー登録失敗確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("このユーザー名は既に使用されています");
  }

  @Test
  void 重複したEmailで登録した時に400エラーが返ること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("NewUser");
    registerRequest.setPassword("NewUserPassword");
    registerRequest.setEmail("user1@example.com");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, String.class);

    // ユーザー登録失敗確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("このメールアドレスは既に使用されています");
  }

  @Test
  void 無効なEmailで登録した時に400エラーが返ること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("NewUser");
    registerRequest.setPassword("NewUserPassword");
    registerRequest.setEmail("abc");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, String.class);

    // ユーザー登録失敗確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("有効なメールアドレスを入力してください。");
  }

  @Test
  void 空のユーザー名で登録した時に400エラーが返ること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("");
    registerRequest.setPassword("NewUserPassword");
    registerRequest.setEmail("newuser@example.com");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, String.class);

    // ユーザー登録失敗確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("ユーザー名を入力してください。");
  }

  @Test
  void 空のパスワードで登録した時に400エラーが返ること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("NewUser");
    registerRequest.setPassword("");
    registerRequest.setEmail("newuser@example.com");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, String.class);

    // ユーザー登録失敗確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody())
        .contains("パスワードを入力してください")
        .contains("パスワードは6文字以上で入力してください。");
  }

  @Test
  void 短いパスワードで登録した時に400エラーが返ること() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("NewUser");
    registerRequest.setPassword("12345"); // 6文字以上が必須
    registerRequest.setEmail("newuser@example.com");
    registerRequest.setDisplayName("NewUser");

    ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register",
        registerRequest, String.class);

    // ユーザー登録失敗確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody())
        .contains("パスワードは6文字以上で入力してください。");
  }

  @Test
  void ユーザー情報更新が正常に行えること() {
    // ログインリクエスト
    LoginRequest request = new LoginRequest("user1", "password123");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // 更新リクエスト作成
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUsername");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("password123");
    updateRequest.setNewPassword("ChangedPassword");

    // 更新処理を実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<UpdateRequest> entity = new HttpEntity<>(updateRequest, headers);
    ResponseEntity<String> updateResponse = restTemplate.exchange("/api/auth/updateUserInfo",
        HttpMethod.PUT, entity, String.class);

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 更新後の情報を検証
    verifyUpdateUserInfo("ChangedUsername", "changed@example.com", "ChangedUser",
        "ChangedPassword");
  }

  @Test
  void 現在のパスワードを間違えてユーザー情報更新をした時に400エラーが返ること() {
    // ログインリクエスト
    LoginRequest request = new LoginRequest("user1", "password123");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // 更新リクエスト作成
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUsername");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("WrongPassword");
    updateRequest.setNewPassword("ChangedPassword");

    // 更新処理を実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<UpdateRequest> entity = new HttpEntity<>(updateRequest, headers);
    ResponseEntity<String> updateResponse = restTemplate.exchange("/api/auth/updateUserInfo",
        HttpMethod.PUT, entity, String.class);

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(updateResponse.getBody()).contains("現在のパスワードが間違っています");

    // 更新されていないことを検証
    verifyUpdateUserInfo("user1", "user1@example.com", "山田太郎", "password123");
  }

  @Test
  void 新しいパスワードを短いパスワードでユーザー情報更新をした時に400エラーが返ること() {
    // ログインリクエスト
    LoginRequest request = new LoginRequest("user1", "password123");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // 更新リクエスト作成
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUsername");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("password123");
    updateRequest.setNewPassword("12345"); // 6文字以上が必須

    // 更新処理を実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<UpdateRequest> entity = new HttpEntity<>(updateRequest, headers);
    ResponseEntity<String> updateResponse = restTemplate.exchange("/api/auth/updateUserInfo",
        HttpMethod.PUT, entity, String.class);

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(updateResponse.getBody()).contains("パスワードは6文字以上で入力してください。");

    // 更新されていないことを検証
    verifyUpdateUserInfo("user1", "user1@example.com", "山田太郎", "password123");
  }

  @Test
  void トークンなしでユーザー情報更新をした時401エラーが返ること() {
    // 更新リクエスト作成
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUsername");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("password123");
    updateRequest.setNewPassword("ChangedPassword");

    // 更新処理を実行
    HttpEntity<UpdateRequest> entity = new HttpEntity<>(updateRequest);
    ResponseEntity<String> response = restTemplate.exchange("/api/auth/updateUserInfo",
        HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");
  }

  @Test
  void 無効なトークンでユーザー情報更新をした時401エラーが返ること() {

    // 更新リクエスト作成
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUsername");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("password123");
    updateRequest.setNewPassword("ChangedPassword");

    // 更新処理を実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<UpdateRequest> entity = new HttpEntity<>(updateRequest, headers);
    ResponseEntity<String> response = restTemplate.exchange("/api/auth/updateUserInfo",
        HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("JWTトークンが無効または期限切れです");
  }

  @Test
  void ユーザー削除が正常に行えること() {
    // ログインリクエスト
    LoginRequest request = new LoginRequest("user1", "password123");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // 削除処理を実行
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> deletedUserResponse = restTemplate.exchange("/api/auth/deleteUser",
        HttpMethod.DELETE, entity, String.class);

    assertThat(deletedUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 削除後はログインできないことを確認
    verifyDeletedUser("user1", "password123");
  }

  @Test
  void トークンなしでユーザー削除をした時に401エラーが返ること() {
    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<String> response = restTemplate.exchange("/api/auth/deleteUser",
        HttpMethod.DELETE, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");
  }

  @Test
  void 無効なトークンでユーザー削除をした時に401エラーが返ること() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange("/api/auth/deleteUser",
        HttpMethod.DELETE, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("JWTトークンが無効または期限切れです");
  }

  /**
   * ユーザー情報が指定した値で更新されていることを検証
   *
   * @param username    ユーザー名
   * @param email       Email
   * @param displayName 表示名
   * @param password    パスワード
   */
  private void verifyUpdateUserInfo(String username, String email, String displayName,
      String password) {
    // 新しいユーザー名、パスワードでログインリクエスト
    LoginRequest request = new LoginRequest(username, password);
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/api/auth/login",
        request, LoginResponse.class);

    // ログイン成功確認
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = response.getBody().getToken();

    // ユーザー情報を取得
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<UserInfo> userResponse = restTemplate.exchange("/api/auth/me", HttpMethod.GET,
        entity, UserInfo.class);

    // ユーザー情報取得成功確認
    assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userResponse.getBody().getUsername()).isEqualTo(username);
    assertThat(userResponse.getBody().getEmail()).isEqualTo(email);
    assertThat(userResponse.getBody().getDisplayName()).isEqualTo(displayName);
  }

  /**
   * ユーザーが存在しないことを検証
   *
   * @param username ユーザー名
   * @param password パスワード
   */
  private void verifyDeletedUser(String username, String password) {
    LoginRequest request = new LoginRequest("user1", "password123");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
        "/api/auth/login",
        request, LoginResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
