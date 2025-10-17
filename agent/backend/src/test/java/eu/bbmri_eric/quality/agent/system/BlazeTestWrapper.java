package eu.bbmri_eric.quality.agent.system;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.bbmri_eric.quality.agent.fhir.Blaze;
import eu.bbmri_eric.quality.agent.settings.SettingsUpdatedEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class BlazeTestWrapper extends Blaze {
  private final int mappedPort;

  public BlazeTestWrapper(RestTemplateBuilder restTemplateBuilder, int mappedPort) {
    super(restTemplateBuilder);
    this.mappedPort = mappedPort;
  }

  @Override
  public void onSettingsUpdated(SettingsUpdatedEvent event) {
    super.onSettingsUpdated(event);

    IGenericClient client = getClient();
    if (client != null) {
      client.registerInterceptor(new PagingUrlInterceptor(mappedPort));
    }
  }
}
