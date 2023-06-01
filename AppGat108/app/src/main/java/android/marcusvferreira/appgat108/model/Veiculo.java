package android.marcusvferreira.appgat108.model;

public class Veiculo {
    private double velocidade, autonomia, consumo, tempoTranscorrido, tempoDesejeado, distanciaPercorrida;
    private String modelo;

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public double getAutonomia() {
        return autonomia;
    }

    public void setAutonomia(double autonomia) {
        this.autonomia = autonomia;
    }

    public double getConsumo() {
        return consumo;
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

    public double getTempoDesejeado() {
        return tempoDesejeado;
    }

    public void setTempoDesejeado(double tempoDesejeado) {
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
