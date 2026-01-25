package inf012.apiclinica.controller;

import inf012.apiclinica.model.Medico;
import inf012.apiclinica.service.MedicoService;
import inf012.apiclinica.dto.MedicoCreateDTO;
import inf012.apiclinica.dto.MedicoListDTO;
import inf012.apiclinica.dto.MedicoUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medico cadastrar(@RequestBody @Valid MedicoCreateDTO dto) {
        return service.cadastrar(dto);
    }

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

    @PutMapping("/{id}")
    public Medico atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicoUpdateDTO dto
    ) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
