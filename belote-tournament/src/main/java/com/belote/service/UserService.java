package com.belote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belote.entity.User;
import com.belote.entity.UserRole;
import com.belote.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordService passwordService;
    
    public User createUser(String username, String password) {
        return createUser(username, password, UserRole.USER);
    }

    public User createAdmin(String username, String password) {
        return createUser(username, password, UserRole.ADMIN);
    }

    private User createUser(String username, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordService.hashPassword(password));
        user.setRole(role);
        
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public User updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        user.setPassword(passwordService.hashPassword(newPassword));
        return userRepository.save(user);
    }
    
    public boolean verifyPassword(String username, String password) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return passwordService.checkPassword(password, user.getPassword());
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateUserRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        // Don't allow changing the admin role if it's the last admin
        if (user.getRole() == UserRole.ADMIN && role != UserRole.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .count();
            if (adminCount <= 1) {
                throw new RuntimeException("Cannot remove the last admin");
            }
        }
        
        user.setRole(role);
        userRepository.save(user);
    }
}
