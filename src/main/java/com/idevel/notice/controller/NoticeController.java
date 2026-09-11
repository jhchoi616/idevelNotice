package com.idevel.notice.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import com.idevel.notice.entity.BoardCategory;
import com.idevel.notice.repository.BoardRepository;
import com.idevel.notice.service.BoardService;


@Controller 
@RequiredArgsConstructor 
public class NoticeController {
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("frees",boardRepository.findByCategoryOrderByCreatedAtDesc(BoardCategory.FREE,PageRequest.of(0, 3)));
        model.addAttribute("questions",boardRepository.findByCategoryOrderByCreatedAtDesc(
    BoardCategory.QUESTION,
    PageRequest.of(0, 3)
));
        model.addAttribute("infos",boardRepository.findByCategoryOrderByCreatedAtDesc(
    BoardCategory.INFO,
    PageRequest.of(0, 3)
));
        model.addAttribute("datas",boardRepository.findByCategoryOrderByCreatedAtDesc(
    BoardCategory.DATA,
    PageRequest.of(0, 3)
));

// 인기 순
model.addAttribute("popularBoards", boardService.popularBoardTop4());
        return "index";
    }
    
}
