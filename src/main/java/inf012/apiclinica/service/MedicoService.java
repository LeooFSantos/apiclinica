package inf012.apiclinica.service;

import inf012.apiclinica.dto.MedicoCreateDTO;
import inf012.apiclinica.dto.MedicoListDTO;
import inf012.apiclinica.dto.MedicoUpdateDTO;
import inf012.apiclinica.repository.MedicoRepository;
import inf012.apiclinica.model.Medico;
import inf012.apiclinica.model.Endereco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class MedicoService {

    private final MedicoRepository repository;

    public MedicoService(MedicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Medico cadastrar(MedicoCreateDTO dto) {

        if (repository.existsByCrmAndCrmUf(dto.getCrm(), dto.getCrmUf())) {
            throw new RuntimeException("CRM já cadastrado para esta UF");
        }

        if (repository.existsByNomeUsuario(dto.getNomeUsuario())) {
            throw new RuntimeException("Nome de usuário já cadastrado");
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());
        endereco.setCep(dto.getCep());

        Medico medico = new Medico();
        medico.setNome(dto.getNome());
        medico.setEmail(dto.getEmail());
        medico.setTelefone(dto.getTelefone());
        medico.setCrm(dto.getCrm());
        medico.setCrmUf(dto.getCrmUf());
        medico.setEspecialidade(dto.getEspecialidade());
        medico.setNomeUsuario(dto.getNomeUsuario());
        medico.setSenha(dto.getSenha());
        medico.setEndereco(endereco);

        return repository.save(medico);
    }

    public Page<MedicoListDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(m -> new MedicoListDTO(
                        m.getNome(),
                        m.getEmail(),
                        m.getCrm(),
                        m.getEspecialidade()
                ));
    }

    @Transactional
    public Medico atualizar(Long id, MedicoUpdateDTO dto) {

        Medico medico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        medico.setNome(dto.getNome());
        medico.setTelefone(dto.getTelefone());

        Endereco endereco = medico.getEndereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());
        endereco.setCep(dto.getCep());

        return repository.save(medico);
    }

    @Transactional
    public void excluir(Long id) {

        Medico medico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        medico.setAtivo(false);
    }
}
