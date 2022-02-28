package com.mentoree.global.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long id;

    private String email;
    private String uuid;
    @Column(length = 1000)
    private String accessToken;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public RefreshToken(String email, String uuid, String accessToken, UserRole role) {
        this.email = email;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.role = role;
    }

    public void updateRefreshToken(String uuid, String accessToken) {
        this.uuid = uuid;
        this.accessToken = accessToken;
    }

}
