package eu.bbmri_eric.quality.agent.dataquality.server;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public final class ServerLinkBuilder {

  public EntityModel<ServerDto> toModel(ServerDto serverDto) {
    return EntityModel.of(serverDto)
        .add(linkTo(ServerController.class).slash(serverDto.getId()).withSelfRel())
        .add(linkTo(ServerController.class).withRel("servers"));
  }

  public EntityModel<DetailedServerDto> toModel(DetailedServerDto detailedServerDto) {
    return EntityModel.of(detailedServerDto)
        .add(linkTo(ServerController.class).slash(detailedServerDto.getId()).withSelfRel())
        .add(linkTo(ServerController.class).withRel("servers"));
  }

  public CollectionModel<EntityModel<ServerDto>> toCollectionModel(List<ServerDto> servers) {
    var entityModels = servers.stream().map(this::toModel).toList();

    return CollectionModel.of(entityModels).add(linkTo(ServerController.class).withSelfRel());
  }
}
