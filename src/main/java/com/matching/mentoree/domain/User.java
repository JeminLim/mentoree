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
@Table(name = "Users")
public class User extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String email;
    private String password;
    private String originProfileImgUrl;
    private String thumbnailImgUrl;
    private String link;

    @Builder
    public User(String username, String email, String password, String originProfileImgUrl, String thumbnailImgUrl, String link) {
        Assert.notNull(username, "username must not be null");
        Assert.notNull(email, "email must not be null");
        Assert.notNull(password, "password must not be null");

        this.username = username;
        this.email = email;
        this.password = password;
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.link = link;
    }




}
