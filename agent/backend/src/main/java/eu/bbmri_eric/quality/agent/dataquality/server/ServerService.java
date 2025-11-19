package eu.bbmri_eric.quality.agent.dataquality.server;

import java.util.List;

public interface ServerService {

  List<ServerDto> findAll();

  DetailedServerDto findById(String id);

  ServerDto create(ServerCreateDto createDto);

  ServerDto update(String id, ServerUpdateDto updateDto);

  void deleteById(String id);
}
