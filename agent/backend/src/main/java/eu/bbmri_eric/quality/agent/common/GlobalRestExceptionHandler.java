package eu.bbmri_eric.quality.agent.common;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Global handler for translating exceptions into HTTP responses */
@ControllerAdvice
public class GlobalRestExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

  @ExceptionHandler(org.hibernate.StaleStateException.class)
  @ApiResponse(
      responseCode = "404",
      description = "Entity Not Found",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleStaleState(org.hibernate.StaleStateException ex) {
    logger.debug("Stale state exception: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Entity not found");
    problemDetail.setTitle("Entity Not Found");
    return problemDetail;
  }

  @ExceptionHandler(ConversionFailedException.class)
  @ApiResponse(
      responseCode = "400",
      description = "Bad Request",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleFailedConversion(ConversionFailedException ex) {
    logger.debug("Conversion failed: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Bad Request");
    return problemDetail;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ApiResponse(
      responseCode = "400",
      description = "Bad Request",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    logger.debug("Illegal argument: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Bad Request");
    return problemDetail;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ApiResponse(
      responseCode = "404",
      description = "User Not Found",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleUserNotFound(UsernameNotFoundException ex) {
    logger.debug("User not found: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("User Not Found");
    return problemDetail;
  }

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  @ApiResponse(
      responseCode = "401",
      description = "Authentication Required",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleAuthenticationRequired(AuthenticationCredentialsNotFoundException ex) {
    logger.debug("Authentication required: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setTitle("Authentication Required");
    return problemDetail;
  }

  @ExceptionHandler(IllegalStateException.class)
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleIllegalState(IllegalStateException ex) {
    logger.debug("Illegal state: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    problemDetail.setTitle("Forbidden");
    return problemDetail;
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ApiResponse(
      responseCode = "403",
      description = "Access Denied",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
    logger.debug("Access denied: {}", ex.getMessage());
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    problemDetail.setTitle("Access Denied");
    return problemDetail;
  }
}
