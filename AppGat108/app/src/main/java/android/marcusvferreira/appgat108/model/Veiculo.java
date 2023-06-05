package android.marcusvferreira.appgat108.model;

import android.location.Location;

/**
 * Comentar acerca do código...
 */
public class Veiculo {
    //Atributos
    private double velocidade, autonomia, consumo, distanciaPercorrida, tempoTranscorrido;;
    private int tempoDesejeado;
    private String modelo;
    private final Location origem, destino;

    public Veiculo(){ //Construtor
        origem =  new Location("");
        destino =  new Location("");
    }

    public double getAutonomia() {
        return autonomia;
    }

    public void setAutonomia(double autonomia) {
        this.autonomia = autonomia;
    }

    public Location getOrigem() {
        return origem;
    }

    public Location getDestino() {
        return destino;
    }

    //Métodos
    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public double getConsumo() {
        return getConsumoMedio(velocidade);
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }

    public double getTempoTranscorrido() {
        return tempoTranscorrido;
    }

    public void setTempoTranscorrido(double tempoTranscorrido) {
        this.tempoTranscorrido = tempoTranscorrido;
    }

    public int getTempoDesejeado() {
        return tempoDesejeado;
    }

    public void setTempoDesejeado(int tempoDesejeado) {
        this.tempoDesejeado = tempoDesejeado;
    }

    public double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(double distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    //Cálculo para o consumo conforme matéria "O teste da relação entre velocidade e consumo"
    //Disponível em: https://quatrorodas.abril.com.br/auto-servico/o-teste-da-relacao-entre-velocidade-e-consumo/
    double getConsumoMedio(double velocidade) {
        switch (this.modelo) {
            case "Fox":
                if (velocidade <= 0) return 0;
                else if (velocidade <= 80) return 21.1;
                else if (velocidade <= 100) return 15.6;
                else return 10.4;
            case "Siena":
                if (velocidade <= 0) return 0;
                else if (velocidade <= 80) return 14.6;
                else if (velocidade <= 100) return 12.9;
                else return 8.8;
            case "Fusion":
                if (velocidade <= 0) return 0;
                else if (velocidade <= 80) return 21.6;
                else if (velocidade <= 100) return 15.4;
                else return 10.1;
            case "Azera":
                if (velocidade <= 0) return 0;
                else if (velocidade <= 80) return 16.5;
                else if (velocidade <= 100) return 13.3;
                else return 10.5;
            default:
                return 0;
        }
    }
}

//add a uma lista e pegar a cada 100m