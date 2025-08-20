package com.katok09.realestate.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
  @NotBlank(message = "ユーザー名を入力してください。")
  private String username;
  @Schema(description = "メールアドレス", example = "newuser@example.com")
  @NotBlank(message = "メールアドレスを入力してください。")
  @Email(message = "有効なメールアドレスを入力してください。")
  private String email;
  @Schema(description = "表示名", example = "新規ユーザー")
  @NotBlank(message = "表示名を入力してください。")
  private String displayName;

  // 現在のパスワード、新規のパスワードが共にnullの時はパスワード更新処理はしない為@NotBlankは使っていません。
  // 現在のパスワードに値があり、新規のパスワードがnullの時はAuthService.validateUserUpdate()のバリデーションにて例外を発生させます。
  @Schema(description = "現在のパスワード", example = "password123")
  private String currentPassword;
  @Schema(description = "新規のパスワード", example = "password789")
  @Size(min = 6, message = "パスワードは6文字以上で入力してください。")
  private String newPassword;

}
