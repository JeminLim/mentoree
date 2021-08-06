package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfoDTO {

    private String username;
    private String nickname;
    private String email;

    private String originProfileImgUrl;
    private String thumbnailImgUrl;
    private String link;

    private List<Program> programList;

    @Builder
    public MemberInfoDTO(String username, String email, String nickname, String originProfileImgUrl, String thumbnailImgUrl, String link, List<Program> programList) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.originProfileImgUrl = originProfileImgUrl;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this. link = link;
        this.programList = programList;
    }



}
