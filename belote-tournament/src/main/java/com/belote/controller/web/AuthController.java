package com.belote.controller.web;

import com.belote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username, 
                             @RequestParam("password") String password,
                             Model model) {
        try {
            userService.createUser(username, password);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
