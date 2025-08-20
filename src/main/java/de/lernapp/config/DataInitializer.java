package de.lernapp.config;

import de.lernapp.model.User;
import de.lernapp.model.Role;
import de.lernapp.model.Role.RoleName;
import de.lernapp.repository.UserRepository;
import de.lernapp.repository.RoleRepository;
import de.lernapp.repository.QuestionRepository;
import de.lernapp.service.MassiveCsvImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final MassiveCsvImportService csvImportService;

    @Bean
    CommandLineRunner init() {
        return args -> {
            // Create roles if not exist
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(RoleName.ROLE_USER);
                        role.setDescription("Standard user role");
                        return roleRepository.save(role);
                    });

            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(RoleName.ROLE_ADMIN);
                        role.setDescription("Administrator role");
                        return roleRepository.save(role);
                    });

            // Create test users if not exist
            if (!userRepository.existsByUsername("testuser")) {
                User testUser = User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .password(passwordEncoder.encode("Test123!"))
                        .firstName("Test")
                        .lastName("User")
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(userRole)))
                        .totalQuestionsAnswered(0)
                        .correctAnswers(0)
                        .currentStreak(0)
                        .bestStreak(0)
                        .experiencePoints(0)
                        .level(1)
                        .build();
                testUser.setCreatedAt(LocalDateTime.now());
                testUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(testUser);
                log.info("Created test user: testuser / Test123!");
            }

            if (!userRepository.existsByUsername("admin")) {
                User adminUser = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("Admin123!"))
                        .firstName("Admin")
                        .lastName("User")
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(adminRole)))
                        .totalQuestionsAnswered(0)
                        .correctAnswers(0)
                        .currentStreak(0)
                        .bestStreak(0)
                        .experiencePoints(0)
                        .level(1)
                        .build();
                adminUser.setCreatedAt(LocalDateTime.now());
                adminUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(adminUser);
                log.info("Created admin user: admin / Admin123!");
            }

            // Import CSV questions if database is empty
            long questionCount = questionRepository.count();
            if (questionCount == 0) {
                log.info("No questions found. Importing from CSV files...");
                try {
                    csvImportService.importAllCsvFiles();
                    long newCount = questionRepository.count();
                    log.info("Successfully imported {} questions from CSV files", newCount);
                } catch (Exception e) {
                    log.error("Error importing CSV questions", e);
                }
            } else {
                log.info("Database already contains {} questions", questionCount);
            }
            
            log.info("Data initialization completed!");
        };
    }
}