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
        user.setLoginFailedAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.update(user);
      }
    }
  }

  @Transactional
  public void resetAccountLockState(User user) {
    user.setLoginFailedAttempts(0);
    user.setAccountLockedUntil(null);
    userRepository.update(user);
  }

  @Transactional
  public void handleLoginFailure(String username) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user != null) {
      int attempts = user.getLoginFailedAttempts() + 1;
      if (attempts >= maxLoginAttempts && user.getAccountLockedUntil() == null) {
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(accountLockDurationMinutes));
      }
      user.setLoginFailedAttempts(attempts);
      userRepository.update(user);
    }
  }
}
