package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.QReply;
import com.matching.mentoree.service.dto.ReplyDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.matching.mentoree.domain.QReply.*;

public class ReplyCustomRepositoryImpl implements ReplyCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ReplyCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<ReplyDTO> findRepliesAllByBoard(Long boardId) {
        return queryFactory
                .select(Projections.bean(ReplyDTO.class,
                        reply.board.id,
                        reply.writer.email,
                        reply.content))
                .from(reply)
                .where(reply.board.id.eq(boardId))
                .fetch();
    }


}
