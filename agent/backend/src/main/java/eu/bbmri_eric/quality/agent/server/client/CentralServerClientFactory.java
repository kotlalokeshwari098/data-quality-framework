package eu.bbmri_eric.quality.agent.server.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Factory for creating CentralServerClient instances with specific server configurations.
 *
 * <p>Encapsulates the creation of CentralServerClient instances, allowing clients to request a
 * client configured for a specific server without managing dependencies directly.
 */
@Component
public class CentralServerClientFactory {

  private final RestTemplate restTemplate;

  public CentralServerClientFactory(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Creates a CentralServerClient configured for the given server.
   *
   * @param agentId the agent identifier
   * @param serverUrl the base URL of the central server
   * @param clientId the client ID for authentication
   * @param clientSecret the client secret for authentication
   * @return a configured CentralServerClient instance
   */
  public CentralServerClient createClient(
      String agentId, String serverUrl, String clientId, String clientSecret) {
    return new CentralServerClientImpl(restTemplate, agentId, serverUrl, clientId, clientSecret);
  }
}
