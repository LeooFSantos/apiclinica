package apiclinica.email.api;

import apiclinica.email.api.dto.EmailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<EmailResponse> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getAllErrors().isEmpty()
        ? "Payload inválido"
        : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

    return ResponseEntity.badRequest().body(new EmailResponse(false, msg));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<EmailResponse> handleGeneric(Exception ex) {
    return ResponseEntity.internalServerError().body(new EmailResponse(false, ex.getMessage()));
  }
}
