package eu.bbmri_eric.quality.server.dataquality.dto;

import eu.bbmri_eric.quality.server.dataquality.domain.AgentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Agent update Request")
public class AgentUpdateRequest {
  private String name;
  private AgentStatus status;
  private String version;

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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
