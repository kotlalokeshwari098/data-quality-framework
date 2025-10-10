package eu.bbmri_eric.quality.server.agent;

import java.util.List;

public interface AgentService {
  AgentDto create(CreateAgentDto createAgentDto);

  AgentDto findById(String id);

  List<AgentDto> listAll();
}
