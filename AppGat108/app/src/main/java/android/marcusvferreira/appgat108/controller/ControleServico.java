package android.marcusvferreira.appgat108.controller;

import android.marcusvferreira.appgat108.model.Servico;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class ControleServico {
    private final DatabaseReference reference;

    public ControleServico(DatabaseReference reference) {
        this.reference = reference;
    }

    public void write(final Servico servico) {
        new Thread(() -> writeData(servico)).start();
    }

    private void writeData(final Servico servico) {
        // Inicializar o aplicativo Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("servicos"); // Nome da coleção no banco de dados

        // Criar um objeto JSON para representar os dados do Servico
        JSONObject servicoData = new JSONObject();
        try {
            servicoData.put("id", servico.getId());
            servicoData.put("carga", servico.getCarga());
            servicoData.put("nomeMotorista", servico.getNomeMotorista());
            servicoData.put("dataHoraInicio", servico.getDataHoraInicio().toString());
            servicoData.put("tempo", servico.getVeiculo().getTempoTranscorrido());

            // Salvar os dados no Firebase
            reference.child(String.valueOf(servico.getId())).setValue(servicoData.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void read(final DataCallback callback) {
        new Thread(() -> readData(callback)).start();
    }

    private void readData(final DataCallback callback) {
        DatabaseReference servicosRef = FirebaseDatabase.getInstance().getReference("servicos");

        servicosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String json = dataSnapshot.getValue(String.class);
                    JSONObject servicoData;
                    try {
                        servicoData = new JSONObject(json);

                        int id = servicoData.getInt("id");
                        String carga = servicoData.getString("carga");
                        String nomeMotorista = servicoData.getString("nomeMotorista");
                        String dataHoraInicio = servicoData.getString("dataHoraInicio");

                        Servico servico = new Servico(id, carga, nomeMotorista);
                        servico.setDataHoraInicio(LocalDateTime.parse(dataHoraInicio));

                        callback.onDataReceived(servico);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
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
        void onDataReceived(Servico servico);
    }
}

