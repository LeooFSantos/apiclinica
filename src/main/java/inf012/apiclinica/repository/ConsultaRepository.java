package inf012.apiclinica.repository;

import inf012.apiclinica.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByPacienteIdAndDataHoraBetween(
            Long pacienteId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);
    List<Consulta> findByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);

    List<Consulta> findByMedicoIdAndDataHoraBetween(Long medicoId, LocalDateTime inicio, LocalDateTime fim);

    Page<Consulta> findByPacienteId(Long pacienteId, Pageable pageable);
}
