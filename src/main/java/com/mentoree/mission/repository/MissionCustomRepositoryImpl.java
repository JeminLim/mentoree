package com.mentoree.mission.repository;

import com.mentoree.mission.api.dto.MissionDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.mentoree.domain.QMission.*;
import static com.mentoree.domain.QProgram.*;

public class MissionCustomRepositoryImpl implements MissionCustomRepository{

    private final JPAQueryFactory queryFactory;

    public MissionCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Optional<MissionDTO> findMissionById(Long missionId) {
        return Optional.ofNullable(queryFactory.select(Projections.fields(MissionDTO.class,
                mission.id,
                mission.title,
                mission.goal,
                mission.content,
                mission.dueDate))
                .from(mission)
                .where(mission.id.eq(missionId))
                .fetchOne());
    }

    @Override
    public List<MissionDTO> findCurrentMission(Long programId) {
        LocalDate now = LocalDate.now();
        return queryFactory.select(Projections.fields(MissionDTO.class,
                mission.id,
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
                mission.id,
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
