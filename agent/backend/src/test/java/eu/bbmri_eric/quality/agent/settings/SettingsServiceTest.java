package eu.bbmri_eric.quality.agent.settings;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SettingsServiceTest {

  @Autowired private SettingsService settingsService;

  @Autowired private SettingsRepository settingsRepository;

  @BeforeEach
  void setUp() {
    settingsRepository.deleteAll();
    settingsRepository.save(new Settings("fhirUrl", "http://localhost:8080/fhir"));
    settingsRepository.save(new Settings("fhirUsername", "testuser"));
    settingsRepository.save(new Settings("fhirPassword", "dGVzdHBhc3M="));
  }

  @Test
  void getSettings_shouldReturnSettingsDTO() {
    SettingsDTO result = settingsService.getSettings();

    assertNotNull(result);
    assertEquals("http://localhost:8080/fhir", result.getFhirUrl());
    assertEquals("testuser", result.getFhirUsername());
    assertEquals("dGVzdHBhc3M=", result.getFhirPassword());
  }

  @Test
  void getSettings_shouldReturnPasswordInBase64Format() {
    SettingsDTO result = settingsService.getSettings();

    assertNotNull(result.getFhirPassword());
    assertEquals("dGVzdHBhc3M=", result.getFhirPassword());

    assertDoesNotThrow(() -> Base64.getDecoder().decode(result.getFhirPassword()));
  }

  @Test
  void updateSettings_shouldStorePasswordAsProvided() {
    String base64Password = Base64.getEncoder().encodeToString("newpassword".getBytes());
    SettingsDTO dto = new SettingsDTO("http://new-server:8080/fhir", "newuser", base64Password);

    SettingsDTO result = settingsService.updateSettings(dto);

    assertNotNull(result);
    assertEquals("http://new-server:8080/fhir", result.getFhirUrl());
    assertEquals("newuser", result.getFhirUsername());

    Settings passwordSetting = settingsRepository.findById("fhirPassword").orElseThrow();
    String storedPassword = passwordSetting.getValue();

    assertEquals(base64Password, storedPassword);

    String decodedPassword = new String(Base64.getDecoder().decode(storedPassword));
    assertEquals("newpassword", decodedPassword);
  }

  @Test
  void updateSettings_shouldStoreAllFieldsCorrectly() {
    String base64Password = Base64.getEncoder().encodeToString("adminpass".getBytes());
    SettingsDTO dto = new SettingsDTO("http://updated:9090/fhir", "admin", base64Password);

    settingsService.updateSettings(dto);

    assertEquals(
        "http://updated:9090/fhir",
        settingsRepository.findById("fhirUrl").orElseThrow().getValue());
    assertEquals("admin", settingsRepository.findById("fhirUsername").orElseThrow().getValue());

    String storedPassword = settingsRepository.findById("fhirPassword").orElseThrow().getValue();
    assertEquals(base64Password, storedPassword);

    String decodedPassword = new String(Base64.getDecoder().decode(storedPassword));
    assertEquals("adminpass", decodedPassword);
  }

  @Test
  void updateSettings_shouldPublishEvent() {
    String base64Password = Base64.getEncoder().encodeToString("eventpass".getBytes());
    SettingsDTO dto = new SettingsDTO("http://localhost:8080/fhir", "eventuser", base64Password);

    assertDoesNotThrow(() -> settingsService.updateSettings(dto));
  }

  @Test
  void updateSettings_shouldThrowException_whenSettingNotFound() {
    settingsRepository.deleteById("fhirUrl");

    String base64Password = Base64.getEncoder().encodeToString("password".getBytes());
    SettingsDTO dto = new SettingsDTO("http://localhost:8080/fhir", "user", base64Password);

    assertThrows(IllegalStateException.class, () -> settingsService.updateSettings(dto));
  }

  @Test
  void getSettings_afterUpdate_shouldReturnUpdatedValues() {
    String newUrl = "http://production:8080/fhir";
    String newUsername = "produser";
    String newPassword = "prodpass123";
    String base64Password = Base64.getEncoder().encodeToString(newPassword.getBytes());

    SettingsDTO updateDto = new SettingsDTO(newUrl, newUsername, base64Password);
    settingsService.updateSettings(updateDto);

    SettingsDTO result = settingsService.getSettings();

    assertEquals(newUrl, result.getFhirUrl());
    assertEquals(newUsername, result.getFhirUsername());

    assertEquals(base64Password, result.getFhirPassword());

    String decodedPassword = new String(Base64.getDecoder().decode(result.getFhirPassword()));
    assertEquals(newPassword, decodedPassword);
  }
}
