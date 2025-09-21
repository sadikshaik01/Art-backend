package art.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import art.model.Users;

public interface UsersRepository extends JpaRepository<Users, String> { 
    // Custom query to find users by role
    Optional<Users> findByEmail(String email); 
}