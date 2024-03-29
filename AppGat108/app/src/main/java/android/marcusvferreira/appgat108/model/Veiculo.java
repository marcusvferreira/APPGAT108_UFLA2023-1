package android.marcusvferreira.appgat108.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * A classe Veiculo representa um veículo para o aplicativo desenvolvido. Ela contém atributos que identificam e armazenam
 * as características do veículo. Esses atributos incluem a distância percorrida, a distância total, o tempo transcorrido,
 * o tempo desejado, a velocidade recomendada, a velocidade média e o modelo do veículo. Além disso, a classe mantém uma lista
 * de velocidades instantâneas obtidas pelo GPS. Os objetos Veiculo também armazenam a localização de origem e destino do veículo.
 * A classe fornece métodos para calcular a velocidade média do veículo com base nas velocidades instantâneas, bem como calcular
 * o consumo do veículo com base no modelo e na velocidade média durante o percurso.
 */
public class Veiculo {

    // Atributos da classe Veículo
    private double distanciaPercorrida, distanciaTotal, tempoTranscorrido, tempoDesejado,
            velocidadeRecomendada, velocidadeMedia;
    private final List<Double> velocidadesInstantaneas = new ArrayList<>(); // Armazena as velocidades instantâneas obtidas pelo GPS
    private String modelo; // Modelo do veículo
    private final Location origem, destino; // Armazenam a origem e o destino do veículo instanciado
    private boolean verificadorCrossDocking = false;

    // O construtor instancia dois objetos Location (origem e destino) inicialmente vazio, pois serão definidos em ControleLocalizacao
    public Veiculo() {
        origem = new Location("");
        destino = new Location("");
    }

    // Métodos getters e setters
    public double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(double distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(double distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }

    public double getTempoTranscorrido() {
        return tempoTranscorrido;
    }

    public void setTempoTranscorrido(double tempoTranscorrido) {
        this.tempoTranscorrido = tempoTranscorrido;
    }

    public double getTempoDesejado() {
        return tempoDesejado;
    }

    public void setTempoDesejado(double tempoDesejado) {
        this.tempoDesejado = tempoDesejado;
    }

    public double getVelocidadeRecomendada() {
        return velocidadeRecomendada;
    }

    public void setVelocidadeRecomendada(double velocidadeRecomendada) {
        this.velocidadeRecomendada = velocidadeRecomendada;
    }

    public double getVelocidadeMedia() {
        return velocidadeMedia;
    }

    public boolean isVerificadorCrossDocking() {
        return verificadorCrossDocking;
    }

    public void setVerificadorCrossDocking(boolean verificadorCrossDocking) {
        this.verificadorCrossDocking = verificadorCrossDocking;
    }

    /**
     * Método que retorna a velocidade atual do veículo.
     * A velocidade atual é obtida a partir das velocidades instantâneas armazenadas na lista 'velocidadesInstantaneas'.
     * Se a lista estiver vazia, significa que não há registros de velocidades, portanto retorna 0.
     * Caso contrário, retorna a última velocidade registrada na lista.
     *
     * @return A velocidade atual do veículo.
     */
    public double getVelocidadeAtual() {
        if (velocidadesInstantaneas.isEmpty()) {
            return 0;
        } else {
            return velocidadesInstantaneas.get(velocidadesInstantaneas.size() - 1);
        }
    }

    public List<Double> getVelocidadesInstantaneas() {
        return velocidadesInstantaneas;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Location getOrigem() {
        return origem;
    }

    public Location getDestino() {
        return destino;
    }

    /**
     * Calcula a velocidade média do veículo com base nas velocidades instantâneas obtidas.
     * Se a lista de velocidades instantâneas estiver vazia, a velocidade média é definida como zero.
     * Caso contrário, itera sobre a lista de velocidades instantâneas, soma os valores e calcula a média.
     */
    public void calcularVelocidadeMedia() {
        if (velocidadesInstantaneas.isEmpty()) {
            velocidadeMedia = 0;
        } else {
            float somaVelocidades = 0;
            for (double velocidade : velocidadesInstantaneas) {
                somaVelocidades += velocidade;
            }
            velocidadeMedia = somaVelocidades / velocidadesInstantaneas.size();
        }
    }

    /**
     * Cálculo para o consumo conforme matéria "O teste da relação entre velocidade e consumo". O consumo apresentado
     * depende do modelo e da velocidade média durante o percurso.
     * Disponível em: https://quatrorodas.abril.com.br/auto-servico/o-teste-da-relacao-entre-velocidade-e-consumo/
     * consumo [L] = distanciaPercorrida [km] / kmPorLitro
     */
    public double getConsumo() {
        switch (modelo) {
            case "Fox":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 21.1;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 15.6;
                else return (distanciaPercorrida / 1000) / 10.4;
            case "Siena":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 14.6;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 12.9;
                else return (distanciaPercorrida / 1000) / 8.8;
            case "Fusion":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 21.6;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 15.4;
                else return (distanciaPercorrida / 1000) / 10.1;
            case "Azera":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 16.5;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 13.3;
                else return (distanciaPercorrida / 1000) / 10.5;
            default:
                return 0;
        }
    }
}
