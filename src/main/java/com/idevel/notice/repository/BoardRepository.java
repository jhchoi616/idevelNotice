package com.idevel.notice.repository;

import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    /**
     * 특정 게시판의 최신 게시글
     */
    Page<Board> findByCategoryOrderByCreatedAtDesc(
        BoardCategory category,
        Pageable pageable
    );
    /**
     * 최근 24시간 이내 전체 카테고리 인기 4등 게시글
     */
    List<Board> findTop4ByCreatedAtAfterOrderByViewCountDescCreatedAtDesc(
    LocalDateTime dateTime
    );
    /**
     * 특정 게시판의 인기 게시글
     */
    Page<Board> findByCategoryOrderByViewCountDescCreatedAtDesc(
        BoardCategory category,
        Pageable pageable
    );

    /**
     * 여러 게시판에서 인기 게시글 조회
     * NOTICE를 제외한 인기글 등에 사용
     */
    Page<Board> findByCategoryInOrderByViewCountDescCreatedAtDesc(
        List<BoardCategory> categories,
        Pageable pageable
    );

    /**
     * 제목에 검색어가 포함된 게시글
     */
    Page<Board> findByCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
        BoardCategory category,
        String keyword,
        Pageable pageable
    );

    /**
     * 제목 또는 내용에 검색어가 포함된 게시글 =:JPA에서 제공해주는 네이밍으로 작성할 경우
     */
    Page<Board> findByCategoryAndTitleContainingIgnoreCaseOrCategoryAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
        BoardCategory category,
        String titleKeyword,
        BoardCategory contentCategory,
        String contentKeyword,
        Pageable pageable
    );
    /**
     * 제목 또는 내용 검색
     */
    @Query("""
        SELECT b
        FROM Board b
        WHERE b.category = :category
          AND (
              LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY b.createdAt DESC
        """)
    Page<Board> search(
        @Param("category") BoardCategory category,
        @Param("keyword") String keyword,
        Pageable pageable
    );
    /**
     * 특정 게시글의 조회수 증가
     */
    // 조회수 증가는 Service에서 처리
}