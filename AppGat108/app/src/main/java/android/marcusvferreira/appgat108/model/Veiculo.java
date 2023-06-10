package android.marcusvferreira.appgat108.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Comentar acerca do código...
 */
public class Veiculo {

    //Atributos da classe Veículo
    private double distanciaPercorrida, distanciaTotal, tempoTranscorrido, tempoDesejeado,
            velociddadeRecomendada, velocidadeMedia;
    private List<Double> velocidadesInstantaneas = new ArrayList<>(); //Armazena as velocidades instantâneas obtidas pelo GPS
    private String modelo; //Modelo do veículo
    private final Location origem, destino; //Armazenam a origem e o destino do veículo instanciado

    //O construtor instancia dois objetos Location (origem e destino) inicialmente vazio, pois serão
    //definidos em ControleLocalizacao
    public Veiculo() {
        origem = new Location("");
        destino = new Location("");


    }


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

    public double getTempoDesejeado() {
        return tempoDesejeado;
    }

    public void setTempoDesejeado(double tempoDesejeado) {
        this.tempoDesejeado = tempoDesejeado;
    }

    public double getVelociddadeRecomendada() {
        return velociddadeRecomendada;
    }

    public void setVelociddadeRecomendada(double velociddadeRecomendada) {
        this.velociddadeRecomendada = velociddadeRecomendada;
    }

    public double getVelocidadeMedia() {
        return velocidadeMedia;
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

    //Cálculo para o consumo conforme matéria "O teste da relação entre velocidade e consumo". O consumo apresentado
    //depende do modelo e da velocidade média durante o percurso.
    //Disponível em: https://quatrorodas.abril.com.br/auto-servico/o-teste-da-relacao-entre-velocidade-e-consumo/
    public double getConsumo() {
        // consumo = distanciaPercorrida / kmPorLitro;
        switch (modelo) {
            case "Fox":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 21.1;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 15.6;
                else return 10.4;
            case "Siena":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 14.6;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 12.9;
                else return 8.8;
            case "Fusion":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 21.6;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 15.4;
                else return 10.1;
            case "Azera":
                if (velocidadeMedia <= 0) return 0;
                else if (velocidadeMedia <= 80) return (distanciaPercorrida / 1000) / 16.5;
                else if (velocidadeMedia <= 100) return (distanciaPercorrida / 1000) / 13.3;
                else return 10.5;
            default:
                return 0;
        }
    }


}
