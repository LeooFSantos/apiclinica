package inf012.apiclinica.controller;

import inf012.apiclinica.dto.JwtAuthResponseDTO;
import inf012.apiclinica.dto.LoginDTO;
import inf012.apiclinica.security.JwtTokenProvider;
import inf012.apiclinica.repository.PacienteRepository;
import inf012.apiclinica.repository.MedicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * Endpoints de autenticação e identificação do usuário logado.
 */
@RestController
@Tag(name = "Autenticação", description = "Login, refresh de token e dados do usuário autenticado")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Operation(summary = "Realiza login e retorna JWT")
    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            var paciente = pacienteRepository.findByNomeUsuario(loginDTO.getNomeUsuario());
            if (paciente != null && Boolean.FALSE.equals(paciente.getAtivo())) {
                return ResponseEntity.badRequest().body("Cadastro Inativo");
            }
            var medico = medicoRepository.findByNomeUsuario(loginDTO.getNomeUsuario());
            if (medico != null && Boolean.FALSE.equals(medico.getAtivo())) {
                return ResponseEntity.badRequest().body("Cadastro Inativo");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getNomeUsuario(),
                            loginDTO.getSenha()
                    )
            );

            String token = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthResponseDTO(token, loginDTO.getNomeUsuario()));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Credenciais inválidas!");
        }
    }

    @Operation(summary = "Gera novo token JWT a partir do usuário")
    @PostMapping("/refresh")
    public ResponseEntity<?> renovarToken(@RequestParam String nomeUsuario) {
        String newToken = tokenProvider.generateTokenFromUsername(nomeUsuario);
        return ResponseEntity.ok(new JwtAuthResponseDTO(newToken, nomeUsuario));
    }

    @Operation(summary = "Retorna informações do usuário autenticado")
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String nome = authentication.getName();
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("nomeUsuario", nome, "roles", roles));
    }
}
