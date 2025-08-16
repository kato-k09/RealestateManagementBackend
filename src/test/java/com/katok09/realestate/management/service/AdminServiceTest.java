package com.katok09.realestate.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

  @Mock
  private UserRepository userRepository;

  private AdminService sut;

  @BeforeEach
  void before() {
    sut = new AdminService(userRepository);
  }

  @Test
  void 全ユーザーのユーザー情報が取得できること() {

    when(userRepository.findAll()).thenReturn(new ArrayList<>());

    List<User> actual = sut.getAllUsers();

    verify(userRepository, times(1)).findAll();
    assertThat(actual).isNotNull();
  }

  @Test
  void 自身以外の指定したユーザーのステータスを変更できること() {

    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("USER");

    doNothing().when(userRepository).updateStatus(999, statusRequest);

    sut.updateStatus(999, 1, statusRequest);

    verify(userRepository, times(1)).updateStatus(anyInt(), any(StatusRequest.class));
  }

}
