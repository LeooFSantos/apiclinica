package inf012.apiclinica.controller;

import inf012.apiclinica.dto.MedicoCreateDTO;
import inf012.apiclinica.model.Medico;
import inf012.apiclinica.model.Endereco;
import inf012.apiclinica.model.MedicoRequest;
import inf012.apiclinica.repository.MedicoRequestRepository;
import inf012.apiclinica.repository.MedicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicos/requests")
public class MedicoRegistrationController {

    @Autowired
    private MedicoRequestRepository requestRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InMemoryUserDetailsManager userDetailsManager;

    // Qualquer um pode solicitar registro como médico
    @PostMapping
    public ResponseEntity<?> criarSolicitacao(@Valid @RequestBody MedicoCreateDTO dto) {
        MedicoRequest req = new MedicoRequest();
        req.setNome(dto.getNome());
        req.setEmail(dto.getEmail());
        req.setTelefone(dto.getTelefone());
        req.setCrm(dto.getCrm());
        req.setCrmUf(dto.getCrmUf());
        req.setEspecialidade(dto.getEspecialidade());
        req.setEndereco(dto.getEndereco());
        req.setNomeUsuario(dto.getNomeUsuario());
        req.setSenha(passwordEncoder.encode(dto.getSenha()));
        MedicoRequest saved = requestRepository.save(req);
        return ResponseEntity.created(URI.create("/api/medicos/requests/" + saved.getId())).body(saved);
    }

    // ADMIN: listar solicitações
    @GetMapping
    public ResponseEntity<List<MedicoRequest>> listarSolicitacoes() {
        return ResponseEntity.ok(requestRepository.findByApprovedFalseAndRejectedFalse());
    }

    // ADMIN: aprovar solicitação
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        Optional<MedicoRequest> opt = requestRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        MedicoRequest req = opt.get();
        if (req.isApproved()) return ResponseEntity.badRequest().body("Já aprovado");
        if (req.isRejected()) return ResponseEntity.badRequest().body("Solicitação já rejeitada");

        // criar entidade Medico
        Medico m = new Medico();
        m.setNome(req.getNome());
        m.setEmail(req.getEmail());
        m.setTelefone(req.getTelefone());
        m.setCrm(req.getCrm());
        m.setCrmUf(req.getCrmUf());
        m.setEspecialidade(req.getEspecialidade());
        m.setNomeUsuario(req.getNomeUsuario());
        m.setSenha(req.getSenha());
        m.setEndereco(req.getEndereco());
        Medico savedMed = medicoRepository.save(m);

        // criar conta de usuário com role MEDICO
        if (!userDetailsManager.userExists(req.getNomeUsuario())) {
            userDetailsManager.createUser(User.withUsername(req.getNomeUsuario())
                    .password(req.getSenha())
                    .roles("MEDICO")
                    .build());
        }

        req.setApproved(true);
        requestRepository.save(req);
        requestRepository.delete(req);
        return ResponseEntity.ok(savedMed);
    }

    // ADMIN: rejeitar solicitação
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejeitar(@PathVariable Long id) {
        Optional<MedicoRequest> opt = requestRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        MedicoRequest req = opt.get();
        if (req.isApproved()) return ResponseEntity.badRequest().body("Já aprovado");
        if (req.isRejected()) return ResponseEntity.badRequest().body("Já rejeitado");
        req.setRejected(true);
        requestRepository.save(req);
        requestRepository.delete(req);
        return ResponseEntity.ok().build();
    }
}
