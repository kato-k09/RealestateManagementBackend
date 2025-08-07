package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.repository.RealestateRepository;
import com.katok09.realestate.management.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

  private final UserRepository userRepository;
  private final RealestateRepository realestateRepository;

  public AdminService(UserRepository userRepository, RealestateRepository realestateRepository) {
    this.userRepository = userRepository;
    this.realestateRepository = realestateRepository;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public void statusChange(int userId, StatusRequest statusRequest) {

    userRepository.statusChange(userId, statusRequest);

  }

}
