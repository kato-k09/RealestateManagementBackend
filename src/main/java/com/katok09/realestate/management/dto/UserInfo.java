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
@Schema(description = "ユーザー情報")
public class UserInfo {

  @Schema(description = "ユーザーID")
  @JsonProperty("id")
  private Long id;
  @Schema(description = "ユーザー名")
  @JsonProperty("username")
  private String username;
  @Schema(description = "表示名")
  @JsonProperty("displayName")
  private String displayName;
  @Schema(description = "メールアドレス")
  @JsonProperty("email")
  private String email;
  @Schema(description = "役割")
  @JsonProperty("role")
  private String role;

}
