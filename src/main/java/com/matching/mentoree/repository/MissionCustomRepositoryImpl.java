package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Mission;
import com.matching.mentoree.domain.QMission;
import com.matching.mentoree.domain.QProgram;
import com.matching.mentoree.service.dto.MissionDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.domain.QMission.*;
import static com.matching.mentoree.domain.QProgram.*;

public class MissionCustomRepositoryImpl implements MissionCustomRepository{

    private final JPAQueryFactory queryFactory;

    public MissionCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<MissionDTO> findCurrentMission(Long programId) {
        LocalDate now = LocalDate.now();
        return queryFactory.select(Projections.fields(MissionDTO.class,
                mission.program.id.as("programId"),
                mission.id.as("missionId"),
                mission.title,
                mission.goal,
                mission.content,
                mission.dueDate))
                .from(mission)
                .join(mission.program, program)
                .where(mission.program.id.eq(programId)
                        .and(mission.dueDate.after(now)))
                .fetch();
    }

    @Override
    public List<MissionDTO> findEndedMission(Long programId) {
        LocalDate now = LocalDate.now();
        return queryFactory.select(Projections.fields(MissionDTO.class,
                mission.program.id.as("programId"),
                mission.id.as("missionId"),
                mission.title,
                mission.goal,
                mission.content,
                mission.dueDate))
                .from(mission)
                .join(mission.program, program)
                .where(mission.program.id.eq(programId)
                        .and(mission.dueDate.before(now)))
                .fetch();
    }
}
