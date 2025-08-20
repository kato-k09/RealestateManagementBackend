package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "ユーザー情報")
@Getter
@Setter
public class User {

  @Schema(description = "ユーザーID DB登録時に自動採番されます。", example = "1")
  private int id;
  @Schema(description = "ユーザー名（ログイン用）", example = "yamada_taro")
  private String username;
  @Schema(description = "パスワード（ハッシュ化されて保存）", example = "password123")
  private String password;
  @Schema(description = "メールアドレス", example = "yamada@example.com")
  private String email;
  @Schema(description = "表示名", example = "山田太郎")
  private String displayName;
  @Schema(description = "ユーザーの役割", example = "USER")
  private String role;
  @Schema(description = "アカウント有効フラグ", example = "true")
  private boolean enabled;
  @Schema(description = "作成日時")
  private LocalDateTime createdAt;
  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;
  @Schema(description = "最終ログイン日時")
  private LocalDateTime lastLoginAt;
  @Schema(description = "パスワード変更日時")
  private LocalDateTime passwordChangedAt;
  @Schema(description = "ログイン連続失敗回数")
  private int loginFailedAttempts;
  @Schema(description = "アカウントロック終了時間")
  private LocalDateTime accountLockedUntil;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
