package inf012.apiclinica.controller;

import inf012.apiclinica.model.Medico;
import inf012.apiclinica.service.MedicoService;
import inf012.apiclinica.dto.MedicoCreateDTO;
import inf012.apiclinica.dto.MedicoListDTO;
import inf012.apiclinica.dto.MedicoUpdateDTO;
import inf012.apiclinica.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints de médicos (cadastro, perfil e listagem).
 */
@RestController
@Tag(name = "Médicos", description = "Cadastro e gerenciamento de médicos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService service;
    private final JwtTokenProvider tokenProvider;

    public MedicoController(MedicoService service, JwtTokenProvider tokenProvider) {
        this.service = service;
        this.tokenProvider = tokenProvider;
    }

    @Operation(summary = "Cadastra um novo médico")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medico cadastrar(@RequestBody @Valid MedicoCreateDTO dto) {
        return service.cadastrar(dto);
    }

    @Operation(summary = "Lista médicos ativos")
    @GetMapping
    public Page<MedicoListDTO> listar(
            @PageableDefault(
                size = 10,
                sort = "nome",
                direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return service.listar(pageable);
    }

    @Operation(summary = "Retorna o perfil do médico autenticado")
    @GetMapping("/me")
    public Medico me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String nomeUsuario = getNomeUsuario(authHeader);
        return service.buscarPorNomeUsuario(nomeUsuario);
    }

    @Operation(summary = "Conta médicos ativos")
    @GetMapping("/ativos/count")
    public long contarAtivos() {
        return service.contarAtivos();
    }

    @Operation(summary = "Atualiza dados do médico autenticado")
    @PutMapping("/me")
    public Medico atualizarMe(@RequestBody @Valid MedicoUpdateDTO dto,
                              @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String nomeUsuario = getNomeUsuario(authHeader);
        return service.atualizarConfiguracoes(nomeUsuario, dto);
    }

    @Operation(summary = "Inativa o cadastro do médico autenticado")
    @DeleteMapping("/me")
    public void inativarMe(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String nomeUsuario = getNomeUsuario(authHeader);
        service.inativarPorNomeUsuario(nomeUsuario);
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

    @Operation(summary = "Inativa um médico por ID (admin)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
