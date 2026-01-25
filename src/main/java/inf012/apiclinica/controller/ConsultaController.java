package inf012.apiclinica.controller;

import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.service.ConsultaService;
import inf012.apiclinica.dto.ConsultaCreateDTO;
import inf012.apiclinica.dto.ConsultaCancelamentoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
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
}