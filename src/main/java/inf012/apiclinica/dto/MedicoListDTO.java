package inf012.apiclinica.dto;

import inf012.apiclinica.model.Especialidade;

public class MedicoListDTO {

    private String nome;
    private String email;
    private String crm;
    private Especialidade especialidade;
    private String nomeUsuario;

    public MedicoListDTO(String nome, String email, String crm, Especialidade especialidade) {
        this.nome = nome;
        this.email = email;
        this.crm = crm;
        this.especialidade = especialidade;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCrm() { return crm; }
    public Especialidade getEspecialidade() { return especialidade; }
    public String getNomeUsuario() { return nomeUsuario; }
}

