package eu.bbmri_eric.quality.server.agent;

import java.util.List;

public interface AgentService {
  AgentRegistration create(AgentRegistrationRequest createAgentDto);

  AgentDto findById(String id);

  List<AgentDto> listAll();
}
