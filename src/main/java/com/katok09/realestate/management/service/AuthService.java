package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.RegisterRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import com.katok09.realestate.management.dto.UserInfo;
import com.katok09.realestate.management.repository.UserRepository;
import com.katok09.realestate.management.util.JwtUtil;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証・認可に関するビジネスロジックを担当するサービス
 */
@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private AuthenticationManager authenticationManager;

  /**
   * ユーザー認証とJWTトークン生成
   *
   * @param loginRequest ログインリクエスト
   * @return ログインレスポンス（JWTトークンとユーザー情報）
   * @throws BadCredentialsException 認証失敗時
   */
  public LoginResponse authenticate(LoginRequest loginRequest) {
    System.out.println("=== AuthService.authenticate() 開始 ===");
    System.out.println("Username: " + loginRequest.getUsername());

    try {
      // Spring Securityの認証マネージャーを使用してユーザー認証
      System.out.println("認証マネージャーで認証開始...");
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()
          )
      );
      System.out.println("認証成功");

      // 認証成功 - ユーザー情報を取得
      UserDetailsServiceImpl.CustomUserPrincipal userPrincipal =
          (UserDetailsServiceImpl.CustomUserPrincipal) authentication.getPrincipal();

      User user = userPrincipal.getUser();
      System.out.println("ユーザー取得成功: " + user.getUsername());

      // 最終ログイン日時を更新
      System.out.println("最終ログイン日時更新中...");
      updateLastLoginTime(user.getId());
      System.out.println("最終ログイン日時更新完了");

      // JWTトークンを生成
      System.out.println("JWTトークン生成中...");
      String jwtToken = jwtUtil.generateToken(
          user.getUsername(),
          user.getRole(),
          user.getId()
      );
      System.out.println("JWTトークン生成完了");

      // ユーザー情報をDTOに変換
      UserInfo userInfo = new UserInfo(
          user.getId(),
          user.getUsername(),
          user.getDisplayName(),
          user.getEmail(),
          user.getRole()
      );

      System.out.println("=== AuthService.authenticate() 正常終了 ===");
      return new LoginResponse(jwtToken, userInfo);

    } catch (DisabledException e) {
      System.err.println("アカウント無効エラー: " + e.getMessage());
      e.printStackTrace();
      throw new BadCredentialsException("アカウントが無効です");
    } catch (AuthenticationException e) {
      System.err.println("認証エラー: " + e.getMessage());
      e.printStackTrace();
      throw new BadCredentialsException("ユーザー名またはパスワードが間違っています");
    } catch (Exception e) {
      System.err.println("予期しないエラー: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("認証処理中にエラーが発生しました", e);
    }
  }

  /**
   * 新規ユーザー登録
   *
   * @param registerRequest ユーザー登録リクエスト
   * @throws IllegalArgumentException 重複やバリデーションエラー時
   */
  @Transactional
  public void registerUser(RegisterRequest registerRequest) {
    // 重複チェック
    validateUserRegistration(registerRequest);

    // 新規ユーザー作成
    User newUser = new User();
    newUser.setUsername(registerRequest.getUsername());
    newUser.setEmail(registerRequest.getEmail());
    newUser.setDisplayName(registerRequest.getDisplayName());
    newUser.setRole("USER"); // デフォルトロール
    newUser.setEnabled(true);
    newUser.setDeleted(false);

    // パスワードをハッシュ化
    String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
    newUser.setPassword(hashedPassword);

    // データベースに保存
    userRepository.save(newUser);
  }

  /**
   * ユーザー登録時のバリデーション
   */
  private void validateUserRegistration(RegisterRequest request) {
    // ユーザー名の重複チェック
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("このユーザー名は既に使用されています");
    }

    // メールアドレスの重複チェック
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("このメールアドレスは既に使用されています");
    }

    // 基本的なバリデーション
    if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("ユーザー名は必須です");
    }

    if (request.getPassword() == null || request.getPassword().length() < 6) {
      throw new IllegalArgumentException("パスワードは6文字以上で入力してください");
    }

    if (request.getEmail() == null || !request.getEmail().contains("@")) {
      throw new IllegalArgumentException("有効なメールアドレスを入力してください");
    }

    if (request.getDisplayName() == null || request.getDisplayName().trim().isEmpty()) {
      throw new IllegalArgumentException("表示名は必須です");
    }
  }

  /**
   * JWTトークンの有効性を検証
   *
   * @param token JWTトークン
   * @return 有効な場合はユーザー情報、無効な場合はnull
   */
  public UserInfo validateToken(String token) {
    try {
      if (!jwtUtil.isTokenValid(token)) {
        return null;
      }

      String username = jwtUtil.getUsernameFromToken(token);
      User user = userRepository.findByUsername(username).orElse(null);

      if (user == null || !user.isEnabled() || user.isDeleted()) {
        return null;
      }

      return new UserInfo(
          user.getId(),
          user.getUsername(),
          user.getDisplayName(),
          user.getEmail(),
          user.getRole()
      );

    } catch (Exception e) {
      return null;
    }
  }

  @Transactional
  public void changeUserInfo(Long userId, UpdateRequest updateRequest) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

    // 現在のパスワードを確認
    if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("現在のパスワードが間違っています");
    }

    // 新しいパスワードのバリデーション
    if (updateRequest.getNewPassword() == null || updateRequest.getNewPassword().length() < 6) {
      throw new IllegalArgumentException("新しいパスワードは6文字以上で入力してください");
    }

    RegisterRequest validateRequest = new RegisterRequest();
    validateRequest.setUsername(updateRequest.getUsername());
    validateRequest.setEmail(updateRequest.getEmail());
    validateRequest.setDisplayName(updateRequest.getDisplayName());
    // パスワードはあえて現在のパスワードを入れvalidateを回避
    validateRequest.setPassword(updateRequest.getCurrentPassword());
    validateUserRegistration(validateRequest);

    String hashedPassword = passwordEncoder.encode(updateRequest.getNewPassword());
    updateRequest.setNewPassword(hashedPassword);

    userRepository.changeUserInfo(userId, updateRequest);
  }

  /**
   * パスワード変更
   *
   * @param userId      ユーザーID
   * @param oldPassword 現在のパスワード
   * @param newPassword 新しいパスワード
   */
  @Transactional
  public void changePassword(Long userId, String oldPassword, String newPassword) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

    // 現在のパスワードを確認
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("現在のパスワードが間違っています");
    }

    // 新しいパスワードのバリデーション
    if (newPassword == null || newPassword.length() < 6) {
      throw new IllegalArgumentException("新しいパスワードは6文字以上で入力してください");
    }

    // パスワードをハッシュ化して更新
    String hashedPassword = passwordEncoder.encode(newPassword);
    userRepository.updatePassword(userId, hashedPassword);
  }

  /**
   * 最終ログイン日時を更新
   */
  private void updateLastLoginTime(Long userId) {
    try {
      userRepository.updateLastLoginAt(userId, LocalDateTime.now());
    } catch (Exception e) {
      // ログイン日時の更新に失敗してもログイン処理は継続
      System.err.println("最終ログイン日時の更新に失敗しました: " + e.getMessage());
    }
  }

  /**
   * ユーザー情報を取得
   *
   * @param userId ユーザーID
   * @return ユーザー情報
   */
  public UserInfo getUserInfo(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

    return new UserInfo(
        user.getId(),
        user.getUsername(),
        user.getDisplayName(),
        user.getEmail(),
        user.getRole()
    );
  }

  /**
   * ユーザーの有効/無効状態を切り替え（管理者機能）
   *
   * @param userId  ユーザーID
   * @param enabled 有効フラグ
   */
  @Transactional
  public void toggleUserEnabled(Long userId, boolean enabled) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

    userRepository.updateEnabled(userId, enabled);
  }
}