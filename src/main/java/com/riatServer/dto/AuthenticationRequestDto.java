package com.riatServer.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
  //  private Long id;
    private String username;
    private String password;
}