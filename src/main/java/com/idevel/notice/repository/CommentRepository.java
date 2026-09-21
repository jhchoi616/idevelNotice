package com.idevel.notice.repository;

import com.idevel.notice.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoardIdOrderByCreatedAtAsc(Long boardId);

    long countByBoardId(Long boardId);

    // 게시글 삭제시 댓글 전체 삭제
    void deleteAllByBoardId(Long boardId);

    // 댓글 삭제시 댓글 + 대댓글 삭제
    @Modifying
    @Query("""
        delete from Comment c
        where c.id = :commentId
        or c.parent.id = :commentId
    """)
    void deleteByIdOrParentId(Long commentId);

    Page<Comment> findByBoardIdAndParentIsNull( Long boardId, Pageable pageable );

    Page<Comment> findByParentId( Long parentId, Pageable pageable );
}