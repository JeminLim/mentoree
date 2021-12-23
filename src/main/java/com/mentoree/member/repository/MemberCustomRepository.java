package com.mentoree.member.repository;

import com.mentoree.member.api.dto.MemberDTO;

import java.util.Optional;

public interface MemberCustomRepository {

    Optional<MemberDTO.MemberInfo> findMemberInfoByEmail(String email);

}
