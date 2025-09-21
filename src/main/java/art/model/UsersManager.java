package art.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import art.repository.UsersRepository;

@Service
public class UsersManager {
    @Autowired
    private UsersRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // ✅ Initialize properly

    // ✅ Improved User Signup (No duplicate password hashing)
    public String addUser(Users user) {    
        if (userRepository.existsById(user.getEmail())) {
            return "401::Email already exists";    
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash before saving
        userRepository.save(user);
        return "200::User Registered Successfully";
    }

    // ✅ Secure Password Recovery (Avoid exposing passwords directly)
    public String recoverPassword(String email) {
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return String.format("Dear %s, please follow the password reset process.", optionalUser.get().getFullname());
        }
        return "404::Email not found";
    }

    // ✅ Fix Validate Credentials Logic (Ensures hashed password comparison)
    public boolean validateCredentials(String email, String password) {
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword());
    }

    // ✅ Improved Get Full Name Method
    public String getFullname(String email) {
        return userRepository.findByEmail(email).map(Users::getFullname).orElse("404::User not found");
    }
}