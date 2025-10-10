package eu.bbmri_eric.quality.server.agent;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public final class AgentLinkBuilder {

  public EntityModel<AgentDto> toModel(AgentDto agentDto) {
    return EntityModel.of(agentDto)
        .add(linkTo(methodOn(AgentController.class).findById(agentDto.getId())).withSelfRel())
        .add(linkTo(AgentController.class).withRel("agents"));
  }

  public CollectionModel<EntityModel<AgentDto>> toCollectionModel(List<AgentDto> agents) {
    var entityModels = agents.stream().map(this::toModel).toList();

    return CollectionModel.of(entityModels).add(linkTo(AgentController.class).withSelfRel());
  }
}
