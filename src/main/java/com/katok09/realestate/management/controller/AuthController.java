package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.RegisterRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import com.katok09.realestate.management.dto.UserInfo;
import com.katok09.realestate.management.service.AuthService;
import com.katok09.realestate.management.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証関連のREST APIエンドポイントを提供するコントローラー
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Reactのポートに合わせる
@Tag(name = "認証API", description = "ログイン、ユーザー登録、トークン検証などの認証関連API")
@Validated
public class AuthController {

  private final AuthService authService;
  private final JwtUtil jwtUtil;

  public AuthController(AuthService authService, JwtUtil jwtUtil) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
  }

  /**
   * ユーザーログイン
   *
   * @param loginRequest ログインリクエスト（ユーザー名、パスワード）
   * @return ログインレスポンス（JWTトークン、ユーザー情報）
   */
  @PostMapping("/login")
  @Operation(summary = "ユーザーログイン", description = "ユーザー名とパスワードでログインし、JWTトークンを取得します")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

    LoginResponse loginResponse = authService.authenticate(loginRequest);

    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/guest-login")
  @Operation(summary = "ゲストユーザーログイン", description = "ゲストユーザーでログインします")
  public ResponseEntity<?> guestLogin() {

    LoginRequest loginRequest = new LoginRequest("guest", "guest123");

    return login(loginRequest);
  }

  @PostMapping("/register")
  @Operation(summary = "ユーザー登録", description = "新規ユーザーを登録します")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

    authService.registerUser(registerRequest);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "ユーザー登録が完了しました");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * JWTトークンの有効性確認
   *
   * @param request HTTPリクエスト
   * @return トークンの有効性とユーザー情報
   */
  @GetMapping("/validate")
  @Operation(summary = "トークン有効性確認", description = "JWTトークンの有効性を確認し、ユーザー情報を取得します")
  public ResponseEntity<?> validateToken(HttpServletRequest request) {

    String token = jwtUtil.extractTokenFromRequest(request);

    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
              "Authorization ヘッダーが見つかりません"));
    }

    UserInfo userInfo = authService.validateToken(token);

    if (userInfo == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("INVALID_TOKEN", "無効なトークンです"));
    }

    Map<String, Object> response = new HashMap<>();
    response.put("valid", true);
    response.put("userInfo", userInfo);
    response.put("remainingMinutes", jwtUtil.getRemainingTimeInMinutes(token));

    return ResponseEntity.ok(response);
  }

  /**
   * 現在のユーザー情報取得
   *
   * @param request HTTPリクエスト
   * @return ユーザー情報
   */
  @GetMapping("/me")
  @Operation(summary = "現在のユーザー情報取得", description = "JWTトークンから現在のユーザー情報を取得します")
  public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {

    String token = jwtUtil.extractTokenFromRequest(request);

    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
              "Authorization ヘッダーが見つかりません"));
    }

    int userId = jwtUtil.getUserIdFromToken(token);
    UserInfo userInfo = authService.getUserInfo(userId);

    return ResponseEntity.ok(userInfo);
  }

  @PutMapping("/changeUserInfo")
  @Operation(summary = "ユーザー情報変更", description = "現在のユーザー情報を変更します")
  public ResponseEntity<?> changeUserInfo(HttpServletRequest request,
      @Valid @RequestBody UpdateRequest updateRequest) {

    String token = jwtUtil.extractTokenFromRequest(request);

    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
              "Authorization ヘッダーが見つかりません"));
    }

    int userId = jwtUtil.getUserIdFromToken(token);

    authService.changeUserInfo(userId, updateRequest);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "ユーザー情報が変更されました");

    return ResponseEntity.ok(response);
  }

  /**
   * ログアウト（トークン無効化） 現在の実装では、クライアント側でトークンを削除する 将来的にはトークンのブラックリスト機能を追加可能
   *
   * @return ログアウト結果
   */
  @PostMapping("/logout")
  @Operation(summary = "ログアウト", description = "ユーザーをログアウトします")
  public ResponseEntity<?> logout() {

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "ログアウトしました");

    return ResponseEntity.ok(response);
  }

  /**
   * ヘルスチェック用エンドポイント
   *
   * @return システム状態
   */
  @GetMapping("/health")
  @Operation(summary = "ヘルスチェック", description = "認証システムの動作状況を確認します")
  public ResponseEntity<Map<String, Object>> healthCheck() {

    Map<String, Object> response = new HashMap<>();
    response.put("status", "OK");
    response.put("service", "Auth Service");
    response.put("timestamp", System.currentTimeMillis());

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/deleteUser")
  @Operation(summary = "ユーザー削除", description = "ユーザーを削除します")
  public ResponseEntity<Map<String, Object>> deleteUser(HttpServletRequest request) {

    String token = jwtUtil.extractTokenFromRequest(request);

    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
              "Authorization ヘッダーが見つかりません"));
    }

    int userId = jwtUtil.getUserIdFromToken(token);

    authService.deleteUser(userId);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "ユーザーが削除されました");

    return ResponseEntity.ok(response);
  }

}