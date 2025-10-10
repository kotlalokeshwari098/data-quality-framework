package eu.bbmri_eric.quality.server.agent;

import eu.bbmri_eric.quality.server.user.UserDTO;
import java.util.Objects;

public class AgentRegistration {
  private AgentDto agent;
  private UserDTO user;

  public AgentRegistration(AgentDto agent, UserDTO user) {
    this.agent = agent;
    this.user = user;
  }

  public AgentDto getAgent() {
    return agent;
  }

  public void setAgent(AgentDto agent) {
    this.agent = agent;
  }

  public UserDTO getUser() {
    return user;
  }

  public void setUser(UserDTO user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AgentRegistration that = (AgentRegistration) o;
    return Objects.equals(agent, that.agent) && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agent, user);
  }
}
