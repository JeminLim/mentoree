package com.matching.mentoree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String email;
    private String userPassword;
    private String originProfileImgUrl;
    private String thumbnailImgUrl;
    private String link;

    @Builder
    public Member(String username, String email, String password, String originProfileImgUrl, String thumbnailImgUrl, String link) {
        Assert.notNull(username, "username must not be null");
        Assert.notNull(email, "email must not be null");
        Assert.notNull(password, "password must not be null");

        this.username = username;
        this.email = email;
        this.userPassword = password;
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.link = link;
    }

    //== 비지니스 로직 ==//
    public void updateProfile(String originProfileImgUrl, String thumbnailImgUrl) {
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
    }

    public void updateLink(String link) { this.link = link; }

    public void changePassword(String password) { this.userPassword = password;}





}
