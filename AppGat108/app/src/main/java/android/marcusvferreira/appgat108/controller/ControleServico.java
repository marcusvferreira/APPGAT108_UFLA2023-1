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

public class ControleServico {
    private final DatabaseReference reference;

    public ControleServico(DatabaseReference reference) {
        this.reference = reference;
    }

    public void write(final Servico servico) {
        new Thread(() -> writeData(servico)).start();
    }

    private void writeData(final Servico servico) {
        // Inicializar o Firebase
       // FirebaseDatabase database = FirebaseDatabase.getInstance();
       // DatabaseReference reference = database.getReference("servicos"); // Nome da coleção no banco de dados

        // Criar um objeto JSON para representar os dados do Servico
        JSONObject servicoData = new JSONObject();
        try {
            //servicoData.put("id", servico.getId());
            servicoData.put("VeloRec", servico.getVeiculo().getVelociddadeRecomendada());
            servicoData.put("nomeMotorista", servico.getNomeMotorista());
            servicoData.put("carga", servico.getCarga());
            servicoData.put("dataHoraInicio", servico.getDataHoraInicio().toString());
            servicoData.put("distanciaTotal", servico.getVeiculo().getDistanciaTotal());
            servicoData.put("distanciaPercorrida", servico.getVeiculo().getDistanciaPercorrida());
            servicoData.put("tempoDejado", servico.getVeiculo().getTempoDesejeado());
            servicoData.put("tempoTranscorrido", servico.getVeiculo().getTempoTranscorrido());

            // Salvar os dados no Firebase
            reference.child(String.valueOf(servico.getId())).setValue(servicoData.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void read(final DataCallback callback, final Servico servico) {
        new Thread(() -> readData(callback, servico)).start();
    }

    public void readData(final DataCallback callback, final Servico servico) {
        DatabaseReference servicosRef = FirebaseDatabase.getInstance().getReference("servicos");

        servicosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String servicoId = dataSnapshot.getKey();
                   // if (servicoId.equals(servico.getId())) {
                        JSONObject servicoData = dataSnapshot.getValue(JSONObject.class);

                        try {
                            double distanciaTotal = servicoData.getDouble("distanciaTotal");
                            double distanciaPercorrida = servicoData.getDouble("distanciaPercorrida");
                            double tempoDejado = servicoData.getDouble("tempoDejado");
                            double tempoTranscorrido = servicoData.getDouble("tempoTranscorrido");

                            double distanciaRestanteOutroVeiculo = distanciaTotal - distanciaPercorrida;
                            double tempoRestanteOutroVeiculo = tempoDejado - tempoTranscorrido;

                            callback.onDataReceived(distanciaRestanteOutroVeiculo, tempoRestanteOutroVeiculo);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                  //  }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw new RuntimeException(error.toException());
            }
        });
    }


    public void deleteAllData() {
        reference.setValue(null);
    }

    public interface DataCallback {
        void onDataReceived(double distanciaRestanteOutroVeiculo, double tempoRestanteOutroVeiculo);
    }

}

