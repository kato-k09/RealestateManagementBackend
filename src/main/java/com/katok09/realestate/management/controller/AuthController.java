package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.RegisterRequest;
import com.katok09.realestate.management.dto.UserInfo;
import com.katok09.realestate.management.service.AuthService;
import com.katok09.realestate.management.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtUtil jwtUtil;

  /**
   * ユーザーログイン
   *
   * @param loginRequest ログインリクエスト（ユーザー名、パスワード）
   * @return ログインレスポンス（JWTトークン、ユーザー情報）
   */
  @PostMapping("/login")
  @Operation(summary = "ユーザーログイン", description = "ユーザー名とパスワードでログインし、JWTトークンを取得します")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    System.out.println("=== AuthController.login() 開始 ===");
    System.out.println("リクエスト受信: " + loginRequest.getUsername());

    try {
      // 入力値のバリデーション
      if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
        System.err.println("ユーザー名が空です");
        return ResponseEntity.badRequest()
            .body(createErrorResponse("VALIDATION_ERROR", "ユーザー名は必須です"));
      }

      if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
        System.err.println("パスワードが空です");
        return ResponseEntity.badRequest()
            .body(createErrorResponse("VALIDATION_ERROR", "パスワードは必須です"));
      }

      System.out.println("バリデーション完了、AuthService呼び出し開始");

      // ログイン認証実行
      LoginResponse loginResponse = authService.authenticate(loginRequest);

      System.out.println("AuthService.authenticate() 完了");

      // 一時的な回避策：単純なMapレスポンスを作成
      Map<String, Object> response = new HashMap<>();
      response.put("token", loginResponse.getToken());
      response.put("type", "Bearer");

      // UserInfo情報を個別に設定
      Map<String, Object> userInfo = new HashMap<>();
      if (loginResponse.getUserInfo() != null) {
        userInfo.put("id", loginResponse.getUserInfo().getId());
        userInfo.put("username", loginResponse.getUserInfo().getUsername());
        userInfo.put("displayName", loginResponse.getUserInfo().getDisplayName());
        userInfo.put("email", loginResponse.getUserInfo().getEmail());
        userInfo.put("role", loginResponse.getUserInfo().getRole());
      }
      response.put("userInfo", userInfo);

      System.out.println("レスポンス作成完了");
      System.out.println("=== AuthController.login() 正常終了 ===");

      return ResponseEntity.ok(response);

    } catch (BadCredentialsException e) {
      System.err.println("BadCredentialsException: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(createErrorResponse("INVALID_CREDENTIALS", e.getMessage()));
    } catch (Exception e) {
      System.err.println("予期しないエラー: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse("INTERNAL_ERROR",
              "システムエラーが発生しました: " + e.getMessage()));
    }
  }

  /**
   * デバッグ用: データベース接続テスト
   */
  @GetMapping("/debug/db-test")
  @Operation(summary = "データベース接続テスト", description = "データベース接続とusersテーブルの確認")
  public ResponseEntity<?> debugDbTest() {
    try {
      // AuthServiceのvalidateTokenメソッドを使用してテスト
      UserInfo testUser = authService.getUserInfo(1L); // ID:1のユーザー情報取得でテスト

      Map<String, Object> response = new HashMap<>();
      response.put("database_connection", "OK");
      response.put("test_user_found", testUser != null);
      if (testUser != null) {
        response.put("test_username", testUser.getUsername());
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put("database_connection", "ERROR");
      error.put("error_message", e.getMessage());
      error.put("error_class", e.getClass().getSimpleName());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }

  /**
   * デバッグ用: 簡単なログインテスト
   */
  @PostMapping("/debug/simple-login")
  @Operation(summary = "シンプルログインテスト", description = "最小限のレスポンスでログインテスト")
  public ResponseEntity<?> simpleLogin(@RequestBody LoginRequest loginRequest) {
    try {
      System.out.println("=== シンプルログインテスト開始 ===");

      // 認証のみ実行
      LoginResponse loginResponse = authService.authenticate(loginRequest);

      // 最小限のレスポンス
      Map<String, Object> simpleResponse = new HashMap<>();
      simpleResponse.put("success", true);
      simpleResponse.put("message", "ログイン成功");
      simpleResponse.put("username", loginResponse.getUserInfo().getUsername());
      simpleResponse.put("role", loginResponse.getUserInfo().getRole());
      simpleResponse.put("hasToken", loginResponse.getToken() != null);

      System.out.println("シンプルレスポンス作成完了");
      return ResponseEntity.ok(simpleResponse);

    } catch (Exception e) {
      System.err.println("シンプルログインエラー: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * 新規ユーザー登録
   *
   * @param registerRequest ユーザー登録リクエスト
   * @return 登録結果
   */
  @PostMapping("/register")
  @Operation(summary = "ユーザー登録", description = "新規ユーザーを登録します")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    try {
      // ユーザー登録実行
      authService.registerUser(registerRequest);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "ユーザー登録が完了しました");

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(createErrorResponse("VALIDATION_ERROR", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse("INTERNAL_ERROR", "ユーザー登録に失敗しました"));
    }
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
    try {
      String token = extractTokenFromRequest(request);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(createErrorResponse("MISSING_TOKEN", "Authorization ヘッダーが見つかりません"));
      }

      UserInfo userInfo = authService.validateToken(token);

      if (userInfo == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(createErrorResponse("INVALID_TOKEN", "無効なトークンです"));
      }

      Map<String, Object> response = new HashMap<>();
      response.put("valid", true);
      response.put("userInfo", userInfo);
      response.put("remainingMinutes", jwtUtil.getRemainingTimeInMinutes(token));

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(createErrorResponse("TOKEN_ERROR", "トークンの検証に失敗しました"));
    }
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
    try {
      String token = extractTokenFromRequest(request);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(createErrorResponse("MISSING_TOKEN", "Authorization ヘッダーが見つかりません"));
      }

      Long userId = jwtUtil.getUserIdFromToken(token);
      UserInfo userInfo = authService.getUserInfo(userId);

      return ResponseEntity.ok(userInfo);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(createErrorResponse("USER_ERROR", "ユーザー情報の取得に失敗しました"));
    }
  }

  /**
   * パスワード変更
   *
   * @param request               HTTPリクエスト
   * @param passwordChangeRequest パスワード変更リクエスト
   * @return 変更結果
   */
  @PostMapping("/change-password")
  @Operation(summary = "パスワード変更", description = "現在のユーザーのパスワードを変更します")
  public ResponseEntity<?> changePassword(HttpServletRequest request,
      @RequestBody Map<String, String> passwordChangeRequest) {
    try {
      String token = extractTokenFromRequest(request);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(createErrorResponse("MISSING_TOKEN", "Authorization ヘッダーが見つかりません"));
      }

      Long userId = jwtUtil.getUserIdFromToken(token);
      String oldPassword = passwordChangeRequest.get("oldPassword");
      String newPassword = passwordChangeRequest.get("newPassword");

      authService.changePassword(userId, oldPassword, newPassword);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "パスワードが変更されました");

      return ResponseEntity.ok(response);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(createErrorResponse("VALIDATION_ERROR", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse("INTERNAL_ERROR", "パスワード変更に失敗しました"));
    }
  }

  /**
   * ログアウト（トークン無効化） 現在の実装では、クライアント側でトークンを削除する 将来的にはトークンのブラックリスト機能を追加可能
   *
   * @return ログアウト結果
   */
  @PostMapping("/logout")
  @Operation(summary = "ログアウト", description = "ユーザーをログアウトします")
  public ResponseEntity<?> logout() {
    // 現在はクライアント側でトークン削除を想定
    // 必要に応じてサーバー側でのトークン無効化機能を追加

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

  /**
   * リクエストからJWTトークンを抽出
   *
   * @param request HTTPリクエスト
   * @return JWTトークン（Bearer プレフィックスなし）
   */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

  /**
   * エラーレスポンスを作成
   *
   * @param errorCode エラーコード
   * @param message   エラーメッセージ
   * @return エラーレスポンス
   */
  private Map<String, Object> createErrorResponse(String errorCode, String message) {
    Map<String, Object> error = new HashMap<>();
    error.put("error", true);
    error.put("errorCode", errorCode);
    error.put("message", message);
    error.put("timestamp", System.currentTimeMillis());
    return error;
  }
}