package inf012.apiclinica.dto;

public class JwtAuthResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private String nomeUsuario;

    public JwtAuthResponseDTO(String token, String nomeUsuario) {
        this.token = token;
        this.nomeUsuario = nomeUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return tipo;
    }

    public void setType(String tipo) {
        this.tipo = tipo;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
