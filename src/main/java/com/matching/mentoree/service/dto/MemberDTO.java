package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Program;
import com.matching.mentoree.domain.UserRole;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class MemberDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString(of = {"email", "password", "memberName", "nickname"})
    public static class RegistrationRequest {

        private String email;
        private String password;
        private String memberName;
        private String nickname;

        @Builder
        public RegistrationRequest(String email, String password, String memberName, String nickname) {
            this.email = email;
            this.password = password;
            this.memberName = memberName;
            this.nickname = nickname;
        }

        // Entity 가 가지는 의존성 보다 dto 가 가지는 의존성을 지니는 것이 좋...을까?
        public Member toEntity(PasswordEncoder encoder, UserRole role) {
            return Member.builder()
                    .email(this.email)
                    .userPassword(encoder.encode(this.password))
                    .memberName(this.memberName)
                    .nickname(this.nickname)
                    .role(role)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class MemberInfo {

        private String email;
        private String memberName;
        private String nickname;
        private String originProfileImgUrl;
        private String thumbnailImgUrl;
        private String link;
        private List<Program> programList = new ArrayList<>();

        @Builder
        public MemberInfo(String email, String memberName, String nickname, String originProfileImgUrl, String thumbnailImgUrl, String link, List<Program> programList) {
            this.email = email;
            this.memberName = memberName;
            this.nickname = nickname;
            this.originProfileImgUrl = originProfileImgUrl;
            this.thumbnailImgUrl = thumbnailImgUrl;
            this.link = link;
            this.programList = programList;
        }

        public Member toEntity() {
            return Member.builder()
                    .email(email)
                    .memberName(memberName)
                    .nickname(nickname)
                    .originProfileImgUrl(originProfileImgUrl)
                    .thumbnailImgUrl(thumbnailImgUrl)
                    .link(link)
                    .build();
        }
    }

}
