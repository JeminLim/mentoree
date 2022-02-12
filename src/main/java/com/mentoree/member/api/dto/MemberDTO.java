package com.mentoree.member.api.dto;

import com.mentoree.member.domain.Member;
import com.mentoree.global.domain.UserRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static com.mentoree.program.api.dto.ProgramDTO.*;

public class MemberDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(description = "회원가입 요청 폼")
    public static class MemberRegistRequest {

        @NotBlank
        @Email
        @ApiModelProperty(value = "이메일")
        private String email;

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&#])[A-Za-z\\d$@!%*?&]{8,16}")
        @ApiModelProperty(value = "비밀번호", notes = "최소 8자 이상 최대 16자 이하, 최소 하나 이상의 대,소문자, 숫자, 특수문자 요구")
        private String password;

        @NotBlank
        @ApiModelProperty(value = "회원 이름")
        private String memberName;

        @NotBlank
        @Pattern(regexp = "^[A-Za-z가-힣]{2,15}")
        @ApiModelProperty(value = "회원 닉네임", notes = "최소 2자 이상 최대 15이하")
        private String nickname;

        @Builder
        public MemberRegistRequest(String email, String password, String memberName, String nickname) {
            this.email = email;
            this.password = password;
            this.memberName = memberName;
            this.nickname = nickname;
        }

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
    @ApiModel(description = "회원 정보")
    public static class MemberInfo {

        @NotBlank
        @ApiModelProperty(value = "회원 이메일")
        private String email;

        @NotBlank
        @ApiModelProperty(value = "회원 이름")
        private String memberName;

        @NotBlank
        @ApiModelProperty(value = "회원 닉네임")
        private String nickname;

        @ApiModelProperty(value = "회원 관심분야 리스트")
        private List<String> interests = new ArrayList<>();

        @ApiModelProperty(value = "회원 경력 상세")
        private String link;

        @ApiModelProperty(value = "회원 참가 프로그램 리스트")
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
