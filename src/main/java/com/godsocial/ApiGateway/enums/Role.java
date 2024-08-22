package com.godsocial.ApiGateway.enums;

public enum Role {
    ADMIN(0),
    MOD(1),
    USER(2);
    private final int role;

    Role(int role){
        this.role = role;
    }
    public int getValue(){
        return this.role;
    }
}
