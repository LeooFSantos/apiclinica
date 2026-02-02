package inf012.apiclinica.service;

import inf012.apiclinica.dto.MedicoCreateDTO;
import inf012.apiclinica.dto.MedicoListDTO;
import inf012.apiclinica.dto.MedicoUpdateDTO;
import inf012.apiclinica.repository.MedicoRepository;
import inf012.apiclinica.repository.ConsultaRepository;
import inf012.apiclinica.model.Medico;
import inf012.apiclinica.model.Endereco;
import inf012.apiclinica.model.Consulta;
import inf012.apiclinica.model.MotivoCancelamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository repository;
    private final ConsultaRepository consultaRepository;

    public MedicoService(MedicoRepository repository, ConsultaRepository consultaRepository) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public Medico cadastrar(MedicoCreateDTO dto) {

        if (repository.existsByCrmAndCrmUf(dto.getCrm(), dto.getCrmUf())) {
            throw new RuntimeException("CRM já cadastrado para esta UF");
        }

        if (repository.existsByNomeUsuario(dto.getNomeUsuario())) {
            throw new RuntimeException("Nome de usuário já cadastrado");
        }

        Endereco endereco = dto.getEndereco();

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

    public Medico buscarPorNomeUsuario(String nomeUsuario) {
        Medico medico = repository.findByNomeUsuario(nomeUsuario);
        if (medico == null) {
            throw new RuntimeException("Médico não encontrado");
        }
        return medico;
    }

    public long contarAtivos() {
        return repository.countByAtivoTrue();
    }

    @Transactional
    public Medico atualizarConfiguracoes(String nomeUsuario, MedicoUpdateDTO dto) {
        Medico medico = repository.findByNomeUsuario(nomeUsuario);
        if (medico == null) {
            throw new RuntimeException("Médico não encontrado");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (repository.existsByEmailAndIdNot(dto.getEmail(), medico.getId())) {
                throw new RuntimeException("E-mail já cadastrado");
            }
            medico.setEmail(dto.getEmail());
        }

        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            medico.setTelefone(dto.getTelefone());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            medico.setSenha(dto.getSenha());
        }

        Endereco endereco = medico.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
            medico.setEndereco(endereco);
        }

        if (dto.getLogradouro() != null && !dto.getLogradouro().isBlank()) endereco.setLogradouro(dto.getLogradouro());
        if (dto.getNumero() != null) endereco.setNumero(dto.getNumero());
        if (dto.getComplemento() != null) endereco.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null && !dto.getBairro().isBlank()) endereco.setBairro(dto.getBairro());
        if (dto.getCidade() != null && !dto.getCidade().isBlank()) endereco.setCidade(dto.getCidade());
        if (dto.getUf() != null && !dto.getUf().isBlank()) endereco.setUf(dto.getUf());
        if (dto.getCep() != null && !dto.getCep().isBlank()) endereco.setCep(dto.getCep());

        return repository.save(medico);
    }

    @Transactional
    public void inativarPorNomeUsuario(String nomeUsuario) {
        Medico medico = repository.findByNomeUsuario(nomeUsuario);
        if (medico == null) {
            throw new RuntimeException("Médico não encontrado");
        }
        medico.setAtivo(false);

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDateTime agora = LocalDateTime.now(zone);
        List<Consulta> abertas = consultaRepository.findByMedicoIdAndCanceladaEmIsNull(medico.getId());
        for (Consulta c : abertas) {
            c.setMotivoCancelamento(MotivoCancelamento.MEDICO_INATIVO);
            c.setCanceladaEm(agora);
        }
        consultaRepository.saveAll(abertas);
        repository.save(medico);
    }

    @Transactional
    public void excluir(Long id) {

        Medico medico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        medico.setAtivo(false);
    }
}
