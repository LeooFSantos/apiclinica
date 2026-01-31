package inf012.apiclinica.controller;

import inf012.apiclinica.dto.JwtAuthResponseDTO;
import inf012.apiclinica.dto.LoginDTO;
import inf012.apiclinica.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginDTO loginDTO) {
        try {
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

    @PostMapping("/refresh")
    public ResponseEntity<?> renovarToken(@RequestParam String nomeUsuario) {
        String newToken = tokenProvider.generateTokenFromUsername(nomeUsuario);
        return ResponseEntity.ok(new JwtAuthResponseDTO(newToken, nomeUsuario));
    }

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
