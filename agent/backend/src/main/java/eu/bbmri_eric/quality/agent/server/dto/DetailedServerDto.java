package eu.bbmri_eric.quality.agent.server.dto;

import eu.bbmri_eric.quality.agent.server.domain.ServerConnectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.hateoas.server.core.Relation;

/**
 * Detailed Data Transfer Object for returning complete Server information.
 *
 * <p>Extends ServerDto and adds sensitive fields and interactions for detailed view operations.
 */
@Schema(description = "Detailed server information including interactions and credentials")
@Relation(itemRelation = "server", collectionRelation = "servers")
public class DetailedServerDto extends ServerDto {

  /** Client ID used for authentication with the server. */
  @Schema(description = "Client ID used for authentication", example = "client-12345")
  private String clientId;

  /** List of interactions logged for this server. */
  @Schema(description = "List of interactions logged for this server")
  private List<ServerInteractionDto> interactions;

  /** Default constructor. */
  public DetailedServerDto() {
    super();
  }

  /**
   * Constructor with all fields.
   *
   * @param id the server ID
   * @param url the server URL
   * @param name the server name
   * @param status the server status
   * @param clientId the client ID
   * @param interactions the list of interactions
   */
  public DetailedServerDto(
      String id,
      String url,
      String name,
      ServerConnectionStatus status,
      String clientId,
      List<ServerInteractionDto> interactions) {
    super(id, url, name, status);
    this.clientId = clientId;
    this.interactions = interactions;
  }

  /**
   * Gets the client ID.
   *
   * @return the client ID
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets the client ID.
   *
   * @param clientId the client ID
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Gets the interactions.
   *
   * @return the interactions
   */
  public List<ServerInteractionDto> getInteractions() {
    return interactions;
  }

  /**
   * Sets the interactions.
   *
   * @param interactions the interactions
   */
  public void setInteractions(List<ServerInteractionDto> interactions) {
    this.interactions = interactions;
  }
}
