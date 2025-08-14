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
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証・認可に関するビジネスロジックを担当するサービス
 */
@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AccountLockService accountLockService;
  private final UserRepository userRepository;
  private final RealestateService realestateService;

  @Autowired
  public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, AccountLockService accountLockService, UserRepository userRepository,
      RealestateService realestateService) {

    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.accountLockService = accountLockService;
    this.userRepository = userRepository;
    this.realestateService = realestateService;
  }

  @Value("${security.max-login-attempts}")
  private int maxLoginAttempts;

  @Value("${security.account-lock-duration}")
  private int accountLockDurationMinutes;

  /**
   * ユーザー認証とJWTトークン生成
   *
   * @param loginRequest ログインリクエスト
   * @return ログインレスポンス（JWTトークンとユーザー情報）
   * @throws BadCredentialsException 認証失敗時
   */
  public LoginResponse authenticate(LoginRequest loginRequest) {
    try {
      // アカウントロックの期限が過ぎている場合はログイン失敗回数、アカウントロック時間をリセット
      accountLockService.unlockIfAccountLockExpired(loginRequest);

      // Spring Securityの認証マネージャーを使用してユーザー認証
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()
          )
      );

      // 認証成功 - ユーザー情報を取得
      UserDetailsServiceImpl.CustomUserPrincipal userPrincipal =
          (UserDetailsServiceImpl.CustomUserPrincipal) authentication.getPrincipal();

      User user = userPrincipal.getUser();

      // ログイン失敗回数、アカウントロック時間をリセット
      accountLockService.resetAccountLockState(user.getId());

      // 最終ログイン日時を更新
      updateLastLoginTime(user.getId());

      // JWTトークンを生成
      String jwtToken = jwtUtil.generateToken(
          user.getUsername(),
          user.getRole(),
          user.getId()
      );

      // ユーザー情報をDTOに変換
      UserInfo userInfo = new UserInfo(
          user.getId(),
          user.getUsername(),
          user.getDisplayName(),
          user.getEmail(),
          user.getRole()
      );

      return new LoginResponse(jwtToken, userInfo);

    } catch (LockedException e) {
      accountLockService.handleLoginFailure(loginRequest);
      User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
      long remainingSeconds = (long) accountLockDurationMinutes * 60;
      if (user != null) {
        remainingSeconds = LocalDateTime.now()
            .until(user.getAccountLockedUntil(), ChronoUnit.SECONDS);
      }
      throw new LockedException(
          "アカウントがロックされています。あと"
              + remainingSeconds + "秒後にロックが解除されます。");
    } catch (BadCredentialsException e) {
      accountLockService.handleLoginFailure(loginRequest);
      throw new BadCredentialsException("ユーザー名またはパスワードが間違っています。");
    } catch (Exception e) {
      throw new RuntimeException("認証処理中にエラーが発生しました。", e);
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
    userRepository.registerUser(newUser);
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
  }

  /**
   * ユーザー更新時のバリデーション
   */
  private void validateUserUpdate(UpdateRequest request, User user) {
    // ユーザー名の重複チェック
    if (userRepository.existsByUsernameNotSelfId(request.getUsername(), user.getId())) {
      throw new IllegalArgumentException("このユーザー名は既に使用されています");
    }

    // メールアドレスの重複チェック
    if (userRepository.existsByEmailNotSelfId(request.getEmail(), user.getId())) {
      throw new IllegalArgumentException("このメールアドレスは既に使用されています");
    }

    if (request.getCurrentPassword() != null) {
      // 現在のパスワードを確認
      if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
        throw new IllegalArgumentException("現在のパスワードが間違っています");
      }
      if (request.getNewPassword() == null || request.getNewPassword() == "") {
        throw new IllegalArgumentException("新しいパスワードを入力してください。");
      }
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
  public void changeUserInfo(int userId, UpdateRequest updateRequest) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

    if (user.getRole().equals("GUEST")) {
      throw new IllegalArgumentException("ゲストユーザー情報は変更できません");
    }

    validateUserUpdate(updateRequest, user);

    if (updateRequest.getCurrentPassword() != null && updateRequest.getNewPassword() != null) {
      String hashedPassword = passwordEncoder.encode(updateRequest.getNewPassword());
      updateRequest.setNewPassword(hashedPassword);
      userRepository.updatePassword(userId, updateRequest.getNewPassword());
    }

    userRepository.updateUser(userId, updateRequest);
  }

  /**
   * 最終ログイン日時を更新
   */
  private void updateLastLoginTime(int userId) {
    try {
      userRepository.updateLastLoginAt(userId, LocalDateTime.now());
    } catch (Exception e) {
      // ログイン日時の更新に失敗してもログイン処理は継続
    }
  }

  /**
   * ユーザー情報を取得
   *
   * @param userId ユーザーID
   * @return ユーザー情報
   */
  public UserInfo getUserInfo(int userId) {
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
   * ユーザーの削除
   *
   * @param userId ユーザーID
   */
  @Transactional
  public void deleteUser(int userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません。"));

    if (user.getRole().equals("GUEST")) {
      throw new IllegalArgumentException("ゲストユーザーは削除できません。");
    }
    if (user.getRole().equals("ADMIN")) {
      throw new IllegalArgumentException("管理者ユーザーは削除できません。");
    }

    realestateService.deleteRealestateByUserId(userId);
    userRepository.deleteUserById(userId);
  }
}
