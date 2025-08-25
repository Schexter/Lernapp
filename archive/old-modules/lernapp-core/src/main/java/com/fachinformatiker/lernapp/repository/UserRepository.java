package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.active = true")
    Page<User> findAllActive(Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.createdAt < :date")
    List<User> findUnverifiedUsersOlderThan(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date AND u.active = true")
    List<User> findInactiveUsersSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.currentLevel >= :level")
    List<User> findByMinimumLevel(@Param("level") Integer level);
    
    @Query("SELECT u FROM User u WHERE u.totalPoints >= :points ORDER BY u.totalPoints DESC")
    List<User> findTopPerformers(@Param("points") Integer points);
    
    @Query("SELECT u FROM User u ORDER BY u.totalPoints DESC")
    Page<User> findLeaderboard(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.learningStreak > 0 ORDER BY u.learningStreak DESC")
    List<User> findActiveLearnersWithStreak();
    
    @Query("SELECT u FROM User u WHERE u.passwordResetToken = :token AND u.passwordResetExpires > :now")
    Optional<User> findByValidPasswordResetToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token")
    Optional<User> findByEmailVerificationToken(@Param("token") String token);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);
    
    @Modifying
    @Query("UPDATE User u SET u.learningStreak = u.learningStreak + 1 WHERE u.id = :userId")
    void incrementLearningStreak(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE User u SET u.learningStreak = 0 WHERE u.id = :userId")
    void resetLearningStreak(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE User u SET u.totalPoints = u.totalPoints + :points WHERE u.id = :userId")
    void addPoints(@Param("userId") Long userId, @Param("points") Integer points);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    Long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    Long countNewUsersSince(@Param("startDate") LocalDateTime startDate);
    
    @Query(value = "SELECT u.* FROM users u " +
           "JOIN user_topics ut ON u.id = ut.user_id " +
           "WHERE ut.topic_id = :topicId", nativeQuery = true)
    List<User> findUsersInterestedInTopic(@Param("topicId") Long topicId);
}