package inf012.apiclinica.service;

import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.model.Endereco;
import inf012.apiclinica.repository.PacienteRepository;
import inf012.apiclinica.dto.PacienteCreateDTO;
import inf012.apiclinica.dto.PacienteUpdateDTO;
import inf012.apiclinica.dto.PacienteListDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    private final PacienteRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final InMemoryUserDetailsManager userDetailsManager;

    public PacienteService(PacienteRepository repository,
                           PasswordEncoder passwordEncoder,
                           InMemoryUserDetailsManager userDetailsManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
    }

    @Transactional
    public Paciente cadastrar(PacienteCreateDTO dto) {

        if (repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        if (repository.existsByNomeUsuario(dto.getNomeUsuario())) {
            throw new RuntimeException("Nome de usuário já cadastrado");
        }

        Endereco endereco = dto.getEndereco();

        Paciente paciente = new Paciente();
        paciente.setNome(dto.getNome());
        paciente.setEmail(dto.getEmail());
        paciente.setTelefone(dto.getTelefone());
        paciente.setCpf(dto.getCpf());
        paciente.setNomeUsuario(dto.getNomeUsuario());
        paciente.setSenha(passwordEncoder.encode(dto.getSenha()));
        paciente.setEndereco(endereco);

        if (!userDetailsManager.userExists(dto.getNomeUsuario())) {
            userDetailsManager.createUser(User.withUsername(dto.getNomeUsuario())
                    .password(passwordEncoder.encode(dto.getSenha()))
                    .roles("USER")
                    .build());
        }

        return repository.save(paciente);
    }

    public Page<PacienteListDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(p -> new PacienteListDTO(
                        p.getNome(),
                        p.getEmail(),
                        p.getCpf()
                ));
    }

    public Paciente buscarPorNomeUsuario(String nomeUsuario) {
        Paciente paciente = repository.findByNomeUsuario(nomeUsuario);
        if (paciente == null) {
            throw new RuntimeException("Paciente não encontrado");
        }
        return paciente;
    }

    @Transactional
    public Paciente atualizarConfiguracoes(String nomeUsuario, PacienteUpdateDTO dto) {
        Paciente paciente = repository.findByNomeUsuario(nomeUsuario);
        if (paciente == null) {
            throw new RuntimeException("Paciente não encontrado");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (repository.existsByEmailAndIdNot(dto.getEmail(), paciente.getId())) {
                throw new RuntimeException("E-mail já cadastrado");
            }
            paciente.setEmail(dto.getEmail());
        }

        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            paciente.setTelefone(dto.getTelefone());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            paciente.setSenha(passwordEncoder.encode(dto.getSenha()));
            if (userDetailsManager.userExists(paciente.getNomeUsuario())) {
                userDetailsManager.updateUser(User.withUsername(paciente.getNomeUsuario())
                        .password(paciente.getSenha())
                        .roles("USER")
                        .build());
            }
        }

        Endereco endereco = paciente.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
            paciente.setEndereco(endereco);
        }

        if (dto.getLogradouro() != null && !dto.getLogradouro().isBlank()) endereco.setLogradouro(dto.getLogradouro());
        if (dto.getNumero() != null) endereco.setNumero(dto.getNumero());
        if (dto.getComplemento() != null) endereco.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null && !dto.getBairro().isBlank()) endereco.setBairro(dto.getBairro());
        if (dto.getCidade() != null && !dto.getCidade().isBlank()) endereco.setCidade(dto.getCidade());
        if (dto.getUf() != null && !dto.getUf().isBlank()) endereco.setUf(dto.getUf());
        if (dto.getCep() != null && !dto.getCep().isBlank()) endereco.setCep(dto.getCep());

        return repository.save(paciente);
    }

    @Transactional
    public void inativarPorNomeUsuario(String nomeUsuario) {
        Paciente paciente = repository.findByNomeUsuario(nomeUsuario);
        if (paciente == null) {
            throw new RuntimeException("Paciente não encontrado");
        }
        paciente.setAtivo(false);
    }

    @Transactional
    public void excluir(Long id) {

        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.setAtivo(false);
    }
}
