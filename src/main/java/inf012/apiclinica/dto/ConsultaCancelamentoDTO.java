package inf012.apiclinica.dto;

import inf012.apiclinica.model.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public class ConsultaCancelamentoDTO {

    @NotNull
    private MotivoCancelamento motivo;

    public MotivoCancelamento getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoCancelamento motivo) {
        this.motivo = motivo;
    }
}