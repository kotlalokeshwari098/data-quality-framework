package eu.bbmri_eric.quality.agent.dataquality.server.client;

public class AgentUpdateRequest {
  private String version;

  public AgentUpdateRequest() {}

  public AgentUpdateRequest(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
