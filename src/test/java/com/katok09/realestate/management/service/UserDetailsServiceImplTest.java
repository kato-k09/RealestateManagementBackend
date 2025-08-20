package com.katok09.realestate.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.repository.UserRepository;
import com.katok09.realestate.management.service.UserDetailsServiceImpl.CustomUserPrincipal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

  @Mock
  private UserRepository userRepository;

  private UserDetailsServiceImpl sut;

  @BeforeEach
  void before() {
    sut = new UserDetailsServiceImpl(userRepository);
  }

  @Test
  void ユーザー名からユーザー詳細情報が取得できること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(false);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));

    UserDetails actual = sut.loadUserByUsername("DummyUser");

    assertThat(actual).isInstanceOf(UserDetails.class);
  }

  @Test
  void 登録されてないユーザー詳細情報を取得する時エラーメッセージが返ること() {

    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.empty());

    UsernameNotFoundException actual = assertThrows(UsernameNotFoundException.class, () -> {
      sut.loadUserByUsername("DummyUser");
    });

    assertThat(actual.getMessage()).isEqualTo("ユーザーが見つかりません: DummyUser");
  }

  @Test
  void 削除されたユーザー詳細情報を取得する時エラーメッセージが返ること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setDeleted(true);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));

    UsernameNotFoundException actual = assertThrows(UsernameNotFoundException.class, () -> {
      sut.loadUserByUsername("DummyUser");
    });

    assertThat(actual.getMessage()).isEqualTo("ユーザーが削除されています: DummyUser");
  }

  @Test
  void 無効なユーザー詳細情報を取得する時エラーメッセージが返ること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(false);
    dummyUser.setDeleted(false);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));

    UsernameNotFoundException actual = assertThrows(UsernameNotFoundException.class, () -> {
      sut.loadUserByUsername("DummyUser");
    });

    assertThat(actual.getMessage()).isEqualTo("ユーザーが無効です: DummyUser");
  }

  @Test
  void ユーザープリンシパルで設定されたロールを取得できること() {

    CustomUserPrincipal actual = createCustomUserPrincipal();

    assertThat(actual.getAuthorities()).hasSize(1);
    assertThat(actual.getAuthorities().iterator().next().getAuthority())
        .isEqualTo("ROLE_USER");
  }

  @Test
  void ユーザープリンシパルで設定されたユーザー名を取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getUsername()).isEqualTo("DummyUser");
  }

  @Test
  void ユーザープリンシパルで設定されたパスワードを取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getPassword()).isEqualTo("DummyPassword");
  }

  @Test
  void ユーザープリンシパルでアカウントの有効期限切れを確認するとき常にtrueが返ること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.isAccountNonExpired()).isTrue();
  }

  @Test
  void ユーザープリンシパルでアカウントロックが設定されていない時trueが返ること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("DummyPassword");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setAccountLockedUntil(null);
    dummyUser.setDeleted(false);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));
    UserDetails actual = sut.loadUserByUsername("DummyUser");

    assertThat(actual.isAccountNonLocked()).isTrue();
  }

  @Test
  void ユーザープリンシパルで期限切れのアカウントロックが設定されている時trueが返ること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("DummyPassword");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setAccountLockedUntil(LocalDateTime.now().minusMinutes(30));
    dummyUser.setDeleted(false);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));
    UserDetails actual = sut.loadUserByUsername("DummyUser");

    assertThat(actual.isAccountNonLocked()).isTrue();
  }

  @Test
  void ユーザープリンシパルで有効な期限のアカウントロックが設定されている時falseが返ること() {

    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("DummyPassword");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
    dummyUser.setDeleted(false);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));
    UserDetails actual = sut.loadUserByUsername("DummyUser");

    assertThat(actual.isAccountNonLocked()).isFalse();
  }

  @Test
  void ユーザープリンシパルでパスワードの有効期限を確認する時常にtrueが返ること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.isCredentialsNonExpired()).isTrue();
  }

  @Test
  void ユーザープリンシパルでユーザーが有効な時trueが返ること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.isEnabled()).isTrue();
  }

  @Test
  void ユーザープリンシパルで元のUserオブジェクトを取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getUser()).isNotNull();
    assertThat(actual.getUser().getId()).isEqualTo(999);
    assertThat(actual.getUser().getUsername()).isEqualTo("DummyUser");
  }

  @Test
  void ユーザープリンシパルでユーザーIDを取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getUserId()).isEqualTo(999);
  }

  @Test
  void ユーザープリンシパルで表示名を取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getDisplayName()).isEqualTo("DummyUser");
  }

  @Test
  void ユーザープリンシパルでEmailを取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getEmail()).isEqualTo("dummy@example.com");
  }

  @Test
  void ユーザープリンシパルでロールを取得できること() {
    CustomUserPrincipal actual = createCustomUserPrincipal();
    assertThat(actual.getRole()).isEqualTo("USER");
  }

  private CustomUserPrincipal createCustomUserPrincipal() {
    User dummyUser = new User();
    dummyUser.setId(999);
    dummyUser.setUsername("DummyUser");
    dummyUser.setPassword("DummyPassword");
    dummyUser.setDisplayName("DummyUser");
    dummyUser.setEmail("dummy@example.com");
    dummyUser.setRole("USER");
    dummyUser.setEnabled(true);
    dummyUser.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
    dummyUser.setDeleted(false);
    when(userRepository.findByUsername("DummyUser")).thenReturn(Optional.of(dummyUser));

    UserDetails userDetails = sut.loadUserByUsername("DummyUser");
    CustomUserPrincipal actual = (CustomUserPrincipal) userDetails;
    return actual;
  }
}
