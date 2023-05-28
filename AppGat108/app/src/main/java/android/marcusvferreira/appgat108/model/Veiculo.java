package android.marcusvferreira.appgat108.model;

public class Veiculo {
    private double velocidade, autonomia, consumo, tempoTranscorrido, tempoDesejeado, distanciaPercorrida;
    private Local destino;

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

    public Local getDestino() {
        return destino;
    }

    public void setDestino(Local destino) {
        this.destino = destino;
    }

    //Cálculo para Fox 1.0 três cilindros
    //Leia mais em: https://quatrorodas.abril.com.br/auto-servico/o-teste-da-relacao-entre-velocidade-e-consumo/
    double getConsumoMedio(double velocidade){
        if(velocidade <= 0) return 0;
        else if(velocidade <= 80) return 21.1;
        else if(velocidade <= 100) return 15.6;
        else return 10.4;
    }

    // ao inves do campo autonomia, fazer uma lista com os veiculos mencionados na materia
    /*utilize a Reconciliação de Dados para determinar qual é a melhor velocidade para
cada um dos pontos de passagem do Veículo. Como vimos, a velocidade e o tempo
interferem no consumo.
    * */


//=;
//= -44.974912;
// try catch
}
