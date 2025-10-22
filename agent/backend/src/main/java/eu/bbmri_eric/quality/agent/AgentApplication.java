package eu.bbmri_eric.quality.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Main Application class */
@SpringBootApplication
@EnableScheduling
public class AgentApplication {

  public static void main(String[] args) {
    SpringApplication.run(AgentApplication.class, args);
  }
}
