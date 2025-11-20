package eu.bbmri_eric.quality.agent.server.impl.client;

public class AgentStatusResponse {
  private String id;
  private AgentStatus status;
  private String name;
  private String version;

  public AgentStatusResponse() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AgentStatus getStatus() {
    return status;
  }

  public void setStatus(AgentStatus status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
