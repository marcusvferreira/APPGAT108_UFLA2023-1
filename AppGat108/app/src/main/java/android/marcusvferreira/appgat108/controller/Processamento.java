package android.marcusvferreira.appgat108.controller;

import android.location.Location;
import android.marcusvferreira.appgat108.model.Servico;
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
    private final Servico servico;
    private final ControleServico controleServico;
    private final Medidor medidores;

    /**
     * Construtor da classe Processamento.
     *
     * @param location            A localização atual.
     * @param veiculo             O veículo associado à localização.
     * @param servico             O objeto de serviço.
     * @param controleServico     O objeto ControleServico responsável pela escrita e leitura dos dados.
     * @param medidores           O objeto Medidor utilizado para calcular a velocidade recomendada.
     * @param handler             O manipulador para a interface principal.
     * @param controleLocalizacao O objeto ControleLocalizacao responsável pela atualização da interface.
     */
    public Processamento(Location location, Veiculo veiculo, Servico servico, ControleServico controleServico, Medidor medidores, Handler handler, ControleLocalizacao controleLocalizacao) {
        this.location = location;
        this.veiculo = veiculo;
        this.handler = handler;
        this.controleLocalizacao = controleLocalizacao;
        this.medidores = medidores;
        this.servico = servico;
        this.controleServico = controleServico;
    }

    /**
     * Método run da interface Runnable. Realiza o processamento necessário com base na localização atual e atualiza a interface.
     */
    @Override
    public void run() {
        // Calcula a distância percorrida (origem até a localização atual)
        veiculo.setDistanciaPercorrida(location.distanceTo(veiculo.getOrigem()));

        // Calcula a velocidade recomendada
        medidores.atualizarMedicao();

        // Escrever o objeto Servico no Firebase
        controleServico.escrever(servico);

        controleServico.ler((distanciaRestanteOutroVeiculo, tempoRestanteOutroVeiculo, velocidadeOutroVeiculo, verificadorCrossDockingOutroVeiculo) -> {
            double tempoRestanteMeuVeiculo = veiculo.getTempoDesejado() - veiculo.getTempoTranscorrido() / 3600;
            double tempoRestanteMax = Math.max(tempoRestanteMeuVeiculo, tempoRestanteOutroVeiculo);

            if (!veiculo.isVerificadorCrossDocking() || !verificadorCrossDockingOutroVeiculo) {
                veiculo.setTempoDesejado(tempoRestanteMax);
                veiculo.setVerificadorCrossDocking(true);
            }

            if (veiculo.getVelocidadeRecomendada() > 100 || velocidadeOutroVeiculo > 100) {
                veiculo.setTempoDesejado(tempoRestanteMax + (3.0 / 60)); // add 3min ao tempo
                veiculo.setVerificadorCrossDocking(false);
            }
        }, servico);

        // Calcula a velocidade média durante o percurso
        double velocidade = location.getSpeed();
        veiculo.getVelocidadesInstantaneas().add(velocidade);
        veiculo.calcularVelocidadeMedia();

        // Distância restante em metros
        double distanciaRestante = (veiculo.getDistanciaTotal() - veiculo.getDistanciaPercorrida());

        // Armazena os parâmetros latitude e longitude para atualizar o campo localização atual
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Posta uma Runnable no Handler para atualizar a interface principal
        handler.post(() -> controleLocalizacao.atualizarLocalizacao(latitude, longitude, distanciaRestante));
    }
}
