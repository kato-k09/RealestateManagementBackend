package com.katok09.realestate.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  @JsonProperty("token")
  private String token;
  @JsonProperty("type")
  private String type;
  @JsonProperty("userInfo")
  private UserInfo userInfo;

  public LoginResponse(String token, UserInfo userInfo) {
    this.token = token;
    this.userInfo = userInfo;
    this.type = "Bearer";
  }

}
