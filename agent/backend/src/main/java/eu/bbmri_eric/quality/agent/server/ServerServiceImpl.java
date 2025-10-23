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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServerServiceImpl implements ServerService {

  private final ServerRepository serverRepository;
  private final ModelMapper modelMapper;
  private final SettingsService settingsService;
  private final CentralServerClient centralServerClient;

  public ServerServiceImpl(
      ServerRepository serverRepository,
      ModelMapper modelMapper,
      SettingsService settingsService,
      CentralServerClient centralServerClient) {
    this.serverRepository = serverRepository;
    this.modelMapper = modelMapper;
    this.settingsService = settingsService;
    this.centralServerClient = centralServerClient;
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
    String agentId = settingsService.getSettings().getAgentId();
    centralServerClient.register(agentId, savedServer.getUrl());
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

  @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
  public void checkAllServerStatuses() {
    String agentId = settingsService.getSettings().getAgentId();
    serverRepository
        .findAll()
        .forEach(
            server -> {
              ServerConnectionStatus newStatus =
                  centralServerClient.checkRegistrationStatus(agentId, server.getUrl());
              if (newStatus != server.getStatus()) {
                server.setStatus(newStatus);
                serverRepository.save(server);
              }
            });
  }
}
