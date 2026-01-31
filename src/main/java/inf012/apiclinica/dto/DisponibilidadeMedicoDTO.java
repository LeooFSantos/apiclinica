package inf012.apiclinica.dto;

import inf012.apiclinica.model.Especialidade;
import java.util.List;

public class DisponibilidadeMedicoDTO {
    private Long medicoId;
    private String nome;
    private String crm;
    private String crmUf;
    private Especialidade especialidade;
    private List<String> horariosDisponiveis;

    public DisponibilidadeMedicoDTO() {}

    public DisponibilidadeMedicoDTO(Long medicoId, String nome, String crm, String crmUf, Especialidade especialidade, List<String> horariosDisponiveis) {
        this.medicoId = medicoId;
        this.nome = nome;
        this.crm = crm;
        this.crmUf = crmUf;
        this.especialidade = especialidade;
        this.horariosDisponiveis = horariosDisponiveis;
    }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getCrmUf() { return crmUf; }
    public void setCrmUf(String crmUf) { this.crmUf = crmUf; }

    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }

    public List<String> getHorariosDisponiveis() { return horariosDisponiveis; }
    public void setHorariosDisponiveis(List<String> horariosDisponiveis) { this.horariosDisponiveis = horariosDisponiveis; }
}