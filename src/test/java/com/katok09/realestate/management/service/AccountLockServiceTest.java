package com.katok09.realestate.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AccountLockServiceTest {

  @Mock
  private UserRepository repository;

  private AccountLockService sut;

  @BeforeEach
  void before() {
    sut = new AccountLockService(repository);
    ReflectionTestUtils.setField(sut, "maxLoginAttempts", 5);
    ReflectionTestUtils.setField(sut, "accountLockDurationMinutes", 30);
  }

  @Test
  void ログインリクエストをしたユーザーのアカウントロック期限が切れていた場合ログイン連続失敗回数とアカウントロック期限をリセットできること() {

    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("DummyUser");
    mockUser.setDisplayName("DummyUser");
    mockUser.setEmail("dummy@example.com");
    mockUser.setRole("USER");
    mockUser.setLoginFailedAttempts(999);
    mockUser.setAccountLockedUntil(LocalDateTime.now().minusMinutes(30));
    LoginRequest loginRequest = new LoginRequest("DummyUser", "DummyPassword");

    when(repository.findByUsername("DummyUser")).thenReturn(Optional.of(mockUser));

    ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> loginFailedAttemptsCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<LocalDateTime> accountLockedUntilCaptor = ArgumentCaptor.forClass(
        LocalDateTime.class);

    doNothing().when(repository)
        .updateLoginFailed(userIdCaptor.capture(), loginFailedAttemptsCaptor.capture(),
            accountLockedUntilCaptor.capture());

    sut.unlockIfAccountLockExpired(loginRequest);

    verify(repository, times(1)).findByUsername("DummyUser");
    verify(repository, times(1)).updateLoginFailed(anyInt(), anyInt(), eq(null));
    assertThat(userIdCaptor.getValue()).isEqualTo(999);
    assertThat(loginFailedAttemptsCaptor.getValue()).isEqualTo(0);
    assertThat(accountLockedUntilCaptor.getValue()).isNull();
  }

  @Test
  void ログインリクエストをしたユーザーのアカウントロック期限が切れていない場合ログイン連続失敗回数とアカウントロック期限をリセットされないこと() {

    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("DummyUser");
    mockUser.setDisplayName("DummyUser");
    mockUser.setEmail("dummy@example.com");
    mockUser.setRole("USER");
    mockUser.setLoginFailedAttempts(999);
    mockUser.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
    LoginRequest loginRequest = new LoginRequest("DummyUser", "DummyPassword");

    when(repository.findByUsername("DummyUser")).thenReturn(Optional.of(mockUser));

    sut.unlockIfAccountLockExpired(loginRequest);

    verify(repository, times(1)).findByUsername("DummyUser");
    verify(repository, never()).updateLoginFailed(999, 0, null);
  }

  @Test
  void 指定されたユーザーのログイン失敗情報をリセットできること() {

    doNothing().when(repository).updateLoginFailed(2, 0, null);

    sut.resetAccountLockState(2);

    verify(repository, times(1)).updateLoginFailed(2, 0, null);
  }

  @Test
  void ログインリクエストをしたユーザーのログイン連続失敗回数が1加算されること() {

    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("DummyUser");
    mockUser.setDisplayName("DummyUser");
    mockUser.setEmail("dummy@example.com");
    mockUser.setRole("USER");
    mockUser.setLoginFailedAttempts(0);
    mockUser.setAccountLockedUntil(null);
    LoginRequest loginRequest = new LoginRequest("DummyUser", "DummyPassword");

    when(repository.findByUsername("DummyUser")).thenReturn(Optional.of(mockUser));

    ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> loginFailedAttemptsCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<LocalDateTime> accountLockedUntilCaptor = ArgumentCaptor.forClass(
        LocalDateTime.class);

    doNothing().when(repository)
        .updateLoginFailed(userIdCaptor.capture(), loginFailedAttemptsCaptor.capture(),
            accountLockedUntilCaptor.capture());

    sut.handleLoginFailure(loginRequest);

    verify(repository, times(1)).findByUsername(any(String.class));
    verify(repository, times(1)).updateLoginFailed(anyInt(), anyInt(), eq(null));
    assertThat(userIdCaptor.getValue()).isEqualTo(999);
    assertThat(loginFailedAttemptsCaptor.getValue()).isEqualTo(1);
    assertThat(accountLockedUntilCaptor.getValue()).isNull();

  }

  @Test
  void ログインリクエストをしたユーザーのログイン連続失敗回数が5以上となった時にアカウントロック期限が設定されること() {

    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("DummyUser");
    mockUser.setDisplayName("DummyUser");
    mockUser.setEmail("dummy@example.com");
    mockUser.setRole("USER");
    mockUser.setLoginFailedAttempts(4);
    mockUser.setAccountLockedUntil(null);
    LoginRequest loginRequest = new LoginRequest("DummyUser", "DummyPassword");

    when(repository.findByUsername("DummyUser")).thenReturn(Optional.of(mockUser));

    ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> loginFailedAttemptsCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<LocalDateTime> accountLockedUntilCaptor = ArgumentCaptor.forClass(
        LocalDateTime.class);

    doNothing().when(repository)
        .updateLoginFailed(userIdCaptor.capture(), loginFailedAttemptsCaptor.capture(),
            accountLockedUntilCaptor.capture());

    sut.handleLoginFailure(loginRequest);

    verify(repository, times(1)).findByUsername(any(String.class));
    verify(repository, times(1)).updateLoginFailed(anyInt(), anyInt(), any(LocalDateTime.class));
    assertThat(userIdCaptor.getValue()).isEqualTo(999);
    assertThat(loginFailedAttemptsCaptor.getValue()).isEqualTo(5);
    assertThat(accountLockedUntilCaptor.getValue()).isCloseTo(LocalDateTime.now().plusMinutes(30),
        within(10, ChronoUnit.SECONDS));

  }

  @Test
  void アカウントロック期限が設定されているユーザーがログイン失敗した時アカウントロック期限が再設定されないこと() {

    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("DummyUser");
    mockUser.setDisplayName("DummyUser");
    mockUser.setEmail("dummy@example.com");
    mockUser.setRole("USER");
    mockUser.setLoginFailedAttempts(6);
    mockUser.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
    LoginRequest loginRequest = new LoginRequest("DummyUser", "DummyPassword");

    when(repository.findByUsername("DummyUser")).thenReturn(Optional.of(mockUser));

    ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> loginFailedAttemptsCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<LocalDateTime> accountLockedUntilCaptor = ArgumentCaptor.forClass(
        LocalDateTime.class);

    doNothing().when(repository)
        .updateLoginFailed(userIdCaptor.capture(), loginFailedAttemptsCaptor.capture(),
            accountLockedUntilCaptor.capture());

    sut.handleLoginFailure(loginRequest);

    verify(repository, times(1)).findByUsername(any(String.class));
    verify(repository, times(1)).updateLoginFailed(anyInt(), anyInt(), any(LocalDateTime.class));
    assertThat(userIdCaptor.getValue()).isEqualTo(999);
    assertThat(loginFailedAttemptsCaptor.getValue()).isEqualTo(7);
    assertThat(accountLockedUntilCaptor.getValue()).isCloseTo(LocalDateTime.now().plusMinutes(10),
        within(10, ChronoUnit.SECONDS));

  }

}
