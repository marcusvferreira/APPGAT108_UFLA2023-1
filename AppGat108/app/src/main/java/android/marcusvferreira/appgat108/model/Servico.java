package android.marcusvferreira.appgat108.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

public class Servico implements Parcelable {
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
