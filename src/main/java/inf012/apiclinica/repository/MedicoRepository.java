package inf012.apiclinica.repository;

import inf012.apiclinica.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByCrm(String crm);
    boolean existsByEmail(String email);

    Page<Medico> findAllByAtivoTrue(Pageable pageable);
    List<Medico> findAllByAtivoTrue();
}