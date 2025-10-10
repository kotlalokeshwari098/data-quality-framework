package eu.bbmri_eric.quality.server.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

  private final AgentService agentService;
  private final AgentLinkBuilder linkBuilder;

  public AgentController(AgentService agentService, AgentLinkBuilder linkBuilder) {
    this.agentService = agentService;
    this.linkBuilder = linkBuilder;
  }

  @PostMapping
  @Schema(name = "Register an agent")
  public ResponseEntity<EntityModel<AgentRegistration>> create(
      @Valid @RequestBody AgentRegistrationRequest createAgentDto) {
    AgentRegistration createdAgentDto = agentService.create(createAgentDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(createdAgentDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<AgentDTO>> findById(@PathVariable String id) {
    AgentDTO agent = agentService.findById(id);
    EntityModel<AgentDTO> agentModel = linkBuilder.toModel(agent);
    return ResponseEntity.ok(agentModel);
  }

  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<AgentDTO>>> listAll() {
    List<AgentDTO> agents = agentService.listAll();
    CollectionModel<EntityModel<AgentDTO>> agentsModel = linkBuilder.toCollectionModel(agents);
    return ResponseEntity.ok(agentsModel);
  }
}
