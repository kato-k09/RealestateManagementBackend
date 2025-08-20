package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ログイン失敗時のビジネスロジックを担当するサービス
 */
@Service
public class AccountLockService {

  private final UserRepository userRepository;

  public AccountLockService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // アカウントロックがかかるログイン連続失敗回数閾値
  @Value("${security.max-login-attempts}")
  private int maxLoginAttempts;

  // アカウントロックがかかる期間
  @Value("${security.account-lock-duration}")
  private int accountLockDurationMinutes;

  /**
   * アカウントロック期限が切れている場合のリセット処理
   *
   * @param loginRequest ログインリクエストDTO
   */
  @Transactional
  public void unlockIfAccountLockExpired(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
    if (user != null && user.getAccountLockedUntil() != null) {
      if (LocalDateTime.now().isAfter(user.getAccountLockedUntil())) {
        userRepository.updateLoginFailed(user.getId(), 0, null);
      }
    }
  }

  /**
   * アカウントロック関係の値のリセット（ログイン成功時に実行されます）
   *
   * @param id トークンから抽出したユーザーID
   */
  @Transactional
  public void resetAccountLockState(int id) {
    userRepository.updateLoginFailed(id, 0, null);
  }

  /**
   * ログイン失敗時の処理
   *
   * @param loginRequest ログインリクエストDTO
   */
  @Transactional
  public void handleLoginFailure(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

    if (user != null) {
      // ログイン連続失敗回数を加算します。
      int loginFailedAttempts = user.getLoginFailedAttempts() + 1;
      LocalDateTime accountLockedUntil = user.getAccountLockedUntil();

      // maxLoginAttemptsで指定された回数以上のログイン連続失敗回数となった場合、
      // accountLockDurationMinutesで指定された期間のアカウントロックがかかります。
      // 一度アカウントロックがかかった場合はアカウントロックがリセットされない限り再度アカウントロックがかからない仕様です。
      if (loginFailedAttempts >= maxLoginAttempts && user.getAccountLockedUntil() == null) {
        accountLockedUntil = LocalDateTime.now().plusMinutes(accountLockDurationMinutes);
      }
      userRepository.updateLoginFailed(user.getId(), loginFailedAttempts, accountLockedUntil);
    }
  }
}
