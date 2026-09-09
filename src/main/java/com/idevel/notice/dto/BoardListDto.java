package com.idevel.notice.dto;

import java.time.LocalDateTime;

import com.idevel.notice.entity.Board;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class BoardListDto {
     private Long id;
    private String title;
    private String nickname;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public BoardListDto(Board board, int commentCount) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = board.getWriter().getNickname();
        this.viewCount = board.getViewCount();
        this.commentCount = commentCount;
        this.createdAt = board.getCreatedAt();
    }
}
