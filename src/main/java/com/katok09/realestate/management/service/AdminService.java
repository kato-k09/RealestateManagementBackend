package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

  private final UserRepository userRepository;

  public AdminService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public void updateStatus(int userId, int selfUserId, StatusRequest statusRequest) {
    if (userId == selfUserId) {
      throw new IllegalArgumentException("自信のステータスは変更できません。");
    }
    userRepository.updateStatus(userId, statusRequest);
  }
}
