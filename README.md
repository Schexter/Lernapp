# Fachinformatiker Lernapp - Java Spring Boot

Eine hochperformante, skalierbare Web-basierte Lernplattform für Fachinformatiker-Azubis mit Enterprise-Features und optimaler User Experience.

## Projekt-Struktur

```
fachinformatiker-lernapp/
├── lernapp-core/           # Domain Models & Business Logic
├── lernapp-security/       # Authentication & Authorization  
├── lernapp-web/           # Web Layer (Controllers & Templates)
├── lernapp-integration/   # Integration Tests
├── build.gradle           # Root Build Configuration
└── settings.gradle        # Module Settings
```

## Technologie-Stack

- **Backend**: Spring Boot 3.2, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, HTMX, Alpine.js, Tailwind CSS
- **Database**: PostgreSQL (Production), H2 (Development)
- **Build**: Gradle 8.5
- **Java**: Version 17

## Installation & Setup

### Voraussetzungen

- Java 17 oder höher
- Gradle 8.5 oder höher (oder nutze den Gradle Wrapper)
- PostgreSQL 14+ (für Produktion)

### Build & Run

```bash
# Build das Projekt
./gradlew build

# Starte die Anwendung
./gradlew :lernapp-web:bootRun

# Run Tests
./gradlew test

# Run Integration Tests
./gradlew :lernapp-integration:test
```

### Development Mode

```bash
# Mit DevTools und H2 Database
./gradlew :lernapp-web:bootRun --args='--spring.profiles.active=dev'
```

## Module

### lernapp-core
Enthält die Domain Models, Repositories und Business Services.

### lernapp-security
Implementiert Authentication, Authorization und JWT Token Management.

### lernapp-web
Web-Layer mit REST Controllers, Thymeleaf Templates und Static Resources.

### lernapp-integration
Integration und End-to-End Tests mit Testcontainers.

## Features

- User Management & Authentication
- Adaptive Learning Algorithm
- Question/Answer System
- Progress Tracking
- Examination Mode
- Admin Dashboard

## Lizenz

Alle Rechte vorbehalten - Hans Hahn