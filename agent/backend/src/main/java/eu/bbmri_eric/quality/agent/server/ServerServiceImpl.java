package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.server.dto.DetailedServerDto;
import eu.bbmri_eric.quality.agent.server.dto.ServerCreateDto;
import eu.bbmri_eric.quality.agent.server.dto.ServerDto;
import eu.bbmri_eric.quality.agent.server.dto.ServerUpdateDto;
import eu.bbmri_eric.quality.agent.settings.SettingsService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.StreamSupport;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class ServerServiceImpl implements ServerService {

  private final ServerRepository serverRepository;
  private final ModelMapper modelMapper;
  private final RestTemplate restTemplate;
  private final SettingsService settingsService;

  public ServerServiceImpl(
      ServerRepository serverRepository,
      ModelMapper modelMapper,
      RestTemplate restTemplate,
      SettingsService settingsService) {
    this.serverRepository = serverRepository;
    this.modelMapper = modelMapper;
    this.restTemplate = restTemplate;
    this.settingsService = settingsService;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ServerDto> findAll() {
    return StreamSupport.stream(serverRepository.findAll().spliterator(), false)
        .map(server -> modelMapper.map(server, ServerDto.class))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public DetailedServerDto findById(String id) {
    Server server =
        serverRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Server not found with id: " + id));
    return modelMapper.map(server, DetailedServerDto.class);
  }

  @Override
  public ServerDto create(ServerCreateDto createDto) {
    Server server = new Server(createDto.getUrl(), createDto.getName());
    Server savedServer = serverRepository.save(server);
    savedServer.register(settingsService.getSettings().getAgentId(), restTemplate);
    return modelMapper.map(savedServer, ServerDto.class);
  }

  @Override
  public ServerDto update(String id, ServerUpdateDto updateDto) {
    Server server =
        serverRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Server not found with id: " + id));

    if (updateDto.getUrl() != null) {
      server.setUrl(updateDto.getUrl());
    }
    if (updateDto.getName() != null) {
      server.setName(updateDto.getName());
    }
    if (updateDto.getClientId() != null) {
      server.setClientId(updateDto.getClientId());
    }
    if (updateDto.getClientSecret() != null) {
      server.setClientSecret(updateDto.getClientSecret());
    }

    Server updatedServer = serverRepository.save(server);
    return modelMapper.map(updatedServer, ServerDto.class);
  }

  @Override
  public void deleteById(String id) {
    if (!serverRepository.existsById(id)) {
      throw new EntityNotFoundException("Server not found with id: " + id);
    }
    serverRepository.deleteById(id);
  }
}
