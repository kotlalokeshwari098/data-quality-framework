package eu.bbmri_eric.quality.server.agent;

import eu.bbmri_eric.quality.server.common.EntityAlreadyExistsException;
import eu.bbmri_eric.quality.server.common.EntityNotFoundException;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AgentServiceImpl implements AgentService {

  private final AgentRepository agentRepository;
  private final ModelMapper modelMapper;

  public AgentServiceImpl(AgentRepository agentRepository, ModelMapper modelMapper) {
    this.agentRepository = agentRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public AgentDto create(CreateAgentDto createAgentDto) {
    if (agentRepository.existsById(createAgentDto.id())) {
      throw new EntityAlreadyExistsException(
          "Agent %s already exists".formatted(createAgentDto.id()));
    }
    Agent agent = new Agent(createAgentDto.id());
    Agent savedAgent = agentRepository.save(agent);
    return modelMapper.map(savedAgent, AgentDto.class);
  }

  @Override
  @Transactional(readOnly = true)
  public AgentDto findById(String id) {
    return modelMapper.map(
        agentRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Agent with ID %s not found".formatted(id))),
        AgentDto.class);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AgentDto> listAll() {
    return agentRepository.findAll().stream()
        .map(agent -> modelMapper.map(agent, AgentDto.class))
        .toList();
  }
}
