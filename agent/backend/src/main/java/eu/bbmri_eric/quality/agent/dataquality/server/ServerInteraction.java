package eu.bbmri_eric.quality.agent.dataquality.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a log entry of an interaction with a central server.
 *
 * <p>This entity tracks all operations performed on or with a server, including configuration
 * updates and communication attempts. Each interaction is timestamped and categorized by type.
 */
@Entity
public class ServerInteraction {

  /** Auto-generated unique identifier for the interaction. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Type of interaction performed. */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private InteractionType type;

  /** Detailed description of the interaction. */
  @NotBlank
  @Column(name = "description", nullable = false)
  private String description;

  /** Timestamp when the interaction occurred. */
  @NotNull
  @Column(name = "timestamp")
  private LocalDateTime timestamp = LocalDateTime.now();

  /** The ID of the server this interaction belongs to. */
  @NotNull
  @Column(nullable = false)
  private String serverId;

  /** Default constructor required by JPA. */
  protected ServerInteraction() {}

  /**
   * Constructs a new ServerInteraction with the specified parameters.
   *
   * <p>The timestamp will be automatically set to the current time when the entity is persisted.
   *
   * @param type the type of interaction
   * @param description the detailed description of the interaction
   */
  public ServerInteraction(InteractionType type, String description) {
    this.type = type;
    this.description = description;
  }

  /**
   * Gets the unique identifier of the interaction.
   *
   * @return the interaction ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Gets the type of interaction.
   *
   * @return the interaction type
   */
  public InteractionType getType() {
    return type;
  }

  /**
   * Sets the type of interaction.
   *
   * @param type the interaction type
   */
  public void setType(InteractionType type) {
    this.type = type;
  }

  /**
   * Gets the description of the interaction.
   *
   * @return the interaction description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the interaction.
   *
   * @param description the interaction description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the timestamp when the interaction occurred.
   *
   * @return the interaction timestamp
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp of the interaction.
   *
   * @param timestamp the interaction timestamp
   */
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets the ID of the server this interaction belongs to.
   *
   * @return the server ID
   */
  public String getServerId() {
    return serverId;
  }

  /**
   * Sets the ID of the server this interaction belongs to.
   *
   * @param serverId the server ID
   */
  public void setServerId(String serverId) {
    this.serverId = serverId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServerInteraction that = (ServerInteraction) o;
    return Objects.equals(id, that.id)
        && type == that.type
        && Objects.equals(description, that.description)
        && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(serverId, that.serverId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, description, timestamp, serverId);
  }

  @Override
  public String toString() {
    return "ServerInteraction{"
        + "id="
        + id
        + ", type="
        + type
        + ", description='"
        + description
        + '\''
        + ", timestamp="
        + timestamp
        + '}';
  }
}
