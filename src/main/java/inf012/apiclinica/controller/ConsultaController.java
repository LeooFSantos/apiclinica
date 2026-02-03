package inf012.apiclinica.controller;

import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.service.ConsultaService;
import inf012.apiclinica.dto.ConsultaCreateDTO;
import inf012.apiclinica.dto.ConsultaCancelamentoDTO;
import inf012.apiclinica.dto.DisponibilidadeMedicoDTO;
import inf012.apiclinica.security.AuthTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints de consultas (agendamento, cancelamento e disponibilidade).
 */
@RestController
@Tag(name = "Consultas", description = "Agendamento, cancelamento e consulta de disponibilidade")
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService service;
    private final AuthTokenService authTokenService;

    public ConsultaController(ConsultaService service, AuthTokenService authTokenService) {
        this.service = service;
        this.authTokenService = authTokenService;
    }

    @Operation(summary = "Agenda uma nova consulta")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta agendar(@RequestBody @Valid ConsultaCreateDTO dto) {
        return service.agendar(dto);
    }

    @Operation(summary = "Cancela uma consulta informando o motivo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(
            @PathVariable Long id,
            @RequestBody @Valid ConsultaCancelamentoDTO dto
    ) {
        service.cancelar(id, dto);
    }

    @Operation(summary = "Lista consultas do médico autenticado no dia")
    @GetMapping("/me")
    public List<Consulta> minhasConsultas(@RequestParam(required = false) String date, Authentication auth) {
        LocalDate dia = (date == null) ? LocalDate.now() : LocalDate.parse(date);
        return service.listarPorMedicoENoDia(auth.getName(), dia);
    }

    @Operation(summary = "Lista consultas do paciente autenticado")
    @GetMapping
    public Page<Consulta> listarDoPaciente(
            Authentication auth,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.listarPorPaciente(auth.getName(), pageable);
    }

    @Operation(summary = "Cancela todas as consultas do médico autenticado")
    @PostMapping("/medico/cancelar-todas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarTodasDoMedico(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String nomeUsuario = authTokenService.getNomeUsuario(authHeader);
        service.cancelarTodasConsultasDoMedico(nomeUsuario);
    }

    @Operation(summary = "Lista disponibilidade de médicos por especialidade e data")
    @GetMapping("/disponibilidade")
    public List<DisponibilidadeMedicoDTO> disponibilidade(
            @RequestParam String date,
            @RequestParam inf012.apiclinica.model.Especialidade especialidade
    ) {
        LocalDate dia = LocalDate.parse(date);
        return service.listarDisponibilidade(especialidade, dia);
    }
}