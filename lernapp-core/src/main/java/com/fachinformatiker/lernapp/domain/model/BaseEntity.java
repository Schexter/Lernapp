package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Basis-Entity für alle Domain Models.
 * Stellt gemeinsame Felder wie ID und Timestamps zur Verfügung.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 * 
 * Alle Rechte vorbehalten - Hans Hahn
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Prüft ob die Entity persistent ist (bereits in DB gespeichert).
     */
    public boolean isPersistent() {
        return id != null;
    }

    /**
     * Wird vor dem ersten Speichern aufgerufen.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
    }

    /**
     * Wird vor jedem Update aufgerufen.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
