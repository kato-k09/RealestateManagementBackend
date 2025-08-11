package com.katok09.realestate.management.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.RegisterRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import com.katok09.realestate.management.dto.UserInfo;
import com.katok09.realestate.management.repository.UserRepository;
import com.katok09.realestate.management.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private AccountLockService accountLockService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RealestateService realestateService;

  private AuthService sut;

  @BeforeEach
  void before() {
    sut = new AuthService(authenticationManager, passwordEncoder,
        jwtUtil, accountLockService, userRepository,
        realestateService);
  }

  @Test
  void 認証成功時にトークンとユーザー情報が返ってくること() {

    LoginRequest loginRequest = new LoginRequest("DummyUser", "DummyPassword");
    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("DummyUser");
    mockUser.setDisplayName("DummyUser");
    mockUser.setEmail("dummy@example.com");
    mockUser.setRole("USER");
    UserDetailsServiceImpl.CustomUserPrincipal mockPrincipal = new UserDetailsServiceImpl.CustomUserPrincipal(
        mockUser);
    Authentication mockAuthentication = mock(Authentication.class);

    doNothing().when(accountLockService).unlockIfAccountLockExpired(any(LoginRequest.class));
    when(authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
    when(mockAuthentication.getPrincipal()).thenReturn(mockPrincipal);
    doNothing().when(accountLockService).resetAccountLockState(any(User.class));
    when(jwtUtil.generateToken("DummyUser", "USER", 999)).thenReturn("DummyToken");
    doNothing().when(userRepository).updateLastLoginAt(anyInt(), any(LocalDateTime.class));

    LoginResponse actual = sut.authenticate(loginRequest);

    verify(accountLockService, times(1)).unlockIfAccountLockExpired(any(LoginRequest.class));
    verify(authenticationManager, times(1)).authenticate(
        any(UsernamePasswordAuthenticationToken.class));
    verify(accountLockService, times(1)).resetAccountLockState(any(User.class));
    verify(jwtUtil, times(1)).generateToken("DummyUser", "USER", 999);
    verify(userRepository, times(1)).updateLastLoginAt(anyInt(), any(LocalDateTime.class));

    assertThat(actual.getToken()).isEqualTo("DummyToken");
    assertThat(actual.getUserInfo().getId()).isEqualTo(999);
    assertThat(actual.getUserInfo().getUsername()).isEqualTo("DummyUser");
    assertThat(actual.getUserInfo().getEmail()).isEqualTo("dummy@example.com");
    assertThat(actual.getUserInfo().getRole()).isEqualTo("USER");
  }

  @Test
  void アカウントロックがかかっているユーザーが認証した時にロック解除までの秒数情報が返ってくること() {

    LoginRequest loginRequest = new LoginRequest("LockedUser", "LockedPassword");
    User mockUser = new User();
    mockUser.setId(999);
    mockUser.setUsername("LockedUser");
    mockUser.setDisplayName("LockedUser");
    mockUser.setEmail("locked@example.com");
    mockUser.setRole("USER");
    mockUser.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));

    doNothing().when(accountLockService).unlockIfAccountLockExpired(any(LoginRequest.class));
    when(authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).thenThrow(
        new LockedException("DummyLockedMessage"));
    doNothing().when(accountLockService).handleLoginFailure("LockedUser");
    when(userRepository.findByUsername("LockedUser")).thenReturn(Optional.of(mockUser));

    LockedException actual = assertThrows(LockedException.class, () -> {
      sut.authenticate(loginRequest);
    });

    verify(accountLockService, times(1)).unlockIfAccountLockExpired(any(LoginRequest.class));
    verify(authenticationManager, times(1)).authenticate(
        any(UsernamePasswordAuthenticationToken.class));
    verify(accountLockService, times(1)).handleLoginFailure("LockedUser");
    verify(userRepository, times(1)).findByUsername("LockedUser");

    // 正常時の処理が呼ばれないことの確認
    verify(accountLockService, never()).resetAccountLockState(any(User.class));
    verify(jwtUtil, never()).generateToken("LockedUser", "USER", 999);
    verify(userRepository, never()).updateLastLoginAt(anyInt(), any(LocalDateTime.class));

    String actualMessage = actual.getMessage();
    assertTrue(actualMessage.contains("アカウントがロックされています。"));
    Pattern pattern = Pattern.compile("あと(\\d+)秒後に");
    Matcher matcher = pattern.matcher(actualMessage);
    assertTrue(matcher.find(), "残り時間の形式が正しくありません。");
    String numberString = matcher.group(1);
    int remainingSeconds = Integer.parseInt(numberString);
    assertThat(remainingSeconds)
        .as("残り時間の確認")
        .isBetween(1700, 1800);
    assertTrue(actualMessage.contains("ロックが解除されます。"));
  }

  @Test
  void 問題のあるアカウント認証情報で認証した時にエラーメッセージが返ってくること() {

    LoginRequest loginRequest = new LoginRequest("BadCredentialsUser", "BadCredentialsPassword");

    doNothing().when(accountLockService).unlockIfAccountLockExpired(any(LoginRequest.class));
    when(authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).thenThrow(
        new BadCredentialsException("DummyBadCredentialsMessage"));
    doNothing().when(accountLockService).handleLoginFailure("BadCredentialsUser");

    BadCredentialsException actual = assertThrows(BadCredentialsException.class, () -> {
      sut.authenticate(loginRequest);
    });

    verify(accountLockService, times(1)).unlockIfAccountLockExpired(any(LoginRequest.class));
    verify(authenticationManager, times(1)).authenticate(
        any(UsernamePasswordAuthenticationToken.class));
    verify(accountLockService, times(1)).handleLoginFailure("BadCredentialsUser");

    // 正常時の処理が呼ばれないことの確認
    verify(accountLockService, never()).resetAccountLockState(any(User.class));
    verify(jwtUtil, never()).generateToken("BadCredentialsUser", "USER", 999);
    verify(userRepository, never()).updateLastLoginAt(anyInt(), any(LocalDateTime.class));

    assertTrue(actual.getMessage().contains("ユーザー名またはパスワードが間違っています。"));

  }

  @Test
  void 正常なユーザー情報を入力した時にユーザーの新規登録が実行されること() {

    RegisterRequest registerRequest = new RegisterRequest(
        "DummyUsername", "DummyPassword", "dummy@example.com", "DummyUser");

    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("DummyHashedPassword");
    doNothing().when(userRepository).save(any(User.class));

    sut.registerUser(registerRequest);

    verify(userRepository, times(1)).existsByUsername("DummyUsername");
    verify(userRepository, times(1)).existsByEmail("dummy@example.com");
    verify(passwordEncoder, times(1)).encode("DummyPassword");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertThat(savedUser.getUsername()).isEqualTo("DummyUsername");
    assertThat(savedUser.getEmail()).isEqualTo("dummy@example.com");
    assertThat(savedUser.getDisplayName()).isEqualTo("DummyUser");
    assertThat(savedUser.getRole()).isEqualTo("USER");
    assertThat(savedUser.isEnabled()).isTrue();
    assertThat(savedUser.isDeleted()).isFalse();
    assertThat(savedUser.getPassword()).isEqualTo("DummyHashedPassword");

  }

  @Test
  void 重複したユーザー名で新規登録しようとした時にエラーメッセージが返されること() {

    RegisterRequest registerRequest = new RegisterRequest(
        "DummyUsername", "DummyPassword", "dummy@example.com", "DummyUser");

    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.registerUser(registerRequest);
    });

    verify(userRepository, times(1)).existsByUsername("DummyUsername");
    verify(userRepository, never()).existsByEmail("dummy@example.com");
    verify(passwordEncoder, never()).encode("DummyPassword");
    verify(userRepository, never()).save(any(User.class));

    assertThat(actual.getMessage()).isEqualTo("このユーザー名は既に使用されています");

  }

  @Test
  void 重複したメールアドレスで新規登録しようとした時にエラーメッセージが返されること() {

    RegisterRequest registerRequest = new RegisterRequest(
        "DummyUsername", "DummyPassword", "dummy@example.com", "DummyUser");

    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.registerUser(registerRequest);
    });

    verify(userRepository, times(1)).existsByUsername("DummyUsername");
    verify(userRepository, times(1)).existsByEmail("dummy@example.com");
    verify(passwordEncoder, never()).encode("DummyPassword");
    verify(userRepository, never()).save(any(User.class));

    assertThat(actual.getMessage()).isEqualTo("このメールアドレスは既に使用されています");

  }

  @Test
  void トークン検証時に有効なトークンが渡された場合そのトークンに紐づけられたユーザー情報が返ってくること() {

    String dummyToken = "DummyToken";
    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    when(jwtUtil.isTokenValid(dummyToken)).thenReturn(true);
    when(jwtUtil.getUsernameFromToken(dummyToken)).thenReturn("DummyUser");
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));

    UserInfo actual = sut.validateToken(dummyToken);

    verify(jwtUtil, times(1)).isTokenValid(dummyToken);
    verify(jwtUtil, times(1)).getUsernameFromToken(dummyToken);
    verify(userRepository, times(1)).findByUsername("DummyUser");

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(999);
    assertThat(actual.getUsername()).isEqualTo("DummyUser");
    assertThat(actual.getDisplayName()).isEqualTo("DummyUser");
    assertThat(actual.getEmail()).isEqualTo("dummy@example.com");
    assertThat(actual.getRole()).isEqualTo("USER");

  }

  @Test
  void トークン検証時に無効なトークンが渡された場合nullが返ってくること() {

    String dummyToken = "DummyToken";

    when(jwtUtil.isTokenValid(dummyToken)).thenReturn(false);

    UserInfo actual = sut.validateToken(dummyToken);

    verify(jwtUtil, times(1)).isTokenValid(dummyToken);
    verify(jwtUtil, never()).getUsernameFromToken(dummyToken);
    verify(userRepository, never()).findByUsername("DummyUser");

    assertThat(actual).isNull();

  }

  @Test
  void トークン検証時に有効なユーザーが見つからなかった場合nullが返ってくること() {

    String dummyToken = "DummyToken";

    when(jwtUtil.isTokenValid(dummyToken)).thenReturn(true);
    when(jwtUtil.getUsernameFromToken(dummyToken)).thenReturn("DummyUser");
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.empty());

    UserInfo actual = sut.validateToken(dummyToken);

    verify(jwtUtil, times(1)).isTokenValid(dummyToken);
    verify(jwtUtil, times(1)).getUsernameFromToken(dummyToken);
    verify(userRepository, times(1)).findByUsername("DummyUser");

    assertThat(actual).isNull();

  }

  @Test
  void 正常なユーザー更新情報が渡された場合ユーザー情報が更新されること() {

    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUser");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("CurrentPassword");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    when(userRepository.findById(999)).thenReturn(Optional.of(dummyUser));
    when(userRepository.existsByUsernameNotId("ChangedUser", 999)).thenReturn(false);
    when(userRepository.existsByEmailNotId("changed@example.com", 999)).thenReturn(false);
    when(passwordEncoder.matches("CurrentPassword", "CurrentPassword")).thenReturn(true);
    when(passwordEncoder.encode("NewPassword")).thenReturn("HashedNewPassword");
    doNothing().when(userRepository).updatePassword(999, "HashedNewPassword");
    doNothing().when(userRepository).changeUserInfo(eq(999), any(UpdateRequest.class));

    sut.changeUserInfo(999, updateRequest);

    verify(userRepository, times(1)).findById(999);
    verify(userRepository, times(1)).existsByUsernameNotId("ChangedUser", 999);
    verify(userRepository, times(1)).existsByEmailNotId("changed@example.com", 999);
    verify(passwordEncoder, times(1)).matches("CurrentPassword", "CurrentPassword");
    verify(passwordEncoder, times(1)).encode("NewPassword");

    // パスワード更新処理の検証（userRepository.updatePasswordメソッドの引数の検証）
    ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<String> hashedNewPasswordCaptor = ArgumentCaptor.forClass(String.class);
    verify(userRepository, times(1)).updatePassword(userIdCaptor.capture(),
        hashedNewPasswordCaptor.capture());

    assertThat(userIdCaptor.getValue()).isEqualTo(999);
    assertThat(hashedNewPasswordCaptor.getValue()).isEqualTo("HashedNewPassword");

    // ユーザー情報更新処理の検証（userRepository.updateRequestメソッドの引数の検証）
    ArgumentCaptor<UpdateRequest> updateRequestCaptor = ArgumentCaptor.forClass(
        UpdateRequest.class);
    verify(userRepository, times(1)).changeUserInfo(userIdCaptor.capture(),
        updateRequestCaptor.capture());
    UpdateRequest changedUser = updateRequestCaptor.getValue();

    assertThat(userIdCaptor.getValue()).isEqualTo(999);
    assertThat(changedUser.getUsername()).isEqualTo("ChangedUser");
    assertThat(changedUser.getEmail()).isEqualTo("changed@example.com");
    assertThat(changedUser.getDisplayName()).isEqualTo("ChangedUser");
    assertThat(changedUser.getNewPassword()).isEqualTo("HashedNewPassword");

  }

  @Test
  void ユーザー情報更新時に有効なユーザーが見つからなかった時にエラーメッセージが返ってくること() {

    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUser");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    when(userRepository.findById(999)).thenReturn(Optional.empty());

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.changeUserInfo(999, updateRequest);
    });

    verify(userRepository, times(1)).findById(999);
    verify(userRepository, never()).existsByUsernameNotId("ChangedUser", 999);
    verify(userRepository, never()).existsByEmailNotId("changed@example.com", 999);
    verify(passwordEncoder, never()).matches("CurrentPassword", "CurrentPassword");
    verify(passwordEncoder, never()).encode("NewPassword");

    assertThat(actual.getMessage()).isEqualTo("ユーザーが見つかりません");

  }

  @Test
  void GUESTロールのユーザー情報更新時にエラーメッセージが返ってくること() {

    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUser");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");
    updateRequest.setCurrentPassword("CurrentPassword");
    updateRequest.setNewPassword("NewPassword");

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("CurrentPassword");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    dummyUser.setRole("GUEST");

    when(userRepository.findById(999)).thenReturn(Optional.of(dummyUser));

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.changeUserInfo(999, updateRequest);
    });

    verify(userRepository, times(1)).findById(999);
    verify(userRepository, never()).existsByUsernameNotId("ChangedUser", 999);
    verify(userRepository, never()).existsByEmailNotId("changed@example.com", 999);
    verify(passwordEncoder, never()).matches("CurrentPassword", "CurrentPassword");
    verify(passwordEncoder, never()).encode("NewPassword");
    assertThat(actual.getMessage()).isEqualTo("ゲストユーザー情報は変更できません");

  }

  @Test
  void ユーザー情報取得時に有効なユーザーIDを渡した時にユーザー情報が返ってくること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("CurrentPassword");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    when(userRepository.findById(999)).thenReturn(Optional.of(dummyUser));

    UserInfo actual = sut.getUserInfo(999);

    verify(userRepository, times(1)).findById(999);
    assertThat(actual.getId()).isEqualTo(999);
    assertThat(actual.getUsername()).isEqualTo("DummyUser");
    assertThat(actual.getDisplayName()).isEqualTo("DummyUser");
    assertThat(actual.getEmail()).isEqualTo("dummy@example.com");
    assertThat(actual.getRole()).isEqualTo("USER");

  }

  @Test
  void ユーザー情報取得時に無効なユーザーIDを渡した時にエラーメッセージが返ってくること() {

    when(userRepository.findById(999)).thenReturn(Optional.empty());

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.getUserInfo(999);
    });

    verify(userRepository, times(1)).findById(999);
    assertThat(actual.getMessage()).isEqualTo("ユーザーが見つかりません");

  }

  @Test
  void 有効なユーザーIDを渡した時にそのユーザーIDに紐づけられた全不動産情報とユーザー情報が削除されること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("CurrentPassword");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    when(userRepository.findById(999)).thenReturn(Optional.of(dummyUser));
    doNothing().when(realestateService).deleteRealestateByUserId(999);
    doNothing().when(userRepository).deleteById(999);

    sut.deleteUser(999);

    verify(userRepository, times(1)).findById(999);
    verify(realestateService, times(1)).deleteRealestateByUserId(999);
    verify(userRepository, times(1)).deleteById(999);

  }

  @Test
  void ユーザー情報削除時に無効なユーザーIDを渡した時にエラーメッセージが返ってくること() {

    when(userRepository.findById(999)).thenReturn(Optional.empty());

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.deleteUser(999);
    });

    verify(userRepository, times(1)).findById(999);
    verify(realestateService, never()).deleteRealestateByUserId(999);
    verify(userRepository, never()).deleteById(999);
    assertThat(actual.getMessage()).isEqualTo("ユーザーが見つかりません。");

  }

  @Test
  void GUESTロールユーザー削除時にエラーメッセージが返ってくること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("CurrentPassword");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    dummyUser.setRole("GUEST");

    when(userRepository.findById(999)).thenReturn(Optional.of(dummyUser));

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.deleteUser(999);
    });

    verify(userRepository, times(1)).findById(999);
    verify(realestateService, never()).deleteRealestateByUserId(999);
    verify(userRepository, never()).deleteById(999);
    assertThat(actual.getMessage()).isEqualTo("ゲストユーザーは削除できません。");

  }

  @Test
  void ADMINロールユーザー削除時にエラーメッセージが返ってくること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("CurrentPassword");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);

    dummyUser.setRole("ADMIN");

    when(userRepository.findById(999)).thenReturn(Optional.of(dummyUser));

    IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> {
      sut.deleteUser(999);
    });

    verify(userRepository, times(1)).findById(999);
    verify(realestateService, never()).deleteRealestateByUserId(999);
    verify(userRepository, never()).deleteById(999);
    assertThat(actual.getMessage()).isEqualTo("管理者ユーザーは削除できません。");

  }

}
