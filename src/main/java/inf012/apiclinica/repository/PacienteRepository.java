package inf012.apiclinica.repository;

import inf012.apiclinica.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByNomeUsuario(String nomeUsuario);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Paciente> findAllByAtivoTrue(Pageable pageable);

    Paciente findByNomeUsuario(String nomeUsuario);
}
