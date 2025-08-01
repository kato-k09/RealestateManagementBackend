package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

  Optional<User> findByUsername(@Param("username") String username);

  Optional<User> findByEmail(@Param("email") String email);

  Optional<User> findById(@Param("id") Long id);

  boolean existsByUsername(@Param("username") String username);

  boolean existsByEmail(@Param("email") String email);

  void save(@Param("user") User user);

  void update(@Param("user") User user);

  void deleteById(@Param("id") Long id);

  List<User> findAll();

  List<User> findAllActive();

  List<User> findUsersCreatedAfter(@Param("since") LocalDateTime since);

  List<User> findUsersUpdatedAfter(@Param("since") LocalDateTime since);

  int countByRole(@Param("role") String role);

  void updateLastLoginAt(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);

  void updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);

  void updateEnabled(@Param("id") Long id, @Param("enabled") boolean enabled);
}
