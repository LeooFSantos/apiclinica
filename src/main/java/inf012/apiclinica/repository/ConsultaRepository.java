package inf012.apiclinica.repository;

import inf012.apiclinica.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
