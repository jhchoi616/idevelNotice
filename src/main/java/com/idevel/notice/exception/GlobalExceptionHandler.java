package com.idevel.notice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(
            IllegalArgumentException e,
            Model model,
            HttpServletResponse response
    ) {
        // System.out.println("여기임 ? 1");
        model.addAttribute("message", e.getMessage());

        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException(
            NoHandlerFoundException e,
            HttpServletResponse response,
            Model model
    ) {
        // System.out.println("여기임 ? 2");
        model.addAttribute("message", "존재하지 않는 페이지입니다.");
        model.addAttribute("status", 404);
        response.setStatus(404);
        return "error";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(
            ResponseStatusException e,HttpServletResponse response,
            Model model
    ) {
        // System.out.println("여기임 ? 3");
        response.setStatus(e.getStatusCode().value());
        model.addAttribute("message", e.getReason());
        model.addAttribute("status", e.getStatusCode().value());

        return "error";
    }
    
}