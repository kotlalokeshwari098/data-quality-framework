package eu.bbmri_eric.quality.server.agent;

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
  public ResponseEntity<EntityModel<AgentDto>> create(
      @Valid @RequestBody CreateAgentDto createAgentDto) {
    AgentDto createdAgent = agentService.create(createAgentDto);
    EntityModel<AgentDto> agentModel = linkBuilder.toModel(createdAgent);
    return ResponseEntity.status(HttpStatus.CREATED).body(agentModel);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<AgentDto>> findById(@PathVariable String id) {
    AgentDto agent = agentService.findById(id);
    EntityModel<AgentDto> agentModel = linkBuilder.toModel(agent);
    return ResponseEntity.ok(agentModel);
  }

  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<AgentDto>>> listAll() {
    List<AgentDto> agents = agentService.listAll();
    CollectionModel<EntityModel<AgentDto>> agentsModel = linkBuilder.toCollectionModel(agents);
    return ResponseEntity.ok(agentsModel);
  }
}
