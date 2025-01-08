package com.belote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.belote.entity.TournamentStatus;
import com.belote.entity.UserRole;
import com.belote.service.TournamentService;
import com.belote.service.UserService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    public String adminPanel(Model model) {
        // Get statistics for the admin dashboard
        model.addAttribute("totalTournaments", tournamentService.getAllTournaments().size());
        model.addAttribute("activeTournaments", 
            tournamentService.getAllTournaments().stream()
                .filter(t -> t.getStatus() == TournamentStatus.IN_PROGRESS)
                .count());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/role")
    public String updateUserRole(
            @PathVariable("userId") Long userId, 
            @RequestParam("role") String roleStr,  
            RedirectAttributes redirectAttributes) {
        try {
            UserRole role = UserRole.valueOf(roleStr);  
            userService.updateUserRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "User role updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid role specified");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}