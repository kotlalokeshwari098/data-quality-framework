package eu.bbmri_eric.quality.agent.dataquality.server;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a server registration is initiated. Decouples server service from direct
 * communication with central servers.
 */
public class ServerRegistrationEvent extends ApplicationEvent {
  private final String agentId;
  private final String serverUrl;

  public ServerRegistrationEvent(Object source, String agentId, String serverUrl) {
    super(source);
    this.agentId = agentId;
    this.serverUrl = serverUrl;
  }

  public String getAgentId() {
    return agentId;
  }

  public String getServerUrl() {
    return serverUrl;
  }
}
