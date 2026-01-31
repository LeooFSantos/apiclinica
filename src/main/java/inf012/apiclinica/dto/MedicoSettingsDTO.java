package inf012.apiclinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MedicoSettingsDTO {

    @Email
    @Size(max = 120)
    private String email;

    @Pattern(regexp = "\\d{10,11}")
    private String telefone;

    @Size(min = 6, max = 60)
    private String senha;

    @Size(min = 2, max = 120)
    private String logradouro;

    @Pattern(regexp = "\\d{0,10}")
    private String numero;

    private String complemento;

    @Size(min = 2, max = 80)
    private String bairro;

    @Size(min = 2, max = 80)
    private String cidade;

    @Pattern(regexp = "[A-Za-z]{2}")
    private String uf;

    @Pattern(regexp = "\\d{8}")
    private String cep;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
}