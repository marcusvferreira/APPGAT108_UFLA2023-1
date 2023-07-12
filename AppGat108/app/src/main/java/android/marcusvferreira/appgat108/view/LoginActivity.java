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

public class LoginActivity extends AppCompatActivity {

    private EditText campoNome, campoSenha;
    private RadioGroup rgCargas;
    private boolean isLoginEfetuado = false;
    private String cargaSelecionada;
    private Servico servico;
    private final List<Motorista> listaMotoristas = new ArrayList<>();
    private MainActivity mainActivity;
    // fazer o motoristo logado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        listaMotoristas.add(new Motorista("", ""));
        listaMotoristas.add(new Motorista("Marcus", "m123"));
        listaMotoristas.add(new Motorista("Rafael", "r123"));
        listaMotoristas.add(new Motorista("Giovanna", "g123"));
        listaMotoristas.add(new Motorista("Enrique", "e123"));

        campoNome = findViewById(R.id.et_nomeMotorista);
        campoSenha = findViewById(R.id.et_senha);
        rgCargas = findViewById(R.id.rg_cargas);

        rgCargas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rgSelecionado = findViewById(checkedId);
                cargaSelecionada = rgSelecionado.getText().toString();
            }
        });
    }

    public void clickLogin(View view) {
        String nome = campoNome.getText().toString().trim(); // nome de usuário inserido pelo usuário
        String senha = campoSenha.getText().toString().trim(); // senha inserida pelo usuário

        for (Motorista motorista : listaMotoristas) {
            if (motorista.getNome().equalsIgnoreCase(nome) && motorista.getSenha().equals(senha)) {
                isLoginEfetuado = true; // Se as credenciais correspondem a um motorista válido, então o login é validado
                break;
            }
        }

        if (isLoginEfetuado) {
            int idServico = (int) (Math.random() * 1000); // Gera um número aleatório para identificar o serviço
            servico = new Servico(idServico, cargaSelecionada, nome);

            // Exibe uma mensagem de login bem-sucedido
            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
            mudarParaActivityMain();
        } else {
            // Exibe uma mensagem de erro de login inválido
            Toast.makeText(this, "Nome de usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
        }

    }

    private void mudarParaActivityMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("servico", servico);
        startActivity(intent); //Inicializa a activity main
        finish(); //Finaliza a login activity
    }
}