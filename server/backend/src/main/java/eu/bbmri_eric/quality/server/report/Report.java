package eu.bbmri_eric.quality.server.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a data quality report.
 *
 * <p>Each report is automatically assigned a unique identifier and timestamp upon creation. Reports
 * are associated with an agent that generated them.
 */
@Entity
public class Report {
  @Id private final String id = UUID.randomUUID().toString();

  private final LocalDateTime timestamp = LocalDateTime.now();

  private String agentId;

  /** Default constructor for JPA. */
  protected Report() {}

  /**
   * Creates a new report for the specified agent.
   *
   * @param agentId the unique identifier of the agent generating this report
   * @throws IllegalArgumentException if agentId is null or empty
   */
  public Report(String agentId) {
    if (agentId == null || agentId.trim().isEmpty()) {
      throw new IllegalArgumentException("Agent ID cannot be null or empty");
    }
    this.agentId = agentId;
  }

  /**
   * Gets the unique identifier of this report.
   *
   * @return the report ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the timestamp when this report was created.
   *
   * @return the creation timestamp
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Gets the identifier of the agent that generated this report.
   *
   * @return the agent ID
   */
  public String getAgentId() {
    return agentId;
  }

  /**
   * Sets the identifier of the agent that generated this report.
   *
   * @param agentId the agent ID to set
   * @throws IllegalArgumentException if agentId is null or empty
   */
  public void setAgentId(String agentId) {
    if (agentId == null || agentId.trim().isEmpty()) {
      throw new IllegalArgumentException("Agent ID cannot be null or empty");
    }
    this.agentId = agentId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Report report = (Report) o;
    return Objects.equals(id, report.id)
        && Objects.equals(timestamp, report.timestamp)
        && Objects.equals(agentId, report.agentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, timestamp, agentId);
  }
}
