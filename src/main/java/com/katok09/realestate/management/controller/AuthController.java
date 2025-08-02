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
            .body(jwtUtil.createErrorResponse("VALIDATION_ERROR", "ユーザー名は必須です"));
      }

      if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
        System.err.println("パスワードが空です");
        return ResponseEntity.badRequest()
            .body(jwtUtil.createErrorResponse("VALIDATION_ERROR", "パスワードは必須です"));
      }

      System.out.println("バリデーション完了、AuthService呼び出し開始");

      // ログイン認証実行
      LoginResponse loginResponse = authService.authenticate(loginRequest);

      System.out.println("AuthService.authenticate() 完了");

      return ResponseEntity.ok(loginResponse);

    } catch (BadCredentialsException e) {
      System.err.println("BadCredentialsException: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("INVALID_CREDENTIALS", e.getMessage()));
    } catch (Exception e) {
      System.err.println("予期しないエラー: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(jwtUtil.createErrorResponse("INTERNAL_ERROR",
              "システムエラーが発生しました: " + e.getMessage()));
    }
  }

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
          .body(jwtUtil.createErrorResponse("VALIDATION_ERROR", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(jwtUtil.createErrorResponse("INTERNAL_ERROR", "ユーザー登録に失敗しました"));
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

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("TOKEN_ERROR", "トークンの検証に失敗しました"));
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
      String token = jwtUtil.extractTokenFromRequest(request);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
                "Authorization ヘッダーが見つかりません"));
      }

      Long userId = jwtUtil.getUserIdFromToken(token);
      UserInfo userInfo = authService.getUserInfo(userId);

      return ResponseEntity.ok(userInfo);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("USER_ERROR", "ユーザー情報の取得に失敗しました"));
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
      String token = jwtUtil.extractTokenFromRequest(request);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
                "Authorization ヘッダーが見つかりません"));
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
          .body(jwtUtil.createErrorResponse("VALIDATION_ERROR", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(jwtUtil.createErrorResponse("INTERNAL_ERROR", "パスワード変更に失敗しました"));
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

}