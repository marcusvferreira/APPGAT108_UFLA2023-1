package android.marcusvferreira.appgat108.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Servico implements Parcelable, Runnable {
    private final int id;
    private final String carga, nomeMotorista;
    private LocalDateTime dataHoraInicio, dataHoraFim;
    private Veiculo veiculo;

    public Servico(int id, String carga, String nomeMotorista) {
        this.id = id;
        this.carga = carga;
        this.nomeMotorista = nomeMotorista;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getCarga() {
        return carga;
    }


    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    @Override
    public void run() {
        // Inicializar o aplicativo Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("servicos"); // Nome da coleção no banco de dados

        // Criar um objeto JSON para representar os dados do Servico
        JSONObject servicoData = new JSONObject();
        try {
            servicoData.put("id", id);
            servicoData.put("carga", carga);
            servicoData.put("nomeMotorista", nomeMotorista);
            servicoData.put("dataHoraInicio", dataHoraInicio.toString());
            //servicoData.put("dataHoraFim", dataHoraFim.toString());

            /*
            // Adicionar os dados do veiculo em um objeto JSON aninhado
            JSONObject veiculoData = new JSONObject();
            veiculoData.put("marca", veiculo.getMarca());
            veiculoData.put("modelo", veiculo.getModelo());
            veiculoData.put("placa", veiculo.getPlaca());
            servicoData.put("veiculo", veiculoData);
*/
            // Salvar os dados no Firebase
            reference.child(String.valueOf(id)).setValue(servicoData.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    // Métodos da interface Parcelable
    protected Servico(Parcel in) {
        id = in.readInt();
        carga = in.readString();
        nomeMotorista = in.readString();
    }

    public static final Creator<Servico> CREATOR = new Creator<Servico>() {
        @Override
        public Servico createFromParcel(Parcel in) {
            return new Servico(in);
        }

        @Override
        public Servico[] newArray(int size) {
            return new Servico[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(carga);
        dest.writeString(nomeMotorista);
    }
}
