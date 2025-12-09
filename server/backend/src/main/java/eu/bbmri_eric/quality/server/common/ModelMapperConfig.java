package eu.bbmri_eric.quality.server.common;

import eu.bbmri_eric.quality.server.dataquality.domain.Agent;
import eu.bbmri_eric.quality.server.dataquality.domain.QualityCheckResult;
import eu.bbmri_eric.quality.server.dataquality.dto.AgentDTO;
import eu.bbmri_eric.quality.server.dataquality.dto.QualityCheckResultDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper bean. Provides the ModelMapper instance required by the
 * application.
 */
@Configuration
class ModelMapperConfig {

  /**
   * Creates a ModelMapper bean for object mapping.
   *
   * @return configured ModelMapper instance
   */
  @Bean
  ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();

    // Configure mapping from Agent to AgentDTO to skip the interactions field
    // This prevents lazy loading of interactions by default
    modelMapper.addMappings(
        new PropertyMap<Agent, AgentDTO>() {
          @Override
          protected void configure() {
            skip(destination.getInteractions());
          }
        });

    // Configure mapping from QualityCheckResult to QualityCheckResultDTO
    // Map the hash from the nested qualityCheck object
    modelMapper.addMappings(
        new PropertyMap<QualityCheckResult, QualityCheckResultDTO>() {
          @Override
          protected void configure() {
            map(source.getQualityCheck().getHash(), destination.getHash());
            map(source.getResult(), destination.getResult());
          }
        });

    return modelMapper;
  }
}
