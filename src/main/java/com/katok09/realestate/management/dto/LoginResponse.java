package com.katok09.realestate.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ログインレスポンス")
public class LoginResponse {

  @Schema(description = "トークン文字列")
  private String token;
  @Schema(description = "トークンタイプ")
  private String type;
  @Schema(description = "ユーザー情報")
  private UserInfo userInfo;

  public LoginResponse(String token, UserInfo userInfo) {
    this.token = token;
    this.type = "Bearer";
    this.userInfo = userInfo;
  }

}
