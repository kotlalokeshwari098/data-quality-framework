package eu.bbmri_eric.quality.server.agent;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

@Schema(description = "Agent data transfer object")
@Relation(itemRelation = "agent", collectionRelation = "agents")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentDTO {

  @Schema(
      description = "Unique identifier of the agent",
      example = "550e8400-e29b-41d4-a716-446655440000")
  private String id;

  private AgentStatus status;

  private String name;

  public AgentDTO() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AgentStatus getStatus() {
    return status;
  }

  public void setStatus(AgentStatus status) {
    this.status = status;
  }
}
