package com.kamran.Docmind.Exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex,Model model) {
        ex.printStackTrace();
         model.addAttribute(
                "message",
                "Something went wrong. Please try again later."
        );
        model.addAttribute("error",ex.getMessage());
        return "error"; // Return the name of the error view
    }
  
}
