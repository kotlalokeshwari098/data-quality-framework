package eu.bbmri_eric.quality.agent.user;

import static org.springframework.security.core.userdetails.User.withUsername;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Service for fetching user details */
@Service
public class UserDetailService implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(UserDetailService.class);
  private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>_-]{8,}$";
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  UserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return withUsername(user.getUsername()).password(user.getPassword()).build();
  }

  public String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return authentication.getName();
    }
    throw new IllegalStateException("No authenticated user found");
  }

  public UserDTO getCurrentUserDTO() {
    String username = getCurrentUsername();
    User currentUser =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

    boolean isUsingDefault = isUsingDefaultPassword(currentUser.getUsername());
    return new UserDTO(currentUser.getUsername(), isUsingDefault, currentUser.getId());
  }

  private void validatePasswordFormat(String password) {
    if (password == null || !password.matches(PASSWORD_PATTERN)) {
      throw new IllegalArgumentException(
          "Password must be at least 8 characters long and contain only letters, digits or special characters");
    }
  }

  public boolean changePassword(String username, PasswordChangeRequest request) {
    validatePasswordFormat(request.getNewPassword());

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new IllegalArgumentException("New password and confirmation do not match");
    }

    log.info("Password change attempt for user: {}", username);

    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    log.info("Password successfully changed for user: {}", username);
    return true;
  }

  public boolean isUsingDefaultPassword(String username) {
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String defaultPassword = "admin".equals(username) ? "adminpass" : null;

    if (defaultPassword == null) {
      return false;
    }

    return passwordEncoder.matches(defaultPassword, user.getPassword());
  }

  public Long getUserId(String username) {
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return user.getId();
  }
}
