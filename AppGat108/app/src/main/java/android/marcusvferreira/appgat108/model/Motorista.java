package android.marcusvferreira.appgat108.model;

public class Motorista {
    private String nome;
    private String senha;

    public Motorista(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }
}
