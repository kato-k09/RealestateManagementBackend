package com.katok09.realestate.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ログインリクエスト")
public class LoginRequest {

  @Schema(description = "ユーザー名", example = "admin")
  @JsonProperty("username")
  private String username;
  @Schema(description = "パスワード", example = "password123")
  @JsonProperty("password")
  private String password;

}
