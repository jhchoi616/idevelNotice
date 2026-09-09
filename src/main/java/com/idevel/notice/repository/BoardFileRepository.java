package com.idevel.notice.repository;

import com.idevel.notice.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {

    List<BoardFile> findByBoardIdOrderBySortOrderAsc(Long boardId);
}