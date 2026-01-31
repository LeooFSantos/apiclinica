package inf012.apiclinica.security;

import inf012.apiclinica.model.Medico;
import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.repository.MedicoRepository;
import inf012.apiclinica.repository.PacienteRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CustomUserDetailsService(
            InMemoryUserDetailsManager inMemoryUserDetailsManager,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository
    ) {
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return inMemoryUserDetailsManager.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            // continua para banco
        }

        Paciente paciente = pacienteRepository.findByNomeUsuario(username);
        if (paciente != null) {
            return User.withUsername(username)
                    .password(paciente.getSenha())
                    .roles("USER")
                    .build();
        }

        Medico medico = medicoRepository.findByNomeUsuario(username);
        if (medico != null) {
            return User.withUsername(username)
                    .password(medico.getSenha())
                    .roles("MEDICO")
                    .build();
        }

        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}