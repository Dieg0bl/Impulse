package com.impulse.infrastructure.web;

import com.impulse.domain.user.User;
import com.impulse.ports.in.UserManagementPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserManagementPort userManagementPort;
    
    public UserController(UserManagementPort userManagementPort) {
        this.userManagementPort = userManagementPort;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userManagementPort.createUser(
                request.getEmail(), 
                request.getUsername(), 
                request.getPassword()
            );
            return ResponseEntity.ok(UserResponse.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginRequest request) {
        try {
            User user = userManagementPort.authenticate(request.getEmail(), request.getPassword());
            userManagementPort.updateLastLogin(user.getId());
            return ResponseEntity.ok(UserResponse.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        try {
            User user = userManagementPort.findById(java.util.UUID.fromString(id));
            return ResponseEntity.ok(UserResponse.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DTOs
    public static class CreateUserRequest {
        private String email;
        private String username;
        private String password;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class UserResponse {
        private String id;
        private String email;
        private String username;
        private String status;
        
        public static UserResponse from(User user) {
            UserResponse response = new UserResponse();
            response.id = user.getId().toString();
            response.email = user.getEmail();
            response.username = user.getUsername();
            response.status = user.getStatus().toString();
            return response;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
