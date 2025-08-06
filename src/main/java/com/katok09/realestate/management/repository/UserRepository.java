package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.UpdateRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

  Optional<User> findByUsername(@Param("username") String username);

  Optional<User> findByEmail(@Param("email") String email);

  Optional<User> findById(@Param("id") int id);

  boolean existsByUsername(@Param("username") String username);

  boolean existsByEmail(@Param("email") String email);

  boolean existsByUsernameNotId(@Param("username") String username, @Param("id") int id);

  boolean existsByEmailNotId(@Param("email") String email, @Param("id") int id);

  void save(@Param("user") User user);

  void update(@Param("user") User user);

  void deleteById(@Param("id") int id);

  List<User> findAll();

  List<User> findAllActive();

  List<User> findUsersCreatedAfter(@Param("since") LocalDateTime since);

  List<User> findUsersUpdatedAfter(@Param("since") LocalDateTime since);

  int countByRole(@Param("role") String role);

  void changeUserInfo(@Param("id") int id, @Param("updateRequest") UpdateRequest updateRequest);

  void updateLastLoginAt(@Param("id") int id, @Param("lastLoginAt") LocalDateTime lastLoginAt);

  void updatePassword(@Param("id") int id, @Param("newPassword") String newPassword);

  void updateEnabled(@Param("id") int id, @Param("enabled") boolean enabled);
}
