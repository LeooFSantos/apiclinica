package apiclinica.email.service;

import apiclinica.email.api.dto.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

  private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username:no-reply@apiclinica.local}")
  private String from;

  public EmailSenderService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void send(EmailRequest req) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(req.getTo());
    message.setSubject(req.getSubject());
    message.setText(req.getBody());

    log.info("Enviando e-mail para {}", req.getTo());
    mailSender.send(message);
  }
}
