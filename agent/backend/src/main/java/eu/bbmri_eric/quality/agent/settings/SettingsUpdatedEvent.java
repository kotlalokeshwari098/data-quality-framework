package eu.bbmri_eric.quality.agent.settings;

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
