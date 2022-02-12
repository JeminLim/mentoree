package com.mentoree.mission.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.mentoree.mission.api.dto.MissionDTOCollection.*;
import static com.mentoree.mission.domain.QMission.*;
import static com.mentoree.program.domain.QProgram.*;

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
    public List<MissionDTO> findMissionList(Long programId, boolean isOpen) {
        return queryFactory.select(Projections.fields(MissionDTO.class,
                mission.id,
                mission.title,
                mission.goal,
                mission.content,
                mission.dueDate))
                .from(mission)
                .join(mission.program, program)
                .where(mission.program.id.eq(programId)
                        ,isOpen(isOpen))
                .fetch();
    }

    private BooleanExpression isOpen(boolean isOpen) {
        LocalDate now = LocalDate.now();
        return isOpen ? mission.dueDate.after(now) : mission.dueDate.before(now);
    }

}
