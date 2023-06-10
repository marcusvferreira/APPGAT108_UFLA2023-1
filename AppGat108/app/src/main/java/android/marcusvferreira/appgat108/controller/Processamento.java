package android.marcusvferreira.appgat108.controller;

import android.location.Location;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.os.Handler;

public class Processamento implements Runnable {
    private final Location location;
    private final Veiculo veiculo;
    private final ControleLocalizacao controleLocalizacao;
    private final Handler handler;

    public Processamento(Location location, Veiculo veiculo, Handler handler, ControleLocalizacao controleLocalizacao) {
        this.location = location;
        this.veiculo = veiculo;
        this.handler = handler;
        this.controleLocalizacao = controleLocalizacao;
    }

    @Override
    public void run() {
        //Calcula a distância percorrida (origem até a localizaçãoa atual)
        veiculo.setDistanciaPercorrida(location.distanceTo(veiculo.getOrigem()));

        //Calcula a velocidade média durante o percurso
        double velocidade = location.getSpeed();
        veiculo.getVelocidadesInstantaneas().add(velocidade);
        veiculo.calcularVelocidadeMedia();

        //Calcula a velocidade recomendada
        double tempoDesejado = veiculo.getTempoDesejeado(); //Tempo desejado já é retornado em hrs
        double tempoTranscorrido = veiculo.getTempoTranscorrido() / 3600; //Tempo transcorrido é retornado em seg, portanto o converte para hrs
        double tempoRestante = tempoDesejado - tempoTranscorrido;
        double distanciaRestante = (veiculo.getDistanciaTotal() - veiculo.getDistanciaPercorrida()) / 1000; //Distância restante em metros
        veiculo.setVelociddadeRecomendada(distanciaRestante / tempoRestante);

        //Armazena os parâmetros latitude e longitude para atualizar o campo localização atual
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Postar uma Runnable no Handler para atualizar a interface principal
        handler.post(new Runnable() {
            @Override
            public void run() {
                controleLocalizacao.atualizarLocalizacao(latitude, longitude);
            }
        });
    }


}
