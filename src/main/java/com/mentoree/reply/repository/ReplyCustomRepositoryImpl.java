package com.mentoree.reply.repository;

import com.mentoree.reply.api.dto.ReplyDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mentoree.domain.QBoard.*;
import static com.mentoree.domain.QMember.*;
import static com.mentoree.domain.QReply.*;

public class ReplyCustomRepositoryImpl implements ReplyCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ReplyCustomRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<ReplyDTO> findRepliesAllByBoard(Long boardId) {
        return queryFactory
                .select(Projections.bean(ReplyDTO.class,
                        reply.id.as("replyId"),
                        reply.board.id.as("boardId"),
                        reply.writer.nickname.as("writerNickname"),
                        reply.content,
                        reply.modifiedDate))
                .from(reply)
                .join(reply.writer, member)
                .join(reply.board, board)
                .where(reply.board.id.eq(boardId))
                .fetch();
    }


}
