package android.marcusvferreira.appgat108.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

/**
 * A classe Servico representa um serviço relacionado a um veículo e a um motorista,
 * contendo informações como ID do serviço, tipo de carga, nome do motorista, horário de início
 * e término do serviço, e uma referência ao veículo associado. Além disso, a classe implementa
 * a interface Parcelable, permitindo que seja facilmente serializada e transmitida entre
 * componentes do Android através de Intents. Ela fornece métodos para obter e definir seus
 * atributos, bem como construtores para recriar o objeto a partir de um Parcel.
 */
public class Servico implements Parcelable {

    // Atributos da classe
    private final int id;
    private final String carga, nomeMotorista;
    private LocalDateTime dataHoraInicio, dataHoraFim;
    private Veiculo veiculo;

    // Construtor da classe Servico
    public Servico(int id, String carga, String nomeMotorista) {
        this.id = id;
        this.carga = carga;
        this.nomeMotorista = nomeMotorista;
    }

    // Getters e setters dos atributos da classe
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

    // Construtor para recriar a classe a partir do Parcel
    protected Servico(Parcel in) {
        id = in.readInt();
        carga = in.readString();
        nomeMotorista = in.readString();
    }

    // Creator para criar a classe a partir do Parcel
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

    // Método para escrever os atributos da classe no Parcel
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(carga);
        dest.writeString(nomeMotorista);
    }
}
