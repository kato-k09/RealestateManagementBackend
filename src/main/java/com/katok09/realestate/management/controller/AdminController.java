package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.service.AdminService;
import com.katok09.realestate.management.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理者API", description = "管理者専用の機能を提供するAPI")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class AdminController {

  private final AdminService adminService;
  private final JwtUtil jwtUtil;

  @Autowired
  public AdminController(AdminService adminService, JwtUtil jwtUtil) {
    this.adminService = adminService;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping("/users")
  @Operation(summary = "全ユーザーの一覧取得", description = "システム内の全ユーザー情報を取得")
  public ResponseEntity<List<User>> getAllUsers() {
    try {
      List<User> users = adminService.getAllUsers();
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping("/users/{userId}/statusChange")
  @Operation(summary = "指定ユーザーのステータス変更", description = "指定ユーザーのrole、enabled、login_failed_attempts、account_locked_untilを変更")
  public ResponseEntity<Map<String, Object>> statusChange(@PathVariable int userId,
      @Valid @RequestBody StatusRequest request) {

    try {
      adminService.statusChange(userId, request);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("role", request.getRole());
      response.put("enabled", request.isEnabled());
      response.put("login_failed_attempts", request.getLoginFailedAttempts());
      response.put("account_locked_until", request.getAccountLockedUntil());
      response.put("message",
          "ステータスを変更しました。\nRole: " + request.getRole() + "\nEnabled: "
              + request.isEnabled() + "\nLoginFailedAttempts: " + request.getLoginFailedAttempts()
              + "\nAccountLockedUntil: " + request.getAccountLockedUntil());
      return ResponseEntity.ok(response);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(jwtUtil.createErrorResponse("VALIDATION_ERROR", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(jwtUtil.createErrorResponse("INTERNAL_ERROR", "ユーザー状態の変更に失敗しました。"));
    }

  }

}
