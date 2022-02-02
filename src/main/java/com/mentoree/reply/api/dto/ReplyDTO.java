package com.mentoree.reply.api.dto;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.reply.domain.Reply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "댓글 정보")
public class ReplyDTO {

    @ApiModelProperty(value = "댓글 엔티티 id")
    private Long replyId;

    @NotNull
    @ApiModelProperty(value = "게시글 id")
    private Long boardId;

    @NotNull
    @ApiModelProperty(value = "게시글 작성자 닉네임")
    private String writerNickname;

    @NotNull
    @ApiModelProperty(value = "댓글 내용")
    private String content;

    @NotNull
    @ApiModelProperty(value = "댓글 작성 및 수정 시간")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
