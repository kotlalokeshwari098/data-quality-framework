package eu.bbmri_eric.quality.agent.settings.event;

import eu.bbmri_eric.quality.agent.settings.dto.SettingsDTO;
import org.springframework.context.ApplicationEvent;

public class SettingsUpdatedEvent extends ApplicationEvent {
  private final SettingsDTO updatedSettings;

  public SettingsUpdatedEvent(Object source, SettingsDTO settings) {
    super(source);
    this.updatedSettings = settings;
  }

  public SettingsDTO getSettings() {
    return updatedSettings;
  }
}
