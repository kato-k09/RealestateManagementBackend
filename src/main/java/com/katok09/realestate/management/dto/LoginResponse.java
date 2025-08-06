package com.katok09.realestate.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  private String token;
  private String type;
  private UserInfo userInfo;

  public LoginResponse(String token, UserInfo userInfo) {
    this.token = token;
    this.userInfo = userInfo;
    this.type = "Bearer";
  }

}
