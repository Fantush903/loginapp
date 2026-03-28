package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.JwtUtil;

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

        // ✅ BCrypt compare — never compare plain text!
        if (!passwordEncoder.matches(password, user.getPassword())) {
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
}
