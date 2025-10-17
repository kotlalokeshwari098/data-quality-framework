package eu.bbmri_eric.quality.agent.system;

import eu.bbmri_eric.quality.agent.fhir.Blaze;
import eu.bbmri_eric.quality.agent.settings.Settings;
import eu.bbmri_eric.quality.agent.settings.SettingsDTO;
import eu.bbmri_eric.quality.agent.settings.SettingsRepository;
import eu.bbmri_eric.quality.agent.settings.SettingsService;
import eu.bbmri_eric.quality.agent.settings.SettingsUpdatedEvent;
import java.util.Base64;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class BlazeTestConfig {

  @Bean
  @Primary
  public Blaze blaze(
      SettingsService settingsService,
      SettingsRepository settingsRepository,
      RestTemplateBuilder restTemplateBuilder) {
    int mappedPort = QualityCheckSystemTest.blazeContainer.getMappedPort(8080);
    String blazeUrl =
        "http://" + QualityCheckSystemTest.blazeContainer.getHost() + ":" + mappedPort + "/fhir";

    settingsRepository.save(new Settings("fhirUrl", blazeUrl));
    settingsRepository.save(new Settings("fhirUsername", "testuser"));
    settingsRepository.save(
        new Settings("fhirPassword", Base64.getEncoder().encodeToString("testpass".getBytes())));

    Blaze blaze = new BlazeTestWrapper(restTemplateBuilder, mappedPort);

    SettingsDTO settings = settingsService.getSettings();
    blaze.onSettingsUpdated(new SettingsUpdatedEvent(this, settings));

    return blaze;
  }
}
