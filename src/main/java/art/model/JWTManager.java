package art.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTManager {

    // Securely store your secret key (replace with environment variable or secure storage)
    private static final String SECRET_KEY_STRING = System.getenv("JWT_SECRET") != null 
        ? System.getenv("JWT_SECRET") 
        : "ThisIsAStrongDefaultSecretKeyThatIsAtLeast32CharactersLong!"; // Ensure at least 256 bits

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    
    // Define expiration time for JWT token
    private static final long EXPIRATION_TIME = 86400000; // 1 day (24 hours)

    // Generate JWT Token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate JWT Token and return email
    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("JWT Validation Failed: " + e.getMessage());
            return null; // Return null if token is invalid
        }
    }
}