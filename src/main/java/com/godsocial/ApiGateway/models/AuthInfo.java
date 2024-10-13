package com.godsocial.ApiGateway.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthInfo {
    private String userId;
    private String username;
    private int role;
}
