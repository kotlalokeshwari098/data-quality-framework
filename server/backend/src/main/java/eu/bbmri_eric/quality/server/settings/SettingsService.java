package eu.bbmri_eric.quality.server.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsService {

  private final ObjectMapper objectMapper;
  private final SettingsRepository settingsRepository;

  public SettingsService(
      SettingsRepository settingsRepository,
      ObjectMapper objectMapper) {
    this.settingsRepository = settingsRepository;
    this.objectMapper = objectMapper;
  }

  public SettingsDTO getSettings() {
    Map<String, String> values = loadSettingsMap();
    return objectMapper.convertValue(values, SettingsDTO.class);
  }

  public SettingsDTO updateSettings(SettingsDTO dto) {
    Map<String, Object> dtoMap = objectMapper.convertValue(dto, Map.class);
    dtoMap.forEach((name, value) -> updateSetting(name, value != null ? value.toString() : null));
    return dto;
  }

  private void updateSetting(String name, String value) {
    if (value == null) {
      return;
    }
    Settings setting =
        settingsRepository
            .findById(name)
            .orElseThrow(() -> new IllegalStateException("Setting not found: " + name));
    setting.setValue(value);
    settingsRepository.save(setting);
  }

  private Map<String, String> loadSettingsMap() {
    return StreamSupport.stream(settingsRepository.findAll().spliterator(), false)
        .collect(Collectors.toMap(Settings::getName, Settings::getValue));
  }
}
