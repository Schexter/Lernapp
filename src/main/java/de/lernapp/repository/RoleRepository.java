package de.lernapp.repository;

import de.lernapp.model.Role;
import de.lernapp.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository für Role-Entitäten
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(RoleName name);
    
    boolean existsByName(RoleName name);
}