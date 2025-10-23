package eu.bbmri_eric.quality.agent.server;

interface CentralServerClient {
  void register(String agentId, String serverUrl);

  ServerConnectionStatus checkRegistrationStatus(String agentId, String serverUrl);

  void healthCheck(String agentId, String serverUrl);
}
