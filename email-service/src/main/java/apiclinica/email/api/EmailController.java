package apiclinica.email.api;

import apiclinica.email.api.dto.EmailRequest;
import apiclinica.email.api.dto.EmailResponse;
import apiclinica.email.service.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

  private final EmailSenderService emailSenderService;

  public EmailController(EmailSenderService emailSenderService) {
    this.emailSenderService = emailSenderService;
  }

  @Operation(
      summary = "Enviar e-mail simples",
      responses = {
          @ApiResponse(responseCode = "200", description = "E-mail enviado",
              content = @Content(schema = @Schema(implementation = EmailResponse.class))),
          @ApiResponse(responseCode = "400", description = "Payload inválido")
      }
  )
  @PostMapping("/send")
  public ResponseEntity<EmailResponse> send(@Valid @RequestBody EmailRequest request) {
    emailSenderService.send(request);
    return ResponseEntity.ok(new EmailResponse(true, "E-mail enviado com sucesso"));
  }
}
