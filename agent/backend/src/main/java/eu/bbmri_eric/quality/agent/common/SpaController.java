package eu.bbmri_eric.quality.agent.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** Controller for redirecting HTTP requests to the SPA served by spring boot */
@Controller
class SpaController {
  // Handles single-level paths like /dashboard, /login, etc. (excluding /api)
  @RequestMapping(value = "/{path:(?!api)[^.]*}", method = RequestMethod.GET)
  String redirectSingleLevel() {
    return "forward:/index.html";
  }

  // Handles multi-level paths like /quality-checks/123, /agents/uuid/reports, etc. (excluding
  // /api/*)
  @RequestMapping(value = "/{path:(?!api).*}/**", method = RequestMethod.GET)
  String redirectMultiLevel() {
    return "forward:/index.html";
  }
}
