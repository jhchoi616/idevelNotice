package com.idevel.notice.dto;

import java.time.LocalDateTime;

import com.idevel.notice.entity.Comment;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CommentDto {
// memberId, nickname만 뽑아서 쓰기. 보안과 불필요 필드 같이 핸들링 굳이? => 불필요 필드 끌고 다니는 것도 다 자원임 최소화를 습관으로!
    private Long id;
    private Long memberId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.memberId = comment.getMember().getId();
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}