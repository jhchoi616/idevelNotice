package com.idevel.notice.service;

import com.idevel.notice.dto.BoardListDto;
import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardCategory;
import com.idevel.notice.entity.BoardFile;
import com.idevel.notice.entity.Member;
import com.idevel.notice.repository.BoardRepository;
import com.idevel.notice.repository.CommentRepository;
import com.idevel.notice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileService boardFileService;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;


    public Page<BoardListDto> findBoards( BoardCategory category, Pageable pageable ) {
         Page<Board> boards =
            boardRepository.findByCategoryOrderByCreatedAtDesc(
                category,
                pageable
        );
        return boards.map(board -> {

        int commentCount =
                (int) commentRepository.countByBoardId(board.getId());

        return new BoardListDto(
                board,
                commentCount
        );
    });
    }
//     제목 검색
     public Page<BoardListDto> searchTitleBoards(
            BoardCategory category,
            String keyword,
            Pageable pageable
    ) {
        Page<Board> boards = boardRepository.findByCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                category,
                keyword,
                pageable
        );
        return boards.map(board -> {

        int commentCount =
                (int) commentRepository.countByBoardId(board.getId());

        return new BoardListDto(
                board,
                commentCount
        );   });
    }
//     내용 검색
     public Page<BoardListDto> searchContentBoards(
            BoardCategory category,
            String keyword,
            Pageable pageable
    ) {
        Page<Board> boards = boardRepository.findByCategoryAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
                category,
                keyword,
                pageable
        );
        return boards.map(board -> {

        int commentCount =
                (int) commentRepository.countByBoardId(board.getId());

        return new BoardListDto(
                board,
                commentCount
        );   });
    }

//     제목이랑 내용 검색
    public Page<BoardListDto> searchBoards(
            BoardCategory category,
            String keyword,
            Pageable pageable
    ) {
        Page<Board> boards = boardRepository.search(
                category,
                keyword,
                pageable
        );
        return boards.map(board -> {

        int commentCount =
                (int) commentRepository.countByBoardId(board.getId());

        return new BoardListDto(
                board,
                commentCount
        );   });
    }
    
// 전체 인기글 리스트 조회
    public Page<BoardListDto> findPopularBoards(Pageable pageable) {
        // List<BoardCategory> categories = List.of(
        //         BoardCategory.FREE,
        //         BoardCategory.QUESTION,
        //         BoardCategory.INFO
        // );
Page<Board> boards = boardRepository.findAllByOrderByViewCountDescCreatedAtDesc(
                pageable
        );
        return boards.map(board -> {

        int commentCount =
                (int) commentRepository.countByBoardId(board.getId());

        return new BoardListDto(
                board,
                commentCount
        );   });
    }

    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글을 찾을 수 없습니다.")
                );
    }

    @Transactional
    public Board findByIdAndIncreaseViewCount(Long boardId) {
        Board board = findById(boardId);
        board.increaseViewCount();

        return board;
    }

    @Transactional 
   public Board write(
            BoardCategory category,
            String title,
            String content,
            String username,
            MultipartFile[] files
    ) {

        /*
         * 작성자 조회
         */
        Member writer = memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다.")
                );


        /*
         * 게시글 생성
         */
        Board board = new Board(
                title,
                content,
                writer,
                category
        );


        /*
         * 게시글 저장
         */
        Board savedBoard = boardRepository.save(board);


        /*
         * 자료실인 경우에만 파일 처리
         */
        if (category == BoardCategory.DATA
                && files != null) {
                    boardFileService.saveFiles(
                    savedBoard,
                    files
            );
// 여기는 파일리스트 있을 때 파일리스트 돌면서 파일들 꺼내서 검증인듯 이건 그냥 보드 파일 서비스로 넘김
            // for (int i = 0; i < files.length; i++) { 

            //     MultipartFile file = files[i];

            //     if (file == null || file.isEmpty()) {
            //         continue;
            //     }

            //     // 파일 검증 및 저장
            //     // BoardFile 생성
            //     // boardFileRepository.save(...)
            // }
        }
        return savedBoard;
    }


    @Transactional 
    public void update(
        BoardCategory category,
        Long id,
        String title,
        String content,
        String username,
        MultipartFile[] files,
        List<Long> deletedFileIds
) {
        
    Board board = boardRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 게시글입니다.")
            );

    // 작성자 본인인지 확인
    if (!board.getWriter().getUsername().equals(username)) {
        throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("제목을 입력해주세요.");
    }

    if (title.length() > 10) {
        throw new IllegalArgumentException("제목은 10자까지 입력할 수 있습니다.");
    }

    if (content == null || content.isBlank()) {
        throw new IllegalArgumentException("내용을 입력해주세요.");
    }

    if (content.length() > 300) {
        throw new IllegalArgumentException("현재 앞단의 300자와 뒷단의 300자 기준 맞추는 중입니다. 뒷단 기준 내용은 300자를 초과했습니다.");
    }

    //여기서 수정 전 카테고리 및 파일 처리 여부 결정
    BoardCategory oldCategory = board.getCategory();

    board.update(title, content);

    board.updateCategory(category);

     if (oldCategory == BoardCategory.DATA
            && category != BoardCategory.DATA) {

        /*
         * DATA → FREE / QUESTION
         *
         * 기존 첨부파일 전부 삭제
         */
        boardFileService.deleteAllByBoard(board);

    } else if (category == BoardCategory.DATA) {
System.out.println("파일 없는 경우에도 여기는 들어오는데");
        /*
         * DATA → DATA
         * FREE → DATA
         * QUESTION → DATA
         * 여기서 deletedFileIds가 없어도 무관 required false임
         * 기존 파일 삭제 처리 + 새 파일 추가
         */
        boardFileService.updateFiles(
                board,
                files,
                deletedFileIds
        );
    }
    }

    @Transactional 
    public void delete(Long id, String username) {

    Board board = boardRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 게시글입니다.")
            );

    // 작성자 본인인지 확인
    if (!board.getWriter().getUsername().equals(username)) {
        throw new IllegalArgumentException("삭제 권한이 없습니다.");
    }

     for (BoardFile file : board.getFiles()) {

        try {
            Path path = Paths.get(file.getFilePath());

            Files.deleteIfExists(path);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "첨부파일 삭제에 실패했습니다.", e
            );
        }
    }

    boardRepository.delete(board);
}

        // 24시간 이내 많이 본 인기글 4등까지
        public List<Board> popularBoardTop4() {
                LocalDateTime since = LocalDateTime.now().minusHours(24);

                return boardRepository.findTop4ByCreatedAtAfterOrderByViewCountDescCreatedAtDesc( since );
        }
}