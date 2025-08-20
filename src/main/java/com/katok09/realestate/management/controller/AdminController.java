package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.service.AdminService;
import com.katok09.realestate.management.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
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

/**
 * 管理者専用REST APIエンドポイントを提供するコントローラー
 */
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

  public AdminController(AdminService adminService, JwtUtil jwtUtil) {
    this.adminService = adminService;
    this.jwtUtil = jwtUtil;
  }

  /**
   * 削除済み以外の全てのユーザー情報を取得します。
   *
   * @return ユーザー情報リスト
   */
  @GetMapping("/users")
  @Operation(summary = "全ユーザーの一覧取得", description = "システム内の全ユーザー情報を取得")
  public ResponseEntity<List<User>> getAllUsers() {

    List<User> users = adminService.getAllUsers();
    return ResponseEntity.ok(users);

  }

  /**
   * ユーザーステータスの更新をします。
   *
   * @param userId        更新対象のユーザーID
   * @param statusRequest ステータスリクエストDTO
   * @param httpRequest   httpリクエスト
   * @return 結果のレスポンス
   */
  @PutMapping("/users/{userId}/updateStatus")
  @Operation(summary = "指定ユーザーのステータス変更", description = "指定ユーザーのrole、enabled、login_failed_attempts、account_locked_untilを変更")
  public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable int userId,
      @Valid @RequestBody StatusRequest statusRequest, HttpServletRequest httpRequest) {

    String token = jwtUtil.extractTokenFromRequest(httpRequest);

    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(jwtUtil.createErrorResponse("MISSING_TOKEN",
              "Authorization ヘッダーが見つかりません"));
    }
    int selfUserId = jwtUtil.getUserIdFromToken(token);

    adminService.updateStatus(userId, selfUserId, statusRequest);

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("role", statusRequest.getRole());
    response.put("enabled", statusRequest.isEnabled());
    response.put("login_failed_attempts", statusRequest.getLoginFailedAttempts());
    response.put("account_locked_until", statusRequest.getAccountLockedUntil());
    response.put("message",
        "ステータスを変更しました。\nRole: " + statusRequest.getRole() + "\nEnabled: "
            + statusRequest.isEnabled() + "\nLoginFailedAttempts: "
            + statusRequest.getLoginFailedAttempts()
            + "\nAccountLockedUntil: " + statusRequest.getAccountLockedUntil());
    return ResponseEntity.ok(response);

  }

}
