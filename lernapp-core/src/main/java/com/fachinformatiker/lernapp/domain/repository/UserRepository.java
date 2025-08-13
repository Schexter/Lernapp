package com.fachinformatiker.lernapp.domain.repository;

import com.fachinformatiker.lernapp.domain.enums.UserRole;
import com.fachinformatiker.lernapp.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository für User-Entity.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Findet einen User anhand des Usernamens.
     */
    Optional<User> findByUsername(String username);

    /**
     * Findet einen User anhand der Email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Prüft ob ein Username bereits existiert.
     */
    boolean existsByUsername(String username);

    /**
     * Prüft ob eine Email bereits existiert.
     */
    boolean existsByEmail(String email);

    /**
     * Findet alle User mit einer bestimmten Rolle.
     */
    List<User> findByRole(UserRole role);

    /**
     * Findet alle aktiven User.
     */
    List<User> findByActiveTrue();

    /**
     * Findet alle User mit verifizierten Emails.
     */
    List<User> findByEmailVerifiedTrue();

    /**
     * Sucht User nach Namen (Vor- oder Nachname).
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Findet Top-User nach Punkten.
     */
    List<User> findTop10ByActiveTrueOrderByTotalPointsDesc();

    /**
     * Update Last Login Zeit.
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Findet User die länger nicht eingeloggt waren.
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date OR u.lastLogin IS NULL")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);

    /**
     * Statistik: Anzahl User pro Rolle.
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();
}
