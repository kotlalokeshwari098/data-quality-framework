package eu.bbmri_eric.quality.agent.common;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Global handler for translating exceptions into HTTP responses */
@ControllerAdvice
public class GlobalRestExceptionHandler {
  @ExceptionHandler(org.hibernate.StaleStateException.class)
  public ResponseEntity<String> handleStaleState(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found");
  }

  @ExceptionHandler(ConversionFailedException.class)
  public ResponseEntity<String> handleFailedConversion(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
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
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        ex.getMessage());
    problemDetail.setTitle("Bad Request");
    return problemDetail;
  }
}
