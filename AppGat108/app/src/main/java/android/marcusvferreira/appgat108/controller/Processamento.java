package android.marcusvferreira.appgat108.controller;

import android.location.Location;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.os.Handler;

/**
 * A classe `Processamento` é responsável por executar o processamento dos dados de localização
 * e atualizar a interface de usuário com as informações relevantes do veículo.
 * Ela implementa a interface `Runnable`, o que permite que seja executada em uma thread separada.
 * O processamento é realizado no método `run()`, onde são calculadas informações como distância percorrida,
 * velocidade média, velocidade recomendada e atualização da localização atual.
 * Os resultados do processamento são enviados para a interface de usuário por meio de um `Handler`,
 * que posta uma ação assíncrona para atualizar os elementos da interface na thread principal.
 * A classe possui uma referência ao objeto `Veiculo` associado à localização e ao objeto `ControleLocalizacao`
 * para chamar o método de atualização da interface.
 */
public class Processamento implements Runnable {
    private final Location location;
    private final Veiculo veiculo;
    private final ControleLocalizacao controleLocalizacao;
    private final Handler handler;

    /**
     * Construtor da classe Processamento.
     *
     * @param location            A localização atual.
     * @param veiculo             O veículo associado à localização.
     * @param handler             O manipulador para a interface principal.
     * @param controleLocalizacao O objeto ControleLocalizacao responsável pela atualização da interface.
     */
    public Processamento(Location location, Veiculo veiculo, Handler handler, ControleLocalizacao controleLocalizacao) {
        this.location = location;
        this.veiculo = veiculo;
        this.handler = handler;
        this.controleLocalizacao = controleLocalizacao;
    }

    /**
     * Método run da interface Runnable. Realiza o processamento necessário com base na localização atual e atualiza a interface.
     */
    @Override
    public void run() {
        // Calcula a distância percorrida (origem até a localizaçãoa atual)
        veiculo.setDistanciaPercorrida(location.distanceTo(veiculo.getOrigem()));

        // Calcula a velocidade média durante o percurso
        double velocidade = location.getSpeed();
        veiculo.getVelocidadesInstantaneas().add(velocidade);
        veiculo.calcularVelocidadeMedia();

        // Calcula a velocidade recomendada
        double tempoDesejado = veiculo.getTempoDesejado(); // Tempo desejado já é retornado em hrs
        double tempoTranscorrido = veiculo.getTempoTranscorrido() / 3600; // Tempo transcorrido é retornado em seg, portanto o converte para hrs
        double tempoRestante = tempoDesejado - tempoTranscorrido;
        double distanciaRestante = (veiculo.getDistanciaTotal() - veiculo.getDistanciaPercorrida()); // Distância restante em metros
        /*
        double velocidadeRecomendada = (distanciaRestante / 1000) / tempoRestante;
        if (velocidadeRecomendada > 0) {
            veiculo.setVelociddadeRecomendada(velocidadeRecomendada);
        } else {
            veiculo.setVelociddadeRecomendada(0.0);
        }
*/

        // Armazena os parâmetros latitude e longitude para atualizar o campo localização atual
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Posta uma Runnable no Handler para atualizar a interface principal
        handler.post(() -> controleLocalizacao.atualizarLocalizacao(latitude, longitude, distanciaRestante));
    }
}
