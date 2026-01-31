package inf012.apiclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PacienteUpdateDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank
    @Pattern(regexp = "\\d{10,11}")
    private String telefone;

    @NotBlank
    @Size(min = 2, max = 120)
    private String logradouro;

    @Pattern(regexp = "\\d{0,10}")
    private String numero;
    private String complemento;

    @NotBlank
    @Size(min = 2, max = 80)
    private String bairro;

    @NotBlank
    @Size(min = 2, max = 80)
    private String cidade;

    @NotBlank
    @Pattern(regexp = "[A-Za-z]{2}")
    private String uf;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    private String cep;

    @Size(min = 3, max = 30)
    @Pattern(regexp = "[A-Za-z0-9_.-]+")
    private String nomeUsuario;

    @Size(min = 6, max = 60)
    private String senha;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

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

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
