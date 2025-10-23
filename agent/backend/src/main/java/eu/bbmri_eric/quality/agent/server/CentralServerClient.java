package eu.bbmri_eric.quality.agent.server;

interface CentralServerClient {
  RegistrationCredentials register(String agentId, String serverUrl);

  ServerConnectionStatus checkRegistrationStatus(
      String agentId, String serverUrl, String clientId, String clientSecret);

  void healthCheck(String agentId, String serverUrl);
}
