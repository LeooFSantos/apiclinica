package inf012.apiclinica.controller;

import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.service.ConsultaService;
import inf012.apiclinica.dto.ConsultaCreateDTO;
import inf012.apiclinica.dto.ConsultaCancelamentoDTO;
import inf012.apiclinica.dto.DisponibilidadeMedicoDTO;
import inf012.apiclinica.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService service;
    private final JwtTokenProvider tokenProvider;

    public ConsultaController(ConsultaService service, JwtTokenProvider tokenProvider) {
        this.service = service;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta agendar(@RequestBody @Valid ConsultaCreateDTO dto) {
        return service.agendar(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(
            @PathVariable Long id,
            @RequestBody @Valid ConsultaCancelamentoDTO dto
    ) {
        service.cancelar(id, dto);
    }

    @GetMapping("/me")
    public List<Consulta> minhasConsultas(@RequestParam(required = false) String date, Authentication auth) {
        LocalDate dia = (date == null) ? LocalDate.now() : LocalDate.parse(date);
        return service.listarPorMedicoENoDia(auth.getName(), dia);
    }

    @GetMapping
    public Page<Consulta> listarDoPaciente(
            Authentication auth,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.listarPorPaciente(auth.getName(), pageable);
    }

    @PostMapping("/medico/cancelar-todas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarTodasDoMedico(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String nomeUsuario = getNomeUsuario(authHeader);
        service.cancelarTodasConsultasDoMedico(nomeUsuario);
    }

    private String getNomeUsuario(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
        }
        String token = authHeader.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        String nomeUsuario = tokenProvider.getUsernameFromToken(token);
        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        return nomeUsuario;
    }

    @GetMapping("/disponibilidade")
    public List<DisponibilidadeMedicoDTO> disponibilidade(
            @RequestParam String date,
            @RequestParam inf012.apiclinica.model.Especialidade especialidade
    ) {
        LocalDate dia = LocalDate.parse(date);
        return service.listarDisponibilidade(especialidade, dia);
    }
}