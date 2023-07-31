package android.marcusvferreira.appgat108.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.marcusvferreira.appgat108.model.Motorista;
import android.marcusvferreira.appgat108.model.Servico;
import android.os.Bundle;
import android.marcusvferreira.appgat108.R;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe LoginActivity representa a tela de login do aplicativo Android "appgat108".
 * Nesta tela, os usuários podem inserir seu nome de usuário e senha, além de selecionar
 * uma carga específica antes de efetuar o login. A classe é responsável por validar as
 * credenciais inseridas pelo usuário, realizar o login caso sejam válidas e redirecionar
 * o usuário para a MainActivity. Caso as credenciais sejam inválidas, uma mensagem de
 * erro é exibida. A LoginActivity também possui uma lista de motoristas pré-cadastrados
 * e gera um objeto Servico contendo informações do serviço e do motorista logado após o
 * login bem sucedido.
 */
public class LoginActivity extends AppCompatActivity {

    // Declaração de variáveis
    private EditText campoNome, campoSenha;  // Campos de texto para inserir o nome e a senha do motorista
    private RadioGroup rgCargas;  // Grupo de botões para seleção de carga
    private boolean isLoginEfetuado = false; // Indica se o login foi realizado com sucesso
    private String cargaSelecionada; // Armazena a carga selecionada pelo motorista
    private Servico servico; // Objeto que representa um serviço
    private final List<Motorista> listaMotoristas = new ArrayList<>(); // Lista de motoristas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Define o layout da activity como activity_login.xml

        // Adiciona alguns motoristas à lista de motoristas
        listaMotoristas.add(new Motorista("Marcus", "m123"));
        listaMotoristas.add(new Motorista("Rafael", "r123"));
        listaMotoristas.add(new Motorista("Giovanna", "g123"));
        listaMotoristas.add(new Motorista("Enrique", "e123"));

        // Vincula os campos de texto e o grupo de botões aos elementos do layout pelo ID
        campoNome = findViewById(R.id.et_nomeMotorista);
        campoSenha = findViewById(R.id.et_senha);
        rgCargas = findViewById(R.id.rg_cargas);

        // Define um listener para o grupo de botões de rádio
        rgCargas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Obtém o texto associado ao botão de rádio selecionado e armazena na variável cargaSelecionada
                RadioButton rgSelecionado = findViewById(checkedId);
                cargaSelecionada = rgSelecionado.getText().toString();
            }
        });
    }

    // Método chamado quando o botão de login é clicado
    public void clickLogin(View view) {
        String nome = campoNome.getText().toString().trim(); // Nome de usuário inserido pelo usuário
        String senha = campoSenha.getText().toString().trim(); // Senha inserida pelo usuário

        // Verifica se as credenciais correspondem a algum motorista da lista
        for (Motorista motorista : listaMotoristas) {
            if (motorista.getNome().equalsIgnoreCase(nome) && motorista.getSenha().equals(senha)) {
                isLoginEfetuado = true; // Se as credenciais correspondem a um motorista válido, então o login é validado
                break;
            }
        }

        if (isLoginEfetuado) {
            // Gera um número aleatório para identificar o serviço
            int idServico = (int) (Math.random() * 1000);
            // Cria um objeto Servico com as informações do serviço e do motorista logado
            servico = new Servico(idServico, cargaSelecionada, nome);

            // Exibe uma mensagem de login bem-sucedido
            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();

            // Chama o método para mudar para a MainActivity
            mudarParaActivityMain();
        } else {
            // Exibe uma mensagem de erro de login inválido
            Toast.makeText(this, "Nome de usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para mudar para a MainActivity
    private void mudarParaActivityMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Passa o objeto Servico como extra para a MainActivity
        intent.putExtra("servico", servico);
        // Inicializa a activity main
        startActivity(intent);
        // Finaliza a login activity para que o usuário não possa voltar à tela de login pressionando o botão "Voltar"
        finish();
    }
}
