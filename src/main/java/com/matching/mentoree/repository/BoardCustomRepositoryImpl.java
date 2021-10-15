package com.matching.mentoree.repository;

import com.matching.mentoree.domain.QBoard;
import com.matching.mentoree.domain.QMember;
import com.matching.mentoree.domain.QMission;
import com.matching.mentoree.service.dto.BoardDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.matching.mentoree.domain.QBoard.*;
import static com.matching.mentoree.domain.QMember.*;
import static com.matching.mentoree.domain.QMission.*;
import static com.matching.mentoree.service.dto.BoardDTO.*;

public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory queryFactory;

    public BoardCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Optional<BoardInfo> findBoardInfoById(Long id) {
        return Optional.ofNullable(
                queryFactory.select(Projections.bean( BoardInfo.class,
                        board.id.as("boardId"),
                        board.mission.id.as("missionId"),
                        board.mission.title.as("missionTitle"),
                        board.writer.nickname.as("writerNickname"),
                        board.content))
                .from(board)
                .join(board.mission, mission)
                .join(board.writer, member)
                .where(board.id.eq(id))
                .fetchOne()
        );
    }

    @Override
    public List<BoardInfo> findAllBoardInfoById(Long missionId) {
        return queryFactory.select(Projections.bean( BoardInfo.class,
                board.id.as("boardId"),
                board.mission.id.as("missionId"),
                board.mission.title.as("missionTitle"),
                board.writer.nickname.as("writerNickname"),
                board.content))
                .from(board)
                .join(board.mission, mission)
                .join(board.writer, member)
                .where(mission.id.eq(missionId))
                .fetch();
    }
}
