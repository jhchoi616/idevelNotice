package com.idevel.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
public class MemberSignupRequest {

    private String username;
    private String password;
    private String nickname;
    private String email;
}