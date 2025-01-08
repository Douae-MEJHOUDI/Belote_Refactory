package com.belote.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    
    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
    
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
