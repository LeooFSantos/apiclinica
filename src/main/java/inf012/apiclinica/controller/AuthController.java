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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthResponseDTO(token, loginDTO.getUsername()));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Credenciais inválidas!");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String username) {
        String newToken = tokenProvider.generateTokenFromUsername(username);
        return ResponseEntity.ok(new JwtAuthResponseDTO(newToken, username));
    }
}
