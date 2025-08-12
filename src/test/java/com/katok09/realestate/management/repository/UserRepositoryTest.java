package com.katok09.realestate.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository sut;

  @Test
  void ユーザー名からユーザー情報を取得できること() {

    User actual = sut.findByUsername("user1").orElse(null);

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(2);
    assertThat(actual.getUsername()).isEqualTo("user1");
    assertThat(actual.getPassword()).isEqualTo(
        "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b6rCiWAXEBkEfC");
    assertThat(actual.getEmail()).isEqualTo("user1@example.com");
    assertThat(actual.getDisplayName()).isEqualTo("山田太郎");
    assertThat(actual.getRole()).isEqualTo("USER");
    assertThat(actual.isEnabled()).isTrue();
    assertThat(actual.isDeleted()).isFalse();
  }

  @Test
  void ユーザー名からユーザー情報取得時に登録が有効でない時nullが返ってくること() {

    User deletedUser = sut.findByUsername("user3").orElse(null);
    User unknownUser = sut.findByUsername("UnknownUser").orElse(null);

    assertThat(deletedUser).isNull();
    assertThat(unknownUser).isNull();
  }

  @Test
  void ユーザーIDからユーザー情報を取得できること() {

    User actual = sut.findById(2).orElse(null);

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(2);
    assertThat(actual.getUsername()).isEqualTo("user1");
    assertThat(actual.getPassword()).isEqualTo(
        "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b6rCiWAXEBkEfC");
    assertThat(actual.getEmail()).isEqualTo("user1@example.com");
    assertThat(actual.getDisplayName()).isEqualTo("山田太郎");
    assertThat(actual.getRole()).isEqualTo("USER");
    assertThat(actual.isEnabled()).isTrue();
    assertThat(actual.isDeleted()).isFalse();
  }

  @Test
  void ユーザーIDからユーザー情報取得時に登録が有効でない時nullが返ってくること() {

    User deletedUser = sut.findById(4).orElse(null);
    User unknownUser = sut.findById(999).orElse(null);

    assertThat(deletedUser).isNull();
    assertThat(unknownUser).isNull();
  }

  @Test
  void 登録が有効なユーザーをユーザー名から確認できること() {

    boolean actual = sut.existsByUsername("user1");

    assertThat(actual).isTrue();
  }

  @Test
  void 登録が無効なユーザーをユーザー名から確認できること() {

    boolean deletedUser = sut.existsByUsername("user3");
    boolean unknownUser = sut.existsByUsername("user999");

    assertThat(deletedUser).isFalse();
    assertThat(unknownUser).isFalse();
  }

  @Test
  void 登録が有効なユーザーをEmailから確認できること() {

    boolean actual = sut.existsByEmail("user1@example.com");

    assertThat(actual).isTrue();
  }

  @Test
  void 登録が無効なユーザーをEmailから確認できること() {

    boolean deletedUser = sut.existsByEmail("user3@example.com");
    boolean unknownUser = sut.existsByEmail("user999@example.com");

    assertThat(deletedUser).isFalse();
    assertThat(unknownUser).isFalse();
  }

  @Test
  void 指定したIDのユーザー以外でユーザー名が登録されていないことを確認できること() {

    boolean actual = sut.existsByUsernameNotSelfId("user1", 2);

    assertThat(actual).isFalse();
  }

  @Test
  void 指定したIDのユーザー以外で既にユーザー名が登録されていることを確認できること() {

    boolean actual = sut.existsByUsernameNotSelfId("user1", 999);

    assertThat(actual).isTrue();
  }

  @Test
  void 指定したIDのユーザー以外でEmailが登録されていないことを確認できること() {

    boolean actual = sut.existsByEmailNotSelfId("user1@example.com", 2);

    assertThat(actual).isFalse();
  }

  @Test
  void 指定したIDのユーザー以外でEmailが登録されていることを確認できること() {

    boolean actual = sut.existsByEmailNotSelfId("user1@example.com", 999);

    assertThat(actual).isTrue();
  }

  @Test
  void 登録が有効なユーザー情報を全て取得できること() {

    List<User> actual = sut.findAll();

    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 渡されたユーザー情報から新規ユーザー登録ができID自動採番や登録日時等を自動登録できた上でDBに反映されること() {

    User newUser = new User();
    newUser.setId(999);
    newUser.setUsername("NewUser");
    newUser.setPassword("Password");
    newUser.setEmail("NewUser@example.com");
    newUser.setDisplayName("NewUser");
    newUser.setRole("USER");
    newUser.setEnabled(true);
    newUser.setDeleted(false);

    sut.registerUser(newUser);

    List<User> actual = sut.findAll();
    assertThat(actual.size()).isEqualTo(4); // 削除済み以外のユーザーは3件DBに登録されていた

    User registeredUser = sut.findByUsername("NewUser").orElse(null);
    assertThat(registeredUser).isNotNull();
    assertThat(registeredUser.getId()).isEqualTo(5);
    assertThat(registeredUser.getUsername()).isEqualTo("NewUser");
    assertThat(registeredUser.getPassword()).isEqualTo("Password");
    assertThat(registeredUser.getEmail()).isEqualTo("NewUser@example.com");
    assertThat(registeredUser.getDisplayName()).isEqualTo("NewUser");
    assertThat(registeredUser.getCreatedAt()).isCloseTo(LocalDateTime.now(),
        within(10, ChronoUnit.SECONDS));
    assertThat(registeredUser.getUpdatedAt()).isCloseTo(LocalDateTime.now(),
        within(10, ChronoUnit.SECONDS));
    assertThat(registeredUser.getLastLoginAt()).isNull();
    assertThat(registeredUser.getPasswordChangedAt()).isNull();
    assertThat(registeredUser.getLoginFailedAttempts()).isEqualTo(0);
    assertThat(registeredUser.getAccountLockedUntil()).isNull();
    assertThat(registeredUser.isEnabled()).isTrue();
    assertThat(registeredUser.isDeleted()).isFalse();

  }

  @Test
  void IDに紐づいたユーザー情報を更新できDBに反映されること() {

    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setUsername("ChangedUser");
    updateRequest.setEmail("changed@example.com");
    updateRequest.setDisplayName("ChangedUser");

    sut.updateUser(2, updateRequest);

    User actual = sut.findById(2).orElse(null);
    assertThat(actual).isNotNull();
    assertThat(actual.getUsername()).isEqualTo("ChangedUser");
    assertThat(actual.getEmail()).isEqualTo("changed@example.com");
    assertThat(actual.getDisplayName()).isEqualTo("ChangedUser");

  }

  @Test
  void IDに紐づいたユーザーのパスワードを更新できDBに反映されること() {

    sut.updatePassword(2, "ChangedPassword");

    User actual = sut.findById(2).orElse(null);
    assertThat(actual).isNotNull();
    assertThat(actual.getPassword()).isEqualTo("ChangedPassword");
  }

  @Test
  void IDに紐づいたユーザーを削除できDBに反映されること() {

    sut.deleteUserById(2);

    User actual = sut.findById(2).orElse(null);
    assertThat(actual).isNull();

    List<User> allUsers = sut.findAll();
    assertThat(allUsers.size()).isEqualTo(2);
  }

  @Test
  void IDに紐づいたユーザーのログイン連続失敗回数とアカウントロック情報が更新できDBに反映されること() {

    LocalDateTime accountLockedUntil = LocalDateTime.now().plusMinutes(30).withNano(0);

    sut.updateLoginFailed(2, 5, accountLockedUntil);

    User actual = sut.findById(2).orElse(null);

    assertThat(actual).isNotNull();
    assertThat(actual.getLoginFailedAttempts()).isEqualTo(5);
    assertThat(actual.getAccountLockedUntil()).isEqualTo(accountLockedUntil);

  }

  @Test
  void IDに紐づいたユーザーの最終ログイン日時が更新できDBに反映されること() {

    LocalDateTime lastLoginAt = LocalDateTime.now().withNano(0);

    sut.updateLastLoginAt(2, lastLoginAt);

    User actual = sut.findById(2).orElse(null);
    assertThat(actual).isNotNull();
    assertThat(actual.getLastLoginAt()).isEqualTo(lastLoginAt);
  }

  @Test
  void IDに紐づいたユーザーのステータス情報が更新できDBに反映されること() {

    LocalDateTime accountLockedUntil = LocalDateTime.now().plusMinutes(30).withNano(0);

    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("ADMIN");
    statusRequest.setEnabled(false);
    statusRequest.setLoginFailedAttempts(999);
    statusRequest.setAccountLockedUntil(accountLockedUntil);

    sut.statusChange(2, statusRequest);

    User actual = sut.findById(2).orElse(null);
    assertThat(actual).isNotNull();
    assertThat(actual.getRole()).isEqualTo("ADMIN");
    assertThat(actual.isEnabled()).isFalse();
    assertThat(actual.getLoginFailedAttempts()).isEqualTo(999);
    assertThat(actual.getAccountLockedUntil()).isEqualTo(accountLockedUntil);

  }

}
