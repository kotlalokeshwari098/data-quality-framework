package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.common.Base64Encoded;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a central server used for reporting.
 *
 * <p>This entity stores connection information for central servers that the agent communicates with
 * to send quality reports. Each server has authentication credentials and a status indicating its
 * availability.
 */
@Entity
public class Server {

  /** Auto-generated unique identifier for the server. */
  @Id private String id = UUID.randomUUID().toString();

  /** URL of the central server. */
  @NotBlank
  @Size(max = 500)
  @Column(name = "url", nullable = false, length = 500)
  private String url;

  /** Display name for the server. */
  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false, length = 255)
  private String name;

  /** Client ID used for authentication with the server. */
  @Size(max = 255)
  @Column(name = "client_id", nullable = false, length = 255)
  private String clientId = "";

  /** Client secret used for authentication with the server. */
  @Size(max = 500)
  @Base64Encoded(message = "Client secret must be Base64 encoded")
  @Column(name = "client_secret", nullable = false, length = 500)
  private String clientSecret = "";

  /** Current status of the server connection. */
  @NotNull
  @Enumerated(EnumType.STRING)
  private ServerStatus status = ServerStatus.PENDING;

  /** List of interactions logged for this server. */
  @OneToMany(mappedBy = "serverId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ServerInteraction> interactions = new ArrayList<>();

  /** Default constructor required by JPA. */
  protected Server() {}

  public Server(
      String url, String name, String clientId, String clientSecret, ServerStatus status) {
    this.url = url;
    this.name = name;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.status = status;
    addInteraction(new ServerInteraction(InteractionType.UPDATE, "Initial Registration"));
  }

  public Server(String url, String name) {
    this.url = url;
    this.name = name;
    addInteraction(new ServerInteraction(InteractionType.UPDATE, "Initial Registration"));
  }

  /**
   * Gets the unique identifier of the server.
   *
   * @return the server ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the URL of the server.
   *
   * @return the server URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the URL of the server.
   *
   * @param url the server URL
   */
  public void setUrl(String url) {
    String oldUrl = this.url;
    this.url = url;
    if (this.id != null && !Objects.equals(oldUrl, url)) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE, String.format("URL updated from '%s' to '%s'", oldUrl, url)));
    }
  }

  /**
   * Gets the display name of the server.
   *
   * @return the server name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the display name of the server.
   *
   * @param name the server name
   */
  public void setName(String name) {
    String oldName = this.name;
    this.name = name;
    if (this.id != null && !Objects.equals(oldName, name)) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE,
              String.format("Name updated from '%s' to '%s'", oldName, name)));
    }
  }

  /**
   * Gets the client ID used for authentication.
   *
   * @return the client ID
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets the client ID used for authentication.
   *
   * @param clientId the client ID
   */
  public void setClientId(String clientId) {
    String oldClientId = this.clientId;
    this.clientId = clientId;
    if (this.id != null && !Objects.equals(oldClientId, clientId)) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE,
              String.format("Client ID updated from '%s' to '%s'", oldClientId, clientId)));
    }
  }

  /**
   * Gets the client secret used for authentication.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Sets the client secret used for authentication.
   *
   * @param clientSecret the client secret
   */
  public void setClientSecret(String clientSecret) {
    boolean hadSecret = this.clientSecret != null && !this.clientSecret.isEmpty();
    this.clientSecret = clientSecret;
    if (this.id != null && hadSecret) {
      addInteraction(new ServerInteraction(InteractionType.UPDATE, "Client secret updated"));
    }
  }

  /**
   * Gets the current status of the server connection.
   *
   * @return the server status
   */
  public ServerStatus getStatus() {
    return status;
  }

  /**
   * Sets the status of the server connection.
   *
   * @param status the server status
   */
  public void setStatus(ServerStatus status) {
    ServerStatus oldStatus = this.status;
    this.status = status;
    if (this.id != null && oldStatus != status) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE,
              String.format("Status changed from %s to %s", oldStatus, status)));
    }
  }

  /**
   * Gets the list of interactions for this server.
   *
   * @return the list of server interactions
   */
  public List<ServerInteraction> getInteractions() {
    return Collections.unmodifiableList(interactions);
  }

  /**
   * Adds an interaction to this server's interaction log.
   *
   * @param interaction the interaction to add
   */
  public void addInteraction(ServerInteraction interaction) {
    interaction.setServerId(this.id);
    interactions.add(interaction);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Server server = (Server) o;
    return Objects.equals(id, server.id)
        && Objects.equals(url, server.url)
        && Objects.equals(name, server.name)
        && Objects.equals(clientId, server.clientId)
        && Objects.equals(clientSecret, server.clientSecret)
        && status == server.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, url, name, clientId, clientSecret, status);
  }

  @Override
  public String toString() {
    return "Server{"
        + "id="
        + id
        + ", url='"
        + url
        + '\''
        + ", name='"
        + name
        + '\''
        + ", clientId='"
        + clientId
        + '\''
        + ", status="
        + status
        + '}';
  }
}
