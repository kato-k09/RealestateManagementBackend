package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountLockService {

  private final UserRepository userRepository;

  public AccountLockService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Value("${security.max-login-attempts}")
  private int maxLoginAttempts;

  @Value("${security.account-lock-duration}")
  private int accountLockDurationMinutes;

  @Transactional
  public void unlockIfAccountLockExpired(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
    if (user != null && user.getAccountLockedUntil() != null) {
      if (LocalDateTime.now().isAfter(user.getAccountLockedUntil())) {
        userRepository.updateLoginFailed(user.getId(), 0, null);
      }
    }
  }

  @Transactional
  public void resetAccountLockState(int id) {
    userRepository.updateLoginFailed(id, 0, null);
  }

  @Transactional
  public void handleLoginFailure(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

    if (user != null) {
      int loginFailedAttempts = user.getLoginFailedAttempts() + 1;
      LocalDateTime accountLockedUntil = user.getAccountLockedUntil();

      if (loginFailedAttempts >= maxLoginAttempts && user.getAccountLockedUntil() == null) {
        accountLockedUntil = LocalDateTime.now().plusMinutes(accountLockDurationMinutes);
      }
      userRepository.updateLoginFailed(user.getId(), loginFailedAttempts, accountLockedUntil);
    }
  }
}
