package inf012.apiclinica.service;

import inf012.apiclinica.integration.email.EmailRequest;
import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.model.Medico;
import inf012.apiclinica.model.MotivoCancelamento;
import inf012.apiclinica.model.Paciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    @Value("${email.service.url:http://localhost:8082}")
    private String emailServiceUrl;

    public EmailNotificationService(RestTemplate restTemplate, ObjectProvider<DiscoveryClient> discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient.getIfAvailable();
    }

    public void enviarBoasVindasPaciente(Paciente paciente) {
        String subject = "Bem-vindo(a) à Clínica";
        String body = "Olá, " + paciente.getNome() + ".\n\n" +
                "Seu cadastro foi realizado com sucesso na Clínica.\n" +
                "Estamos à disposição para cuidar da sua saúde.";
        enviarEmail(paciente.getEmail(), subject, body);
    }

    public void enviarRespostaSolicitacaoMedico(MedicoRequestView req, boolean aprovado) {
        String subject = "Resposta da solicitação de cadastro";
        String status = aprovado ? "APROVADO" : "REJEITADO";
        String body = "Olá, " + req.getNome() + ".\n\n" +
                "Sua solicitação de cadastro como médico foi " + status + ".";
        enviarEmail(req.getEmail(), subject, body);
    }

    public void enviarConsultaAgendada(Consulta consulta) {
        Paciente paciente = consulta.getPaciente();
        Medico medico = consulta.getMedico();
        String dataHora = consulta.getDataHora().format(DATA_HORA);

        String subjectPaciente = "Confirmação de consulta";
        String bodyPaciente = "Olá, " + paciente.getNome() + ".\n\n" +
                "Sua consulta foi marcada para " + dataHora + ".\n" +
                "Médico: Dr(a). " + medico.getNome() + ".";
        enviarEmail(paciente.getEmail(), subjectPaciente, bodyPaciente);

        String subjectMedico = "Nova consulta agendada";
        String bodyMedico = "Olá, Dr(a). " + medico.getNome() + ".\n\n" +
                "Uma consulta foi agendada para " + dataHora + ".\n" +
                "Paciente: " + paciente.getNome() + ".";
        enviarEmail(medico.getEmail(), subjectMedico, bodyMedico);
    }

    public void enviarConsultaCancelada(Consulta consulta) {
        Paciente paciente = consulta.getPaciente();
        Medico medico = consulta.getMedico();
        String dataHora = consulta.getDataHora().format(DATA_HORA);
        String motivo = formatarMotivo(consulta.getMotivoCancelamento());

        String subject = "Consulta cancelada";
        String bodyPaciente = "Olá, " + paciente.getNome() + ".\n\n" +
                "Sua consulta marcada para " + dataHora + " foi cancelada.\n" +
                "Motivo: " + motivo + ".";
        enviarEmail(paciente.getEmail(), subject, bodyPaciente);

        String bodyMedico = "Olá, Dr(a). " + medico.getNome() + ".\n\n" +
                "A consulta marcada para " + dataHora + " foi cancelada.\n" +
                "Motivo: " + motivo + ".";
        enviarEmail(medico.getEmail(), subject, bodyMedico);
    }

    private void enviarEmail(String to, String subject, String body) {
        try {
            String baseUrl = resolveEmailServiceUrl();
            EmailRequest request = new EmailRequest(to, subject, body);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/api/emails/send",
                    request,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Falha ao enviar e-mail para {}. Status: {}", to, response.getStatusCode());
            }
        } catch (Exception ex) {
            log.warn("Erro ao enviar e-mail para {}: {}", to, ex.getMessage());
        }
    }

    private String resolveEmailServiceUrl() {
        if (discoveryClient == null) {
            return emailServiceUrl;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("email-service");
        if (instances != null && !instances.isEmpty()) {
            return instances.get(0).getUri().toString();
        }
        return emailServiceUrl;
    }

    private String formatarMotivo(MotivoCancelamento motivo) {
        if (motivo == null) return "não informado";
        return switch (motivo) {
            case PACIENTE_DESISTIU -> "Paciente desistiu";
            case MEDICO_CANCELOU -> "Médico cancelou";
            case PROBLEMA_AGENDA -> "Problema de agenda";
            case EMERGENCIA -> "Emergência";
            case MEDICO_INATIVO -> "Médico inativo";
            case OUTROS -> "Outros";
        };
    }

    public static class MedicoRequestView {
        private final String nome;
        private final String email;

        public MedicoRequestView(String nome, String email) {
            this.nome = nome;
            this.email = email;
        }

        public String getNome() { return nome; }
        public String getEmail() { return email; }
    }
}
