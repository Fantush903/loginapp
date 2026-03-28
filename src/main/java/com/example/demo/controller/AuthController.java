package com.example.demo.controller;

import com.example.demo.JwtUtil;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController               // Tells Spring: "This class handles HTTP requests"
@RequestMapping("/auth")     // All endpoints start with /auth
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    // ✅ REGISTER Endpoint
    // POST http://localhost:8080/auth/register

    @PostMapping("/register")
    public ResponseEntity<?>  register(@Valid @RequestBody User user) {
        try {
            String result = userService.registerUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);

//            String result = userService.registerUser(user);
//            return ResponseEntity.ok(result);
        }

    }

    // ✅ LOGIN Endpoint      // ✅ LOGIN — returns JWT token
    // POST http://localhost:8080/auth/login

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
       try {
           String email = request.get("email");
           String password = request.get("password");
           String token = userService.loginUser(email, password);

           // ✅ Return token to frontend
           Map<String, String> response = new HashMap<>();
           response.put("token", token);
           response.put("message", "Login successful!");
           return ResponseEntity.ok(response);

       } catch (RuntimeException e) {
           Map<String, String> error = new HashMap<>();
           error.put("error", e.getMessage());
           return ResponseEntity.badRequest().body(error);
       }

    }

    // ✅ TEST Endpoint — just to check app is running
    // ✅ PROTECTED endpoint — only works with valid token
    // GET http://localhost:8080/auth/hello

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestHeader("Authorization") String authHeader) {
//        return ResponseEntity.ok("App is running");

        try{
            String token = authHeader.replace("Bearer ", "");

            if(!jwtUtil.isTokenValid(token)){
                return  ResponseEntity.status(401).body("Invalid token!");
            }

            String email = jwtUtil.extractEmail(token);
            Map<String, String> response = new HashMap<>();
            response.put("email", email);
            response.put("message", "Welcome! Token is valid ✅");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized!");
        }
    }
}
