package com.idevel.notice.repository;

import com.idevel.notice.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
// 좋아요 데이터 가져오기인데.... 이긴한데..? 차라리 MemberId로 하고 join해서 보드 데이터 가져오는게 낫지 않나..? JPA에서 찾아보기
    Optional<BoardLike> findByBoardIdAndMemberId(
            Long boardId,
            Long memberId
    );
// 이미 좋아요 한 글인지 체크용도
    boolean existsByBoardIdAndMemberId(
            Long boardId,
            Long memberId
    );
// 좋아요 수 조회
    long countByBoardId(Long boardId);
}