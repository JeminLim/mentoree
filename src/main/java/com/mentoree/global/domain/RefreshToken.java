package com.mentoree.global.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long id;

    private String email;
    private String uuid;
    private String accessToken;

    @Builder
    public RefreshToken(String email, String uuid, String accessToken) {
        this.email = email;
        this.uuid = uuid;
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String uuid, String accessToken) {
        this.uuid = uuid;
        this.accessToken = accessToken;
    }

}
