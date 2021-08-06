package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor
public class MemberCreateDTO {

    private String username;
    private String nickname;
    private String email;
    private String userPassword;

    @Builder
    public MemberCreateDTO(String username, String email, String nickname, String userPassword) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.userPassword = userPassword;
    }

    public Member toEntity() {
        return Member.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .userPassword(userPassword)
                .build();
    }
}
