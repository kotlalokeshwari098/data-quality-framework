package eu.bbmri_eric.quality.server.agent;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public final class AgentLinkBuilder {

  public EntityModel<AgentDTO> toModel(AgentDTO agentDto) {
    return EntityModel.of(agentDto)
        .add(linkTo(methodOn(AgentController.class).findById(agentDto.getId())).withSelfRel())
        .add(linkTo(AgentController.class).withRel("agents"));
  }

  public CollectionModel<EntityModel<AgentDTO>> toCollectionModel(List<AgentDTO> agents) {
    var entityModels = agents.stream().map(this::toModel).toList();

    return CollectionModel.of(entityModels).add(linkTo(AgentController.class).withSelfRel());
  }
}
