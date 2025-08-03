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
@Schema(description = "ユーザー更新リクエスト")
public class UpdateRequest {

  @Schema(description = "ユーザー名", example = "newuser")
  @JsonProperty("username")
  private String username;
  @Schema(description = "メールアドレス", example = "newuser@example.com")
  @JsonProperty("email")
  private String email;
  @Schema(description = "表示名", example = "新規ユーザー")
  @JsonProperty("displayName")
  private String displayName;
  @Schema(description = "現在のパスワード", example = "password123")
  @JsonProperty("currentPassword")
  private String currentPassword;
  @Schema(description = "新規のパスワード", example = "password789")
  @JsonProperty("newPassword")
  private String newPassword;

}
