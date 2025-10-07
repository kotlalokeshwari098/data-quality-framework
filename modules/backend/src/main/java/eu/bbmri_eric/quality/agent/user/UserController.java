package eu.bbmri_eric.quality.agent.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User Management", description = "APIs for user authentication and password management")
public class UserController {

  private final UserService userService;
  private final AuthenticationContextService authenticationContextService;

  public UserController(
      UserService userService, AuthenticationContextService authenticationContextService) {
    this.userService = userService;
    this.authenticationContextService = authenticationContextService;
  }

  @Operation(
      summary = "Get current user information",
      description =
          "Retrieves information about the currently authenticated user including default password status and user ID")
  @GetMapping("/api/login")
  public UserDTO login() {
    return authenticationContextService.getCurrentUser();
  }

  @Operation(
      summary = "Change user password",
      description =
          "Changes the password for a specific user. Users can only change their own password. Requires valid current password and new password confirmation.")
  @PutMapping("/api/users/{userId}/password")
  public void changePassword(
      @Parameter(
              description =
                  "ID of the user whose password should be changed. Must match the current user's ID.",
              required = true,
              example = "1")
          @PathVariable
          Long userId,
      @Parameter(
              description =
                  "Password change request containing current password, new password, and confirmation",
              required = true)
          @Valid
          @RequestBody
          PasswordChangeRequest request) {
    userService.changePassword(userId, request);
  }
}
