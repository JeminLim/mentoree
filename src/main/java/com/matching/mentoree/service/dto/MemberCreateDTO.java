package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Member;
import lombok.*;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor
public class MemberCreateDTO {

    private String memberName;
    private String nickname;
    private String email;
    private String userPassword;

    @Builder
    public MemberCreateDTO(String memberName, String email, String nickname, String userPassword) {
        this.memberName = memberName;
        this.email = email;
        this.nickname = nickname;
        this.userPassword = userPassword;
    }

    public Member toEntity() {
        return Member.builder()
                .memberName(memberName)
                .nickname(nickname)
                .email(email)
                .userPassword(userPassword)
                .build();
    }
}
