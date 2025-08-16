package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

  Optional<User> findByUsername(@Param("username") String username);

  Optional<User> findById(@Param("id") int id);

  boolean existsByUsername(@Param("username") String username);

  boolean existsByEmail(@Param("email") String email);

  boolean existsByUsernameNotSelfId(@Param("username") String username, @Param("id") int id);

  boolean existsByEmailNotSelfId(@Param("email") String email, @Param("id") int id);

  List<User> findAll();

  void registerUser(@Param("user") User user);

  void updateUser(@Param("id") int id, @Param("updateRequest") UpdateRequest updateRequest);

  void updatePassword(@Param("id") int id, @Param("newPassword") String newPassword);

  void deleteUserById(@Param("id") int id);

  void updateLoginFailed(@Param("id") int id, @Param("loginFailedAttempts") int loginFailedAttempts,
      @Param("accountLockedUntil") LocalDateTime accountLockedUntil);

  void updateLastLoginAt(@Param("id") int id, @Param("lastLoginAt") LocalDateTime lastLoginAt);

  void updateStatus(@Param("id") int id, @Param("statusRequest") StatusRequest statusRequest);
}
