package com.idevel.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor 
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 게시글 내용
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 작성자
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_board_member")
    )
    private Member writer;

    /**
     * 게시판 종류
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardCategory category;

    /**
     * 조회수
     */
    @Column(nullable = false)
    private int viewCount = 0;

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

    public Board(
        String title,
        String content,
        Member writer,
        BoardCategory category
    ) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.category = category;
    }

    public void increaseViewCount() {
    this.viewCount++;
    }

}