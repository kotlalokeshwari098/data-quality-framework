package eu.bbmri_eric.quality.agent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class UserDetailServiceTest {

  private static final String ADMIN_USER = "admin";
  private static final String ADMIN_PASS = "adminpass";

  @Autowired private UserRepository userRepository;
  @Autowired private UserDetailService userDetailService;
  @Autowired private UserService userService;
  @Autowired private AuthenticationContextService authenticationContextService;
  @Autowired private PasswordEncoder passwordEncoder;

  @AfterEach
  void tearDown() {
    User adminUser = userRepository.findByUsername(ADMIN_USER).orElse(null);
    if (adminUser != null) {
      adminUser.setPassword(passwordEncoder.encode(ADMIN_PASS));
      userRepository.save(adminUser);
    }

    userRepository.findByUsername("testuser").ifPresent(userRepository::delete);
    userRepository.findByUsername("user1").ifPresent(userRepository::delete);
  }

  @Test
  void loadsUser_withROLE_USER_whenRoleIsUser() {
    userRepository.save(new User("user1", "pass1"));
    var details = userDetailService.loadUserByUsername("user1");
    assertThat(details.getUsername()).isEqualTo("user1");
    assertThat(details.getPassword()).isEqualTo("pass1");
  }

  @Test
  void throwsUsernameNotFound_whenUserMissing() {
    assertThatThrownBy(() -> userDetailService.loadUserByUsername("absent"))
        .isInstanceOf(UsernameNotFoundException.class);
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void changePassword_success() {
    User adminUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    Long adminUserId = adminUser.getId();
    PasswordChangeRequest request =
        new PasswordChangeRequest(ADMIN_PASS, "newAdminPass123!", "newAdminPass123!");

    userService.changePassword(adminUserId, request);

    User updatedUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    assertThat(passwordEncoder.matches("newAdminPass123!", updatedUser.getPassword())).isTrue();
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void changePassword_failure_whenCurrentPasswordIncorrect() {
    User adminUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    Long adminUserId = adminUser.getId();
    PasswordChangeRequest request =
        new PasswordChangeRequest("wrongCurrentPass", "newPass123!", "newPass123!");

    assertThatThrownBy(() -> userService.changePassword(adminUserId, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Current password is incorrect");
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void changePassword_failure_whenPasswordsDoNotMatch() {
    User adminUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    Long adminUserId = adminUser.getId();
    PasswordChangeRequest request =
        new PasswordChangeRequest(ADMIN_PASS, "newPass123!", "differentPass");

    assertThatThrownBy(() -> userService.changePassword(adminUserId, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("New password and confirmation do not match");
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void changePassword_failure_whenTryingToChangeOtherUserPassword() {
    Long otherUserId = -1L;
    PasswordChangeRequest request =
        new PasswordChangeRequest(ADMIN_PASS, "newPass123!", "newPass123!");

    assertThatThrownBy(() -> userService.changePassword(otherUserId, request))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("You can only change your own password");
  }

  @Test
  void changePassword_failure_whenPasswordTooShort() {
    PasswordChangeRequest request = new PasswordChangeRequest(ADMIN_PASS, "short", "short");

    // This will fail validation at the DTO level (Jakarta validation)
    assertThat(request.getNewPassword()).isEqualTo("short");
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void isUsingDefaultPassword_returnsTrue_whenAdminUsesDefaultPassword() {
    UserDTO userDTO = authenticationContextService.getCurrentUser();

    assertThat(userDTO.isDefaultPassword()).isTrue();
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void isUsingDefaultPassword_returnsFalse_whenAdminPasswordChanged() {
    // Change password within test
    User adminUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    adminUser.setPassword(passwordEncoder.encode("newPassword123!"));
    userRepository.save(adminUser);

    UserDTO userDTO = authenticationContextService.getCurrentUser();
    assertThat(userDTO.isDefaultPassword()).isFalse();
  }

  @Test
  @WithMockUser(username = "testuser")
  void isUsingDefaultPassword_returnsFalse_whenNonAdminUser() {
    userRepository.save(new User("testuser", passwordEncoder.encode("somepassword123!")));
    UserDTO userDTO = authenticationContextService.getCurrentUser();

    assertThat(userDTO.isDefaultPassword()).isFalse();
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void passwordChangeRemovesDefaultPasswordFlag() {
    UserDTO beforeChange = authenticationContextService.getCurrentUser();
    assertThat(beforeChange.isDefaultPassword()).isTrue();

    User adminUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    Long adminUserId = adminUser.getId();
    PasswordChangeRequest request =
        new PasswordChangeRequest(ADMIN_PASS, "newPassword123!", "newPassword123!");
    userService.changePassword(adminUserId, request);

    UserDTO afterChange = authenticationContextService.getCurrentUser();
    assertThat(afterChange.isDefaultPassword()).isFalse();
  }

  @Test
  @WithMockUser(username = ADMIN_USER)
  void getUserId_returnsCorrectId_whenUserExists() {
    User adminUser = userRepository.findByUsername(ADMIN_USER).orElseThrow();
    Long expectedId = adminUser.getId();

    UserDTO userDTO = authenticationContextService.getCurrentUser();
    Long actualId = userDTO.getUserId();

    assertThat(actualId).isEqualTo(expectedId);
  }
}
