package art.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import art.model.JWTManager;
import art.model.Users;
import art.model.UsersManager;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UsersController {

    @Autowired
    private UsersManager usersManager;

    @Autowired
    private JWTManager jwtManager;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Users user) {
        String response = usersManager.addUser(user);
        if (response.startsWith("200")) {
            return ResponseEntity.ok(Map.of("status", "200", "message", "User Registered Successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "401", "message", response));
    }


    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Users user) {
        if (usersManager.validateCredentials(user.getEmail(), user.getPassword())) {
            String token = jwtManager.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of(
                "status", "200",
                "message", "Login Successful",
                "token", token
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "401", "message", "Invalid Credentials"));
    }

    @GetMapping("/recover/{email}")
    public ResponseEntity<Map<String, String>> recoverPassword(@PathVariable String email) {
        String response = usersManager.recoverPassword(email);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @GetMapping("/fullname")
    public ResponseEntity<Map<String, String>> getFullname(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "401", "message", "Missing or Invalid Token"));
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        String email = jwtManager.validateToken(token);
        
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "401", "message", "Invalid or Expired Token"));
        }

        return ResponseEntity.ok(Map.of("status", "200", "fullname", usersManager.getFullname(email)));
    }
}