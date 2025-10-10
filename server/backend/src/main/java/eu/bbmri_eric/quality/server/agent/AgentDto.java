package eu.bbmri_eric.quality.server.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

@Schema(description = "Agent data transfer object")
@Relation(itemRelation = "agent", collectionRelation = "agents")
public class AgentDto {

  @Schema(
      description = "Unique identifier of the agent",
      example = "550e8400-e29b-41d4-a716-446655440000")
  private String id;

  public AgentDto() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
