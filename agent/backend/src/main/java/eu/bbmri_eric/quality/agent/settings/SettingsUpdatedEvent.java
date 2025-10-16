package eu.bbmri_eric.quality.agent.settings;

import org.springframework.context.ApplicationEvent;

public class SettingsUpdatedEvent extends ApplicationEvent {
  public SettingsUpdatedEvent(Object source) {
    super(source);
  }
}
