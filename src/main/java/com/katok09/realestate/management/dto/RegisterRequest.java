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
@Schema(description = "ユーザー登録リクエスト")
public class RegisterRequest {

  @Schema(description = "ユーザー名", example = "newuser")
  @JsonProperty("username")
  private String username;
  @Schema(description = "パスワード", example = "password123")
  @JsonProperty("password")
  private String password;
  @Schema(description = "メールアドレス", example = "newuser@example.com")
  @JsonProperty("email")
  private String email;
  @Schema(description = "表示名", example = "新規ユーザー")
  @JsonProperty("displayName")
  private String displayName;

}
