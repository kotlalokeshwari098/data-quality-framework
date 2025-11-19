package eu.bbmri_eric.quality.agent.common;

import org.springframework.security.core.Authentication;

public interface JwtUtil {
  String generateToken(Authentication authentication);

  String extractUsername(String token);

  boolean validateToken(String token, String username);
}
