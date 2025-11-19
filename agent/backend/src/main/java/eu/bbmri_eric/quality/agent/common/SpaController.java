package eu.bbmri_eric.quality.agent.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Controller for redirecting HTTP requests to the SPA served by spring boot */
@Controller
class SpaController {
  @GetMapping(value = "/{path:(?!api)[^.]*}")
  String redirectSingleLevel() {
    return "forward:/index.html";
  }

  @GetMapping(value = "/{path:(?!api).*}/**")
  String redirectMultiLevel() {
    return "forward:/index.html";
  }
}
