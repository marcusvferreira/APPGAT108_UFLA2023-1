package android.marcusvferreira.appgat108.controller;

import android.marcusvferreira.appgat108.model.Servico;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Essa classe é responsável por fornecer funcionalidades relacionadas ao controle de serviços
 * utilizando criptografia AES-128 bits. Permite escrever e ler dados criptografados no Firebase com
 * o auxílio da classe CriptografiaAES.
 */
public class ControleServico {

    /**
     * A variável DatabaseReference reference é um objeto que representa uma referência a
     * um local específico no banco de dados do Firebase. Essa referência é utilizada para
     * realizar operações de leitura, escrita e exclusão de dados nesse local.
     */
    private final DatabaseReference reference;

    // Criptografia AES-128 bits
    private static String chave = "MARCUSAPPGAT108ABCDEFG0123456789";
    private static String vetorInicializacao = "0123456789MARCUS";

    public ControleServico(DatabaseReference reference) {
        this.reference = reference;
    }

    // Método para escrever os dados criptografados no Firebase
    public void escrever(final Servico servico) {
        new Thread(() -> escreverDados(servico)).start();
    }

    private void escreverDados(final Servico servico) {
        // Criar um objeto JSON para representar os dados do Servico
        JSONObject dadosServico = new JSONObject();
        try {
            dadosServico.put("nomeMotorista", servico.getNomeMotorista());
            dadosServico.put("carga", servico.getCarga());
            dadosServico.put("dataHoraInicio", servico.getDataHoraInicio().toString());
            dadosServico.put("distanciaTotal", servico.getVeiculo().getDistanciaTotal());
            dadosServico.put("distanciaPercorrida", servico.getVeiculo().getDistanciaPercorrida());
            dadosServico.put("tempoDesejado", servico.getVeiculo().getTempoDesejado());
            dadosServico.put("tempoTranscorrido", servico.getVeiculo().getTempoTranscorrido());

            // Converter o objeto JSON para uma string
            String jsonDados = dadosServico.toString();

            // Criptografar os dados
            String dadosCriptografados = CriptografiaAES.criptografar(jsonDados, chave, vetorInicializacao);

            // Salvar os dados criptografados no Firebase
            reference.child(String.valueOf(servico.getId())).setValue(dadosCriptografados);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para ler os dados criptografados do Firebase e descriptografá-los
    public void ler(final DataCallback callback, final Servico servico) {
        new Thread(() -> lerDados(callback, servico)).start();
    }

    private void lerDados(final DataCallback callback, final Servico servico) {
        DatabaseReference servicosRef = FirebaseDatabase.getInstance().getReference("servicos");
        servicosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int servicoId = Integer.parseInt(dataSnapshot.getKey());
                    if (servicoId != servico.getId()) {
                        String servicoCriptografado = dataSnapshot.getValue(String.class);
                        try {
                            // Descriptografar os dados
                            String dadosDescriptografados = CriptografiaAES.descriptografar(servicoCriptografado, chave, vetorInicializacao);

                            // Converter a string descriptografada para um objeto JSON
                            JSONObject dadosServico = new JSONObject(dadosDescriptografados);

                            double distanciaTotal = dadosServico.getDouble("distanciaTotal");
                            double distanciaPercorrida = dadosServico.getDouble("distanciaPercorrida");
                            double tempoDesejado = dadosServico.getDouble("tempoDesejado");
                            double tempoTranscorrido = dadosServico.getDouble("tempoTranscorrido");

                            double distanciaRestanteOutroVeiculo = distanciaTotal - distanciaPercorrida;
                            double tempoRestanteOutroVeiculo = tempoDesejado - tempoTranscorrido;

                            // Chamada do callback com os dados descriptografados
                            callback.onDataReceived(distanciaRestanteOutroVeiculo, tempoRestanteOutroVeiculo);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw new RuntimeException(error.toException());
            }
        });
    }

    // Método para deletar todos os dados do Firebase
    public void deletarTodosOsDados() {
        reference.setValue(null);
    }

    // Interface para retornar os dados descriptografados
    public interface DataCallback {
        void onDataReceived(double distanciaRestanteOutroVeiculo, double tempoRestanteOutroVeiculo);
    }
}
