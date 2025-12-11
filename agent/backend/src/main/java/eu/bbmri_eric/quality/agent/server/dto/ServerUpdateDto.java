package eu.bbmri_eric.quality.agent.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for updating an existing Server.
 *
 * <p>Contains only the fields that can be updated: name, URL, clientId, and clientSecret. All
 * fields are optional to allow partial updates.
 */
@Getter
@Setter
@Schema(description = "Data for updating an existing server (all fields optional)")
public class ServerUpdateDto {

  /** URL of the central server. */
  @Size(max = 500, message = "URL must not exceed 500 characters")
  @Schema(description = "URL of the central server", example = "https://central.example.com")
  @Pattern(
      regexp = "^https?://[^\\s/$.?#].[^\\s]*$",
      message = "URL must be a valid HTTP or HTTPS URL")
  private String url;

  /** Display name for the server. */
  @Size(max = 255, message = "Name must not exceed 255 characters")
  @Schema(description = "Display name for the server", example = "Production Central Server")
  private String name;

  /** Client ID used for authentication with the server. */
  @Size(max = 255, message = "Client ID must not exceed 255 characters")
  @Schema(description = "Client ID used for authentication", example = "client-12345")
  private String clientId;

  /** Client secret used for authentication with the server. */
  @Size(max = 500, message = "Client secret must not exceed 500 characters")
  @Schema(
      description = "Client secret used for authentication (Base64 encoded)",
      example = "Y2xpZW50LXNlY3JldA==")
  private String clientSecret;

  /** Default constructor. */
  public ServerUpdateDto() {}

  /**
   * Constructor with all fields.
   *
   * @param url the server URL
   * @param name the server name
   * @param clientId the client ID
   * @param clientSecret the client secret
   */
  public ServerUpdateDto(String url, String name, String clientId, String clientSecret) {
    this.url = url;
    this.name = name;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }
}
