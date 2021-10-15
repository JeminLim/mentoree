package com.matching.mentoree.service.dto;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.Member;
import com.matching.mentoree.domain.Reply;
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

    private Long boardId;
    private String writerNickname;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd 'T' HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public ReplyDTO(Long boardId, String writerNickname, String content) {
        this.boardId = boardId;
        this.writerNickname = writerNickname;
        this.content = content;
    }

    public Reply toEntity(Board board, Member writer) {
        return Reply.builder()
                .board(board)
                .writer(writer)
                .content(content)
                .build();
    }


}
