package com.idevel.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_file")
@Getter
@NoArgsConstructor
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 첨부된 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "board_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_board_file_board")
    )
    private Board board;

    /**
     * 사용자가 업로드한 원본 파일명
     * 예: 고양이사진.png
     */
    @Column(nullable = false, length = 255)
    private String originalFileName;

    /**
     * 서버에 실제 저장되는 파일명
     * 예: 8f3a2c1e-....png
     */
    @Column(nullable = false, length = 255)
    private String storedFileName;

    /**
     * 실제 파일이 저장된 경로
     */
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * 파일 크기(byte)
     */
    @Column(nullable = false)
    private long fileSize;

    /**
     * MIME 타입
     * 예: image/png, image/jpeg
     */
    @Column(nullable = false, length = 100)
    private String contentType;

    /**
     * 첨부파일 표시 순서
     */
    @Column(nullable = false)
    private int sortOrder = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public BoardFile(
        Board board,
        String originalFileName,
        String storedFileName,
        String filePath,
        long fileSize,
        String contentType,
        int sortOrder
    ) {
        this.board = board;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.sortOrder = sortOrder;
    }
}