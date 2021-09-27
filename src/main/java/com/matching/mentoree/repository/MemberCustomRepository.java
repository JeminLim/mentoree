package com.matching.mentoree.repository;

import com.matching.mentoree.service.dto.MemberDTO;
import com.matching.mentoree.service.dto.MemberDTO.MemberInfo;

import java.util.Optional;

public interface MemberCustomRepository {

    Optional<MemberInfo> findMemberInfoByEmail(String email);

}
