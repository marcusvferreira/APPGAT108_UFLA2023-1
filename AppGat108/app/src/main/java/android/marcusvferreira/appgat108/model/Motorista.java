package android.marcusvferreira.appgat108.model;

/**
 * A classe Motorista representa um motorista no sistema, contendo informações básicas como
 * nome e senha. Ela possui um construtor que recebe o nome e a senha do motorista para inicializar
 * os atributos da classe. É utilizada em conjunto com a classe LoginActivity para validar as
 * credenciais de um motorista durante o processo de login no aplicativo.
 */
public class Motorista {

    // Atributos da classe
    private final String nome;
    private final String senha;

    // Construtor da classe Motorista
    public Motorista(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    // Getters dos atributos da classe

    /**
     * Obtém o nome do motorista.
     *
     * @return O nome do motorista.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém a senha do motorista.
     *
     * @return A senha do motorista.
     */
    public String getSenha() {
        return senha;
    }
}
