package com.katok09.realestate.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "ユーザーステータス変更リクエスト")
public class StatusRequest {

  @Schema(description = "ロール", example = "ADMIN")
  @NotBlank(message = "ロールは必須です。")
  @Pattern(regexp = "^(ADMIN|USER|GUEST)$", message = "ADMINまたはUSERまたはGUESTを選択してください。")
  String role;
  @Schema(description = "ユーザーの有効", example = "true")
  boolean enabled;
  @Schema(description = "ログイン失敗回数", example = "3")
  int loginFailedAttempts;
  @Schema(description = "アカウントロック期限", example = "2025-08-01T00:00:00")
  LocalDateTime accountLockedUntil;
}

