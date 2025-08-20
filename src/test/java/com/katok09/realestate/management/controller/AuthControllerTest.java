package com.katok09.realestate.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.katok09.realestate.management.config.JwtRequestFilter;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.dto.RegisterRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import com.katok09.realestate.management.dto.UserInfo;
import com.katok09.realestate.management.service.AuthService;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  AuthService authService;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private JwtRequestFilter jwtRequestFilter;

  @MockBean
  private JwtUtil jwtUtil;

  @Test
  void ログイン認証が成功した時OKレスポンスが返ってくること() throws Exception {
    LoginResponse dummyResponse = new LoginResponse("DummyToken", new UserInfo());
    when(authService.authenticate(any(LoginRequest.class))).thenReturn(dummyResponse);

    mockMvc.perform(post("/api/auth/login")
            .contentType("application/json")
            .content(
                """
                    {
                        "username":"DummyUser",
                        "password":"DummyPassword"
                    }
                    """
            ))
        .andExpect(status().isOk());

    verify(authService, times(1)).authenticate(any(LoginRequest.class));
  }

  @Test
  void ログイン認証時にユーザー名の入力が無い時BadRequestレスポンスが返ってくること()
      throws Exception {

    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("error", true);
    errorBody.put("errorCode", "VALIDATION_ERROR");
    errorBody.put("message", "ユーザー名は必須です");
    errorBody.put("timestamp", System.currentTimeMillis());

    when(jwtUtil.createErrorResponse("VALIDATION_ERROR", "ユーザー名は必須です")).thenReturn(
        errorBody);

    mockMvc.perform(post("/api/auth/login")
            .contentType("application/json")
            .content(
                """
                    {
                        "username":"",
                        "password":"DummyPassword"
                    }
                    """
            ))
        .andExpect(status().isBadRequest());

    verify(authService, never()).authenticate(any(LoginRequest.class));
  }

  @Test
  void ログイン認証時にパスワードの入力が無い時BadRequestレスポンスが返ってくること()
      throws Exception {

    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("error", true);
    errorBody.put("errorCode", "VALIDATION_ERROR");
    errorBody.put("message", "パスワードは必須です");
    errorBody.put("timestamp", System.currentTimeMillis());

    when(jwtUtil.createErrorResponse("VALIDATION_ERROR", "パスワードは必須です")).thenReturn(
        errorBody);

    mockMvc.perform(post("/api/auth/login")
            .contentType("application/json")
            .content(
                """
                    {
                        "username":"DummyUser",
                        "password":""
                    }
                    """
            ))
        .andExpect(status().isBadRequest());

    verify(authService, never()).authenticate(any(LoginRequest.class));
  }

  @Test
  void ログイン認証時にサービス層でユーザー側の誤りによる例外が発生した時にUnauthorizedレスポンスが返ってくること()
      throws Exception {

    when(authService.authenticate(any(LoginRequest.class)))
        .thenThrow(new BadCredentialsException("ユーザー名またはパスワードが間違っています。"));

    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("error", true);
    errorBody.put("errorCode", "INVALID_CREDENTIALS");
    errorBody.put("message", "ユーザー名またはパスワードが間違っています");
    errorBody.put("timestamp", System.currentTimeMillis());

    when(jwtUtil.createErrorResponse("INVALID_CREDENTIALS",
        "ユーザー名またはパスワードが間違っています。")).thenReturn(errorBody);

    mockMvc.perform(post("/api/auth/login")
            .contentType("application/json")
            .content(
                """
                    {
                        "username":"DummyUser",
                        "password":"DummyPassword"
                    }
                    """
            ))
        .andExpect(status().isUnauthorized());

    verify(authService, times(1)).authenticate(any(LoginRequest.class));
  }

  @Test
  void ログイン認証時にサービス層でユーザー側の誤りでない理由の例外が発生した時にInternalServerErrorレスポンスが返ってくること()
      throws Exception {

    when(authService.authenticate(any(LoginRequest.class)))
        .thenThrow(new RuntimeException("認証処理中にエラーが発生しました。"));

    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("error", true);
    errorBody.put("errorCode", "INTERNAL_ERROR");
    errorBody.put("message", "システムエラーが発生しました");
    errorBody.put("timestamp", System.currentTimeMillis());

    when(jwtUtil.createErrorResponse("INTERNAL_ERROR",
        "システムエラーが発生しました。")).thenReturn(errorBody);

    mockMvc.perform(post("/api/auth/login")
            .contentType("application/json")
            .content(
                """
                    {
                        "username":"DummyUser",
                        "password":"DummyPassword"
                    }
                    """
            ))
        .andExpect(status().isInternalServerError());

    verify(authService, times(1)).authenticate(any(LoginRequest.class));
  }

  @Test
  void ゲストログイン認証が成功した時にOKレスポンスが返ってくること()
      throws Exception {

    LoginResponse dummyResponse = new LoginResponse("DummyToken", new UserInfo());
    when(authService.authenticate(any(LoginRequest.class))).thenReturn(dummyResponse);

    mockMvc.perform(post("/api/auth/guest-login")
            .contentType("application/json")
            .content(
                """
                      {
                            "username":"guest",
                            "password":"guest123"
                      }
                    """
            ))
        .andExpect(status().isOk());

    verify(authService, times(1)).authenticate(any(LoginRequest.class));
  }

  @Test
  void ユーザー登録が成功した時にCreatedレスポンスが返ってくること() throws Exception {

    doNothing().when(authService).registerUser(any(RegisterRequest.class));
    mockMvc.perform(post("/api/auth/register")
            .contentType("application/json")
            .content(
                """
                    {
                        "username": "DummyUser",
                        "password": "DummyPassword",
                        "email": "dummy@dummy.com",
                        "displayName": "DummyDisplayName"
                    }
                    """
            ))
        .andExpect(status().isCreated());

    verify(authService, times(1)).registerUser(any(RegisterRequest.class));
  }

  @Test
  void ユーザー登録時にサービス層でバリデーションによる例外が発生した時にBadRequestレスポンスが返ってくること()
      throws Exception {

    doThrow(new IllegalArgumentException("このユーザー名は既に使用されています")).when(authService)
        .registerUser(any(RegisterRequest.class));

    mockMvc.perform(post("/api/auth/register")
            .contentType("application/json")
            .content(
                """
                    {
                        "username": "DummyUser",
                        "password": "DummyPassword",
                        "email": "dummy@dummy.com",
                        "displayName": "DummyDisplayName"
                    }
                    """
            ))
        .andExpect(status().isBadRequest());

    verify(authService, times(1)).registerUser(any(RegisterRequest.class));
  }

  @Test
  void ユーザー登録時にサーバー側による例外が発生した時にInternalServerErrorレスポンスが返ってくること()
      throws Exception {

    doThrow(new RuntimeException()).when(authService).registerUser(any(RegisterRequest.class));

    mockMvc.perform(post("/api/auth/register")
            .contentType("application/json")
            .content(
                """
                    {
                        "username": "DummyUser",
                        "password": "DummyPassword",
                        "email": "dummy@dummy.com",
                        "displayName": "DummyDisplayName"
                    }
                    """
            ))
        .andExpect(status().isInternalServerError());

    verify(authService, times(1)).registerUser(any(RegisterRequest.class));
  }

  @Test
  void トークン認証が成功した時にOKレスポンスが返ってくること() throws Exception {
    String dummyToken = "DummyToken";
    UserInfo dummyUserInfo = new UserInfo(999, "DummyUser", "DummyUser", "dummy@dummy.com", "USER");

    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(authService.validateToken(dummyToken)).thenReturn(dummyUserInfo);
    when(jwtUtil.getRemainingTimeInMinutes(dummyToken)).thenReturn((long) 60 * 24);

    mockMvc.perform(get("/api/auth/validate")
            .contentType("application/json")
            .header("Authorization", "Bearer DummyToken"))
        .andExpect(status().isOk());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(authService, times(1)).validateToken(dummyToken);
    verify(jwtUtil, times(1)).getRemainingTimeInMinutes(dummyToken);
  }

  @Test
  void トークン認証が失敗した時にUnauthorizedレスポンスが返ってくること() throws Exception {
    String dummyToken = "DummyToken";
    UserInfo dummyUserInfo = null;

    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(authService.validateToken(dummyToken)).thenReturn(dummyUserInfo);
    when(jwtUtil.getRemainingTimeInMinutes(dummyToken)).thenReturn((long) 60 * 24);

    mockMvc.perform(get("/api/auth/validate")
            .contentType("application/json")
            .header("Authorization", "Bearer DummyToken"))
        .andExpect(status().isUnauthorized());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(authService, times(1)).validateToken(dummyToken);
    verify(jwtUtil, never()).getRemainingTimeInMinutes(dummyToken);
  }

  @Test
  void 自身の情報取得が成功した時にOKレスポンスが返ってくること() throws Exception {

    String dummyToken = "DummyToken";
    int dummyUserId = 999;
    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(jwtUtil.getUserIdFromToken(dummyToken)).thenReturn(dummyUserId);
    when(authService.getUserInfo(dummyUserId)).thenReturn(new UserInfo());

    mockMvc.perform(get("/api/auth/me")
            .contentType("application/json")
            .header("Authorization", "Bearer DummyToken"))
        .andExpect(status().isOk());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, times(1)).getUserIdFromToken(dummyToken);
    verify(authService, times(1)).getUserInfo(dummyUserId);
  }

  @Test
  void 自身の情報取得が失敗した時にUnauthorizedレスポンスが返ってくること() throws Exception {

    String dummyToken = null;
    int dummyUserId = 999;
    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(jwtUtil.getUserIdFromToken(dummyToken)).thenReturn(dummyUserId);
    when(authService.getUserInfo(dummyUserId)).thenReturn(new UserInfo());

    mockMvc.perform(get("/api/auth/me")
            .contentType("application/json")
            .header("Authorization", "Bearer DummyToken"))
        .andExpect(status().isUnauthorized());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, never()).getUserIdFromToken(dummyToken);
    verify(authService, never()).getUserInfo(dummyUserId);
  }

  @Test
  void ユーザー情報の更新が成功した時にOKレスポンスが返ってくること() throws Exception {

    String dummyToken = "DummyToken";
    int dummyUserId = 999;
    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(jwtUtil.getUserIdFromToken(dummyToken)).thenReturn(dummyUserId);
    doNothing().when(authService).updateUserInfo(eq(dummyUserId), any(UpdateRequest.class));

    mockMvc.perform(put("/api/auth/updateUserInfo")
            .contentType("application/json")
            .content(
                """
                    {
                          "username":"DummyUser",
                          "email":"dummy@dummy.com",
                          "displayName":"DummyUser"
                    }
                    """
            ))
        .andExpect(status().isOk());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, times(1)).getUserIdFromToken(dummyToken);
    verify(authService, times(1)).updateUserInfo(eq(dummyUserId), any(UpdateRequest.class));
  }

  @Test
  void ユーザー情報の更新時にバリデーションによる例外が発生した時にBadRequestレスポンスが返ってくること()
      throws Exception {

    String dummyToken = "DummyToken";
    int dummyUserId = 999;
    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(jwtUtil.getUserIdFromToken(dummyToken)).thenReturn(dummyUserId);
    doThrow(new IllegalArgumentException("このユーザー名は既に使用されています")).when(authService)
        .updateUserInfo(eq(dummyUserId), any(UpdateRequest.class));

    mockMvc.perform(put("/api/auth/updateUserInfo")
            .contentType("application/json")
            .content(
                """
                    {
                          "username":"DummyUser",
                          "email":"dummy@dummy.com",
                          "displayName":"DummyUser"
                    }
                    """
            ))
        .andExpect(status().isBadRequest());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, times(1)).getUserIdFromToken(dummyToken);
    verify(authService, times(1)).updateUserInfo(eq(dummyUserId), any(UpdateRequest.class));
  }

  @Test
  void ユーザー情報の更新時にサーバー側による例外が発生した時にInternalServerErrorレスポンスが返ってくること()
      throws Exception {

    String dummyToken = "DummyToken";
    int dummyUserId = 999;
    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(jwtUtil.getUserIdFromToken(dummyToken)).thenReturn(dummyUserId);
    doThrow(new RuntimeException()).when(authService)
        .updateUserInfo(eq(dummyUserId), any(UpdateRequest.class));

    mockMvc.perform(put("/api/auth/updateUserInfo")
            .contentType("application/json")
            .content(
                """
                    {
                          "username":"DummyUser",
                          "email":"dummy@dummy.com",
                          "displayName":"DummyUser"
                    }
                    """
            ))
        .andExpect(status().isInternalServerError());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, times(1)).getUserIdFromToken(dummyToken);
    verify(authService, times(1)).updateUserInfo(eq(dummyUserId), any(UpdateRequest.class));
  }

  @Test
  void ログアウトが成功した時にOKレスポンスが返ってくること() throws Exception {

    mockMvc.perform(post("/api/auth/logout"))
        .andExpect(status().isOk());
  }

  @Test
  void 認証システムの動作確認が成功した時にOKレスポンスが返ってくること() throws Exception {

    mockMvc.perform(get("/api/auth/health"))
        .andExpect(status().isOk());
  }

  @Test
  void ユーザーの削除が成功した時にOKレスポンスが返ってくること() throws Exception {

    String dummyToken = "DummyToken";
    int dummyUserId = 999;
    when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(dummyToken);
    when(jwtUtil.getUserIdFromToken(dummyToken)).thenReturn(dummyUserId);
    doNothing().when(authService).deleteUser(dummyUserId);

    mockMvc.perform(delete("/api/auth/deleteUser")
            .contentType("application/json")
            .header("Authorization", "Bearer DummyToken"))
        .andExpect(status().isOk());

    verify(jwtUtil, times(1)).extractTokenFromRequest(any(HttpServletRequest.class));
    verify(jwtUtil, times(1)).getUserIdFromToken(dummyToken);
    verify(authService, times(1)).deleteUser(dummyUserId);
  }

}
