package de.lernapp.repository;

import de.lernapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository für User-Entitäten
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = ?1")
    Optional<User> findByUsernameWithRoles(String username);
    
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllActiveUsers();
    
    @Query("SELECT u FROM User u ORDER BY u.experiencePoints DESC")
    List<User> findTopUsersByExperience();
    
    @Query("SELECT u FROM User u ORDER BY u.correctAnswers DESC LIMIT 10")
    List<User> findTop10ByCorrectAnswers();
}