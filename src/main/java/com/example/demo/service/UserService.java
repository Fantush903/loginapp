package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.JwtUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service      //  Tells Spring: "This is a business logic class"
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ Inject BCrypt

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Register — password encrypted before saving
    public String registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // ✅ Encrypt password before saving to DB
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return "User registered successfully!";
    }

    // ✅ Login — now returns JWT token
    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(password, user.getPassword())) {    // ✅ BCrypt compare — never compare plain text!
            throw new RuntimeException("Wrong password!");
        }

        // ✅ Generate and return JWT token
        return jwtUtil.generateToken(email);
    }

    // ✅ Get by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    // ✅ Get by Email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    // ✅ Request Reset Code — shows code on screen
    public Map<String, String> requestReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found!"));

        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setResetCode(code);
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("resetCode", code);
        response.put("message", "Reset code ready!");
        return response;
    }

    // ✅ Confirm Reset
    public String confirmReset(String email, String resetCode, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!resetCode.equals(user.getResetCode()))
            throw new RuntimeException("Invalid reset code!");

        if (user.getResetCodeExpiry().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Reset code expired!");

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetCode(null);
        user.setResetCodeExpiry(null);
        userRepository.save(user);
        return "Password changed successfully!";
    }

}
