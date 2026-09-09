package com.idevel.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 댓글이 작성된 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "board_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_comment_board")
    )
    private Board board;

    /**
     * 댓글 작성자
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_comment_member")
    )
    private Member member;

    /**
     * 부모 댓글
     *
     * NULL  → 일반 댓글
     * 값 존재 → 대댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "parent_id",
        foreignKey = @ForeignKey(name = "fk_comment_parent")
    )
    private Comment parent;

    /**
     * 댓글 내용
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 비밀댓글 여부
     */
    @Column(nullable = false)
    private boolean secret = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 일반 댓글 생성
     */
    public Comment(
        Board board,
        Member member,
        String content,
        boolean secret
    ) {
        this.board = board;
        this.member = member;
        this.content = content;
        this.secret = secret;
    }

    /**
     * 대댓글 생성
     */
    public Comment(
        Board board,
        Member member,
        Comment parent,
        String content,
        boolean secret
    ) {
        this.board = board;
        this.member = member;
        this.parent = parent;
        this.content = content;
        this.secret = secret;
    }
}