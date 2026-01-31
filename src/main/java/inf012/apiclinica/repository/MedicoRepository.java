package inf012.apiclinica.repository;

import inf012.apiclinica.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByCrmAndCrmUf(String crm, String crmUf);
    boolean existsByNomeUsuario(String nomeUsuario);

    Page<Medico> findAllByAtivoTrue(Pageable pageable);
    List<Medico> findAllByAtivoTrue();

    List<Medico> findAllByAtivoTrueAndEspecialidade(inf012.apiclinica.model.Especialidade especialidade);

    Medico findByNomeUsuario(String nomeUsuario);
}