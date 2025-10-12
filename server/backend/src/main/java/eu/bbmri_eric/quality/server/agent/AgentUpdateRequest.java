package eu.bbmri_eric.quality.server.agent;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Agent update Request")
public class AgentUpdateRequest {
  private String name;
  private AgentStatus status;

  public AgentUpdateRequest() {
    // For model mapper
  }

  public AgentUpdateRequest(String name, AgentStatus status) {
    this.name = name;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AgentStatus getStatus() {
    return status;
  }

  public void setStatus(AgentStatus status) {
    this.status = status;
  }
}
