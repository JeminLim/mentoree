package com.mentoree.board.repository;

import com.mentoree.board.api.dto.BoardDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static com.mentoree.board.domain.QBoard.*;
import static com.mentoree.mission.domain.QMission.*;
import static com.mentoree.member.domain.QMember.*;


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
