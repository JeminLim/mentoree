package com.matching.mentoree.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member extends BaseTimeEntity implements UserDetails {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    //변경 불가능
    private String memberName;
    private String email;

    //변경 가능
    private String userPassword;
    private String nickname;
    private String originProfileImgUrl;
    private String thumbnailImgUrl;
    private String link;

    @Builder
    public Member(String memberName, String email, String nickname, String userPassword, String originProfileImgUrl, String thumbnailImgUrl, String link, UserRole role) {
        Assert.notNull(memberName, "memberName must not be null");
        Assert.notNull(email, "email must not be null");
        Assert.notNull(userPassword, "userPassword must not be null");
        Assert.notNull(nickname, "nickname must not be null");

        this.memberName = memberName;
        this.email = email;
        this.nickname = nickname;
        this.userPassword = userPassword;
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.link = link;
        this.role = role;
    }

    //== 변경 로직 ==//
    public void updateProfileImg(String originProfileImgUrl, String thumbnailImgUrl) {
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
    }
    public void updateLink(String link) { this.link = link; }
    public void updateNickname(String nickname) {this.nickname = nickname;}
    public void changePassword(String password) { this.userPassword = password;}



    //== UserDetails implement ==//
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getKey()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
