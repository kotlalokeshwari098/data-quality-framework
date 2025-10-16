package eu.bbmri_eric.quality.agent.settings;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class SettingsService {

    private final ObjectMapper objectMapper;

    private final SettingsRepository settingsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SettingsService(SettingsRepository settingsRepository, ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.settingsRepository = settingsRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    public SettingsDTO getSettings() {
        Map<String , String> values = loadSettingsMap();
        return objectMapper.convertValue(values, SettingsDTO.class);
    }

    public SettingsDTO updateSettings(SettingsDTO dto) {
        Map<String, Object> dtoMap = objectMapper.convertValue(dto, Map.class);
        dtoMap.forEach((name, value) -> updateSetting(name, value != null ? value.toString() : null));
        eventPublisher.publishEvent(new SettingsUpdatedEvent(this));
        return dto;
    }

    private void updateSetting(String name, String value) {
        Settings setting = settingsRepository.findById(name)
            .orElseThrow(() -> new IllegalStateException("Setting not found: " + name));
        setting.setValue(value);
        settingsRepository.save(setting);
    }

    private Map<String, String> loadSettingsMap() {
        return StreamSupport.stream(settingsRepository.findAll().spliterator(), false)
            .collect(Collectors.toMap(Settings::getName, Settings::getValue));
    }

}
