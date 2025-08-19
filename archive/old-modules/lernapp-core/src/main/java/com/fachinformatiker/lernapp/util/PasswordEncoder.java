package com.fachinformatiker.lernapp.util;

/**
 * Interface für Password Encoding
 * Temporäre Lösung bis Spring Security vollständig integriert ist
 * 
 * @author Hans Hahn
 */
public interface PasswordEncoder {
    
    /**
     * Encode the raw password
     * @param rawPassword the raw password
     * @return encoded password
     */
    String encode(CharSequence rawPassword);
    
    /**
     * Verify the encoded password obtained from storage matches the submitted raw password
     * @param rawPassword the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from storage
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
