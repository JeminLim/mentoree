package com.mentoree.member.domain;

import com.mentoree.global.domain.BaseTimeEntity;
import com.mentoree.global.domain.UserRole;
import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    //변경 불가능
    private String memberName;
    private String email;
    private String oAuthId;

    //변경 가능
    private String userPassword;
    private String nickname;
    private String link;

    @Builder
    public Member(String memberName, String email, String oAuthId, String nickname, String userPassword, String link, UserRole role) {
        Assert.notNull(email, "email must not be null");

        this.memberName = memberName;
        this.oAuthId = oAuthId;
        this.email = email;
        this.nickname = nickname;
        this.userPassword = userPassword;
        this.link = link;
        this.role = role;
    }

    //== 변경 로직 ==//
    public void updateLink(String link) { this.link = link; }
    public void updateNickname(String nickname) {this.nickname = nickname;}
    public void changePassword(String password) { this.userPassword = password;}
    public Member updateOAuthInfo(String memberName) { this.memberName = memberName;
                                                        return this;}

}
