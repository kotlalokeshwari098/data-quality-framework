package eu.bbmri_eric.quality.agent.server.client;

import eu.bbmri_eric.quality.agent.server.ServerConnectionStatus;

public interface CentralServerClient {
  RegistrationCredentials register();

  ServerConnectionStatus checkRegistrationStatus();

  void healthCheck();

  void updateAgentVersion(String version);
}
