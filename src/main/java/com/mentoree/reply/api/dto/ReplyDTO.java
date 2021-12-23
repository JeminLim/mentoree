package com.mentoree.reply.api.dto;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.reply.domain.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReplyDTO {

    private Long replyId;
    private Long boardId;
    private String writerNickname;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd 'T' HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public ReplyDTO(Long replyId, Long boardId, String writerNickname, String content, LocalDateTime modifiedDate) {
        this.replyId = replyId;
        this.boardId = boardId;
        this.writerNickname = writerNickname;
        this.content = content;
        this.modifiedDate = modifiedDate;
    }

    public static ReplyDTO of(Reply reply) {
        return ReplyDTO.builder()
                .replyId(reply.getId())
                .boardId(reply.getBoard().getId())
                .writerNickname(reply.getWriter().getNickname())
                .content(reply.getContent())
                .modifiedDate(reply.getModifiedDate())
                .build();
    }

    public Reply toEntity(Board board, Member writer) {
        return Reply.builder()
                .board(board)
                .writer(writer)
                .content(content)
                .build();
    }


}
