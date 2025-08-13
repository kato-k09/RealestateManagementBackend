package com.katok09.realestate.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.katok09.realestate.management.config.JwtRequestFilter;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.service.AdminService;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AdminService adminService;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private JwtRequestFilter jwtRequestFilter;

  @MockBean
  private JwtUtil jwtUtil;

  @Test
  void 全ユーザーのユーザー情報が取得できOKレスポンスが返ってくること() throws Exception {

    when(adminService.getAllUsers()).thenReturn(new ArrayList<>());

    mockMvc.perform(get("/api/admin/users"))
        .andExpect(status().isOk());

    verify(adminService, times(1)).getAllUsers();

  }

  @Test
  void IDに紐づいたユーザーのステータスを変更でき変更レスポンスとOKレスポンスが返ってくること()
      throws Exception {

    StatusRequest statusRequest = new StatusRequest();
    statusRequest.setRole("ADMIN");
    statusRequest.setEnabled(false);
    statusRequest.setLoginFailedAttempts(0);
    statusRequest.setAccountLockedUntil(null);

    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn("DummyToken");
    when(jwtUtil.getUserIdFromToken("DummyToken")).thenReturn(1);
    doNothing().when(adminService).statusChange(999, 1, statusRequest);

    mockMvc.perform(put("/api/admin/users/{id}/statusChange", 999)
            .contentType("application/Json")
            .content(
                """
                    {
                        "role": "ADMIN",
                        "enabled": false,
                        "login_failed_attempts": 0,
                        "account_locked_until": null
                    }
                    """
            ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(
            "ステータスを変更しました。\nRole: ADMIN\nEnabled: false\nLoginFailedAttempts: 0\nAccountLockedUntil: null"))
        .andExpect(jsonPath("$.role").value("ADMIN"))
        .andExpect(jsonPath("$.enabled").value(false))
        .andExpect(jsonPath("$.login_failed_attempts").value(0))
        .andExpect(jsonPath("$.account_locked_until").doesNotExist());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, times(1)).getUserIdFromToken(any(String.class));
    verify(adminService, times(1)).statusChange(anyInt(), anyInt(), any(StatusRequest.class));
  }


  @Test
  void IDに紐づいたユーザーのステータスを変更する時トークンが見つからない場合エラーレスポンスが返ってくること()
      throws Exception {

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", true);
    errorResponse.put("errorCode", "MISSING_TOKEN");
    errorResponse.put("message", "Authorization ヘッダーが見つかりません");
    errorResponse.put("timestamp", System.currentTimeMillis());

    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(null);
    when(jwtUtil.createErrorResponse("MISSING_TOKEN", "Authorization ヘッダーが見つかりません"))
        .thenReturn(errorResponse);

    mockMvc.perform(put("/api/admin/users/{id}/statusChange", 999)
            .contentType("application/Json")
            .content(
                """
                    {
                        "role": "ADMIN",
                        "enabled": false,
                        "login_failed_attempts": 0,
                        "account_locked_until": null
                    }
                    """
            ))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("MISSING_TOKEN"))
        .andExpect(jsonPath("$.message").value("Authorization ヘッダーが見つかりません"));

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, never()).getUserIdFromToken(any(String.class));
    verify(adminService, never()).statusChange(anyInt(), anyInt(), any(StatusRequest.class));
  }

}
