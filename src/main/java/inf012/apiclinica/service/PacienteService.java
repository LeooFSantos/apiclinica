package inf012.apiclinica.service;

import inf012.apiclinica.model.Paciente;
import inf012.apiclinica.model.Endereco;
import inf012.apiclinica.repository.PacienteRepository;
import inf012.apiclinica.dto.PacienteCreateDTO;
import inf012.apiclinica.dto.PacienteUpdateDTO;
import inf012.apiclinica.dto.PacienteListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Paciente cadastrar(PacienteCreateDTO dto) {

        if (repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());
        endereco.setCep(dto.getCep());

        Paciente paciente = new Paciente();
        paciente.setNome(dto.getNome());
        paciente.setEmail(dto.getEmail());
        paciente.setTelefone(dto.getTelefone());
        paciente.setCpf(dto.getCpf());
        paciente.setEndereco(endereco);

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

    @Transactional
    public Paciente atualizar(Long id, PacienteUpdateDTO dto) {

        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.setNome(dto.getNome());
        paciente.setTelefone(dto.getTelefone());

        Endereco endereco = paciente.getEndereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());
        endereco.setCep(dto.getCep());

        return repository.save(paciente);
    }

    @Transactional
    public void excluir(Long id) {

        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.setAtivo(false);
    }
}
