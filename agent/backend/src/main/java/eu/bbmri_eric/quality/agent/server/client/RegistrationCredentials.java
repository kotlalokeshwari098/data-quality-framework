package eu.bbmri_eric.quality.agent.server.client;

import java.util.Objects;

/**
 * Data class representing registration credentials returned from a central server. Contains the
 * clientId and clientSecret needed for authentication.
 */
public class RegistrationCredentials {
  private final String clientId;
  private final String clientSecret;

  public RegistrationCredentials(String clientId, String clientSecret) {
    this.clientId = Objects.requireNonNull(clientId, "ClientId cannot be null");
    this.clientSecret = Objects.requireNonNull(clientSecret, "ClientSecret cannot be null");
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegistrationCredentials that = (RegistrationCredentials) o;
    return Objects.equals(clientId, that.clientId)
        && Objects.equals(clientSecret, that.clientSecret);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientId, clientSecret);
  }

  @Override
  public String toString() {
    return "RegistrationCredentials{" + "clientId='" + clientId + '\'' + '}';
  }
}
