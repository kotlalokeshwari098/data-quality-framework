package eu.bbmri_eric.quality.server.setting;

public interface SettingService {
  SettingDTO getSettings();

  SettingDTO updateSettings(SettingDTO settingDTO);
}
