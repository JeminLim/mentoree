package com.matching.mentoree.domain;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //변경 불가능
    private String username;
    private String email;

    //변경 가능
    private String userPassword;
    private String nickname;
    private String originProfileImgUrl;
    private String thumbnailImgUrl;
    private String link;

    @Builder
    public Member(String username, String email, String nickname, String userPassword, String originProfileImgUrl, String thumbnailImgUrl, String link) {
        Assert.notNull(username, "username must not be null");
        Assert.notNull(email, "email must not be null");
        Assert.notNull(userPassword, "userPassword must not be null");
        Assert.notNull(nickname, "nickname must not be null");

        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.userPassword = userPassword;
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.link = link;
    }

    //== 변경 로직 ==//
    public void updateProfileImg(String originProfileImgUrl, String thumbnailImgUrl) {
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
    }
    public void updateLink(String link) { this.link = link; }
    public void updateNickname(String nickname) {this.nickname = nickname;}
    public void changePassword(String password) { this.userPassword = password;}





}
