package eu.bbmri_eric.quality.server.agent;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Agent {
  @Id private String id;
  private AgentStatus status = AgentStatus.PENDING;

  public Agent(String id) {
    this.id = id;
  }

  protected Agent() {}

  public String getId() {
    return id;
  }

  public AgentStatus getStatus() {
    return status;
  }

  public void setStatus(AgentStatus status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Agent agent = (Agent) o;
    return Objects.equals(id, agent.id) && status == agent.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status);
  }
}
