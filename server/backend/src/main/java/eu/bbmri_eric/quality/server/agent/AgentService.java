package eu.bbmri_eric.quality.server.agent;

import java.util.List;

public interface AgentService {
  AgentRegistration create(AgentRegistrationRequest createAgentDto);

  AgentDTO update(AgentUpdateRequest updateAgentDto, String agentId);

  AgentDTO findById(String id);

  List<AgentDTO> listAll();
}
