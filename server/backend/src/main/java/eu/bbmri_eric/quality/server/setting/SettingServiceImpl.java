package eu.bbmri_eric.quality.server.setting;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingServiceImpl implements SettingService {

  private final ModelMapper modelMapper;
  private final SettingRepository settingRepository;

  public SettingServiceImpl(SettingRepository settingRepository, ModelMapper modelMapper) {
    this.settingRepository = settingRepository;
    this.modelMapper = modelMapper;
  }

  public SettingDTO getSettings() {
    Map<String, String> values = loadSettingsMap();
    return modelMapper.map(values, SettingDTO.class);
  }

  public SettingDTO updateSettings(SettingDTO dto) {
    Map<String, Object> dtoMap = modelMapper.map(dto, Map.class);
    dtoMap.forEach((name, value) -> updateSetting(name, value != null ? value.toString() : null));
    return dto;
  }

  private void updateSetting(String name, String value) {
    if (value == null) {
      return;
    }
    Setting setting =
        settingRepository
            .findById(name)
            .orElseThrow(() -> new IllegalStateException("Setting not found: " + name));
    setting.setValue(value);
    settingRepository.save(setting);
  }

  private Map<String, String> loadSettingsMap() {
    return StreamSupport.stream(settingRepository.findAll().spliterator(), false)
        .collect(Collectors.toMap(Setting::getName, Setting::getValue));
  }
}
