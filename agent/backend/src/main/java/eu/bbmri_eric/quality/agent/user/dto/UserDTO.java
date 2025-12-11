package eu.bbmri_eric.quality.agent.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Schema(description = "User data transfer object containing user information")
@Getter
@Setter
public class UserDTO {

  @Schema(description = "Username of the user", example = "admin")
  private String username;

  @Schema(description = "Unique identifier of the user", example = "1")
  private Long userId;

  @Schema(description = "Flag indicating if the user is using default password", example = "true")
  private boolean defaultPassword;

  public UserDTO() {
    // No-argument constructor for ModelMapper
  }

  public UserDTO(String username, Long userId) {
    this.userId = userId;
    this.username = username;
    this.defaultPassword = false;
  }

  public UserDTO(String username, boolean defaultPassword, Long userId) {
    this.userId = userId;
    this.username = username;
    this.defaultPassword = defaultPassword;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserDTO userDTO = (UserDTO) o;
    return Objects.equals(username, userDTO.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }
}
