package inf012.apiclinica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import inf012.apiclinica.repository.PacienteRepository;
import inf012.apiclinica.repository.MedicoRepository;
import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.model.Medico;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException ex) {
                    userDetails = carregarPorRepositorio(username);
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private UserDetails carregarPorRepositorio(String username) {
        Paciente paciente = pacienteRepository.findByNomeUsuario(username);
        if (paciente != null) {
            return User.withUsername(username)
                    .password("")
                    .roles("USER")
                    .build();
        }

        Medico medico = medicoRepository.findByNomeUsuario(username);
        if (medico != null) {
            return User.withUsername(username)
                    .password("")
                    .roles("MEDICO")
                    .build();
        }

        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}
