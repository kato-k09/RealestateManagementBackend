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
@Schema(description = "ユーザー情報")
public class UserInfo {

  @Schema(description = "ユーザーID")
  private int id;
  @Schema(description = "ユーザー名")
  private String username;
  @Schema(description = "表示名")
  private String displayName;
  @Schema(description = "メールアドレス")
  private String email;
  @Schema(description = "役割")
  private String role;

}
