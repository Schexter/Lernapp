package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    Set<Permission> findByNameIn(Set<String> names);
    
    Set<Permission> findByResource(String resource);
    
    Set<Permission> findByResourceAndAction(String resource, String action);
    
    boolean existsByName(String name);
}