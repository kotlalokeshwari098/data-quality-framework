package eu.bbmri_eric.quality.server.agent;

import eu.bbmri_eric.quality.server.common.EntityAlreadyExistsException;
import eu.bbmri_eric.quality.server.common.EntityNotFoundException;
import eu.bbmri_eric.quality.server.user.UserCreateDTO;
import eu.bbmri_eric.quality.server.user.UserDTO;
import eu.bbmri_eric.quality.server.user.UserService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AgentServiceImpl implements AgentService {

  private final AgentRepository agentRepository;
  private final ModelMapper modelMapper;
  private final UserService userService;

  public AgentServiceImpl(
      AgentRepository agentRepository, ModelMapper modelMapper, UserService userService) {
    this.agentRepository = agentRepository;
    this.modelMapper = modelMapper;
    this.userService = userService;
  }

  @Override
  public AgentRegistration create(AgentRegistrationRequest createAgentDto) {
    if (agentRepository.existsById(createAgentDto.id())) {
      throw new EntityAlreadyExistsException(
          "Agent %s already exists".formatted(createAgentDto.id()));
    }
    Agent agent = new Agent(createAgentDto.id());
    Agent savedAgent = agentRepository.save(agent);
    UserDTO agentUser =
        userService.createUser(
            new UserCreateDTO("Agent %s".formatted(savedAgent.getId()), savedAgent.getId()));
    return new AgentRegistration(modelMapper.map(savedAgent, AgentDTO.class), agentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public AgentDTO findById(String id) {
    return modelMapper.map(
        agentRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Agent with ID %s not found".formatted(id))),
        AgentDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AgentDTO> listAll() {
    return agentRepository.findAll().stream()
        .map(agent -> modelMapper.map(agent, AgentDTO.class))
        .toList();
  }
}
