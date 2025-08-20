package de.lernapp.service;

import de.lernapp.model.User;
import de.lernapp.model.Role;
import de.lernapp.model.Role.RoleName;
import de.lernapp.repository.UserRepository;
import de.lernapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import de.lernapp.dto.RegisterRequest;

/**
 * Service für User-Verwaltung und Authentifizierung
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
    
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        return registerUserExtended(username, email, password, firstName, lastName, null, null, null);
    }
    
    /**
     * Erweiterte Registrierung mit beruflichen Informationen
     */
    public User registerUserExtended(String username, String email, String password, 
                                   String firstName, String lastName,
                                   String ausbildungsrichtung, String ausbildungsjahr, String berufsschule) {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        // TODO: Erweitere User-Entity um berufliche Felder
        // user.setAusbildungsrichtung(ausbildungsrichtung);
        // user.setAusbildungsjahr(ausbildungsjahr);
        // user.setBerufsschule(berufsschule);
        
        // Add default role
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(RoleName.ROLE_USER)
                            .description("Standard user role")
                            .build();
                    return roleRepository.save(newRole);
                });
        
        user.addRole(userRole);
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public List<User> getLeaderboard() {
        return userRepository.findTop10ByCorrectAnswers();
    }
    
    public User updateUserProgress(String username, boolean isCorrect) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        if (isCorrect) {
            user.incrementCorrectAnswer();
        } else {
            user.incrementWrongAnswer();
        }
        
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}