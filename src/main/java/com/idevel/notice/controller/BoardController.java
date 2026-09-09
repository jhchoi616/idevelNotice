package com.idevel.notice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.idevel.notice.dto.BoardListDto;
import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardCategory;
import com.idevel.notice.repository.BoardFileRepository;
import com.idevel.notice.service.BoardService;
import com.idevel.notice.service.CommentService;



@Controller 
@RequestMapping("/board")
@RequiredArgsConstructor 
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final BoardFileRepository boardFileRepository;
    @GetMapping("/write")
    public String boardWrite() {
        return "board/write";
    }



    @GetMapping("/{category}")
    public String board(
        @PathVariable("category") String category,
         @PageableDefault ( size = 10, sort = "createdAt", direction = Sort.Direction.DESC ) Pageable pageable, 
        Model model
    ) {
        
        BoardCategory boardCategory = BoardCategory.from(category);
        Page<BoardListDto> boardPage =boardService.findBoards(boardCategory, pageable);
        System.out.println("조회 카테고리 = "+category);
        System.out.println("조회 카테고리2 = "+boardCategory);
        model.addAttribute("category", boardCategory);
        model.addAttribute("boards",boardPage.getContent());
        model.addAttribute("page",boardPage);


        return "board/list";
    }

    @GetMapping("/{category}/{id}")
    public String detail(
            @PathVariable("category") String category,
            @PathVariable("id") Long id,
            Model model
    ) {

        BoardCategory boardCategory;

    try {
        boardCategory = BoardCategory.valueOf(category.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
    }

    Board board = boardService.findByIdAndIncreaseViewCount(id);

    if (board.getCategory() != boardCategory) {
        throw new IllegalArgumentException("잘못된 게시글 주소입니다.");
    }

    model.addAttribute("board", board);
    model.addAttribute("comments", commentService.findByBoardId(id));

    model.addAttribute(
            "files",
            boardFileRepository.findByBoardIdOrderBySortOrderAsc(id)
    );


        return "board/detail";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("category")
            BoardCategory category,

            @RequestParam("title")
            String title,

            @RequestParam("content")
            String content,

            @RequestParam(value = "files", required = false)
            MultipartFile[] files,

            Authentication authentication
    ) {

        Board board = boardService.write(
                category,
                title,
                content,
                authentication.getName(),
                files
        );
        System.out.println(board);

        return "redirect:/board/" + category.name().toLowerCase();
    }
    

// @PostMapping("/{category}/{id}/comment")
// public String writeComment(
//         @PathVariable("category") String category,
//         @PathVariable("id") Long id,
//         @RequestParam("content") String content,
//         Authentication authentication
// ) {
//     commentService.writeComment(
//             id,
//             content,
//             authentication.getName()
//     );

//     return "redirect:/board/" + category.toLowerCase() + "/" + id;
// }

}
