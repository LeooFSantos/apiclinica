package inf012.apiclinica.controller;

import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.service.PacienteService;
import inf012.apiclinica.dto.PacienteCreateDTO;
import inf012.apiclinica.dto.PacienteListDTO;
import inf012.apiclinica.dto.PacienteUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente cadastrar(@RequestBody @Valid PacienteCreateDTO dto) {
        return service.cadastrar(dto);
    }

    @GetMapping
    public Page<PacienteListDTO> listar(@PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/me")
    public Paciente me(Authentication auth) {
        if (auth == null) {
            throw new RuntimeException("Não autenticado");
        }
        return service.buscarPorNomeUsuario(auth.getName());
    }

    @PutMapping("/me")
    public Paciente atualizarMe(@RequestBody @Valid PacienteUpdateDTO dto, Authentication auth) {
        if (auth == null) {
            throw new RuntimeException("Não autenticado");
        }
        return service.atualizarConfiguracoes(auth.getName(), dto);
    }

    @DeleteMapping("/me")
    public void inativarMe(Authentication auth) {
        if (auth == null) {
            throw new RuntimeException("Não autenticado");
        }
        service.inativarPorNomeUsuario(auth.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
