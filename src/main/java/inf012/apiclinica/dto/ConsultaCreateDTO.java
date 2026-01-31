package inf012.apiclinica.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import inf012.apiclinica.model.Especialidade;

public class ConsultaCreateDTO {

    @NotNull
    private Long pacienteId;

    private Long medicoId;

    private Especialidade especialidade;

    @NotNull
    private LocalDateTime dataHora;

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
