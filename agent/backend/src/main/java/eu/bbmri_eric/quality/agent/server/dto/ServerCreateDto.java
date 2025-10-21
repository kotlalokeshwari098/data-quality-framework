package eu.bbmri_eric.quality.agent.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.util.HtmlUtils;

/**
 * Data Transfer Object for creating a new Server.
 *
 * <p>Contains the essential fields required to create a new server entity.
 */
@Schema(description = "Data required to create a new server")
public class ServerCreateDto {

  /** URL of the central server. */
  @NotBlank(message = "URL is required")
  @Size(max = 500, message = "URL must not exceed 500 characters")
  @Pattern(
      regexp = "^https?://[^\\s/$.?#].[^\\s]*$",
      message = "URL must be a valid HTTP or HTTPS URL")
  @Schema(
      description = "URL of the central server",
      example = "https://central.example.com",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String url;

  /** Display name for the server. */
  @NotBlank(message = "Name is required")
  @Size(max = 255, message = "Name must not exceed 255 characters")
  @Schema(
      description = "Display name for the server",
      example = "Production Central Server",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  /** Default constructor. */
  public ServerCreateDto() {}

  /**
   * Constructor with all fields.
   *
   * @param url the server URL
   * @param name the server name
   */
  public ServerCreateDto(String url, String name) {
    this.url = url;
    this.name = name;
  }

  /**
   * Gets the URL.
   *
   * @return the URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the URL.
   *
   * @param url the URL
   */
  public void setUrl(String url) {
    this.url = url != null ? HtmlUtils.htmlEscape(url.trim()) : null;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name != null ? HtmlUtils.htmlEscape(name.trim()) : null;
  }
}
