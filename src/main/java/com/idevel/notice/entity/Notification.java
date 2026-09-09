package com.idevel.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알림을 받는 회원
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_notification_member")
    )
    private Member member;

    /**
     * 알림 종류
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    /**
     * 알림 메시지
     */
    @Column(nullable = false, length = 500)
    private String message;

    /**
     * 관련 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "board_id",
        foreignKey = @ForeignKey(name = "fk_notification_board")
    )
    private Board board;

    /**
     * 관련 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "comment_id",
        foreignKey = @ForeignKey(name = "fk_notification_comment")
    )
    private Comment comment;

    /**
     * 읽음 여부
     */
    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(
        Member member,
        NotificationType type,
        String message,
        Board board,
        Comment comment
    ) {
        this.member = member;
        this.type = type;
        this.message = message;
        this.board = board;
        this.comment = comment;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}