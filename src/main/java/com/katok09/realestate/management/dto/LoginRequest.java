package com.katok09.realestate.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
  @NotBlank(message = "ユーザー名を入力してください。")
  private String username;
  @Schema(description = "パスワード", example = "password123")
  @NotBlank(message = "パスワードを入力してください。")
  private String password;

}
