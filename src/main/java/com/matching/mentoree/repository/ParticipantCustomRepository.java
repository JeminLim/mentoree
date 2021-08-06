package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Participant;

import java.util.List;

public interface ParticipantCustomRepository {

    void rejectAll();
    List<Participant> findParticipateHistory(Member targetMember);

}
