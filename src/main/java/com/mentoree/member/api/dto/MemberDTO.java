package com.mentoree.member.api.dto;

import com.mentoree.member.domain.Member;
import com.mentoree.global.domain.UserRole;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static com.mentoree.program.api.dto.ProgramDTO.*;

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
    @NoArgsConstructor
    public static class MemberInfo {
        private String email;
        private String memberName;
        private String nickname;
        private List<String> interests = new ArrayList<>();
        private String link;
        private List<ParticipatedProgramDTO> participatedPrograms = new ArrayList<>();

        @Builder
        public MemberInfo(String email, String memberName, String nickname, List<String> interests, String link, List<ParticipatedProgramDTO> participatedPrograms) {
            this.email = email;
            this.memberName = memberName;
            this.nickname = nickname;
            this.interests = interests;
            this.link = link;
            this.participatedPrograms = participatedPrograms;
        }

        public static MemberInfo of(Member member) {
            return MemberInfo.builder()
                    .email(member.getEmail())
                    .memberName(member.getMemberName())
                    .nickname(member.getNickname())
                    .link(member.getLink())
                    .build();
        }

        public Member toEntity() {
            return Member.builder()
                    .email(email)
                    .memberName(memberName)
                    .nickname(nickname)
                    .link(link)
                    .build();
        }
    }

}
