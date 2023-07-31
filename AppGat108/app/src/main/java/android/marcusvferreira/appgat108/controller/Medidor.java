package android.marcusvferreira.appgat108.controller;

import android.marcusvferreira.appgat108.model.Veiculo;

import java.util.Arrays;
import java.util.Random;

/**
 * Classe Medidor é responsável por realizar medições relacionadas ao veículo, incluindo a
 * velocidade recomendada e a variância da medição. Utiliza a técnica de reconciliação de dados
 * para calcular a velocidade recomendada com base em medições parciais realizadas em intervalos
 * de distância específicos. A classe monitora o veículo e atualiza suas medições periodicamente.
 */
public class Medidor {

    // Número de medidores inicial
    private static int numMedidores = 10;

    // Distância entre os medidores
    private double distanciaMedidores;

    // Número constante de medidores iniciais
    private final int CONST_NUM_MEDIDORES_INICIAIS = numMedidores;

    // Tempo transcorrido entre os medidores
    private double tempoTranscorridoEntreMedidores;

    // Objeto Veiculo associado ao Medidor
    private final Veiculo veiculo;

    // Objeto Reconciliation para a reconciliação de dados
    private Reconciliation rec;

    // Arrays para os cálculos da reconciliação
    double[] y = new double[numMedidores + 1];
    double[] v = new double[numMedidores + 1];
    double[][] A = new double[1][numMedidores + 1];
    double[] aux = new double[numMedidores]; // array auxiliar

    // Construtor da classe Medidor
    public Medidor(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.iniciarMedidor();
    }

    // Inicializa os parâmetros do Medidor
    public void iniciarMedidor() {
        this.distanciaMedidores = veiculo.getDistanciaTotal() / CONST_NUM_MEDIDORES_INICIAIS;
        preencherV();
        preencherA();
        preencherY();
        preencherAux();
    }

    // Preenche o array Y com valores de tempo para a reconciliação
    void preencherY() {
        for (int i = 0; i < y.length; i++) {
            if (i == y.length - 1) {
                y[i] = veiculo.getTempoDesejado() - veiculo.getTempoTranscorrido() / 3600;
            } else {
                y[i] = (veiculo.getTempoDesejado() - veiculo.getTempoTranscorrido() / 3600) / (y.length - 1);
            }
        }
    }

    // Preenche o array V com valores de variância
    void preencherV() {
        Random random = new Random();
        for (int i = 0; i < v.length; i++) {
            v[i] = random.nextDouble() * 0.005; // Gerando um número aleatório entre 0 e 0.005 para simular a variância
        }
    }

    // Preenche o array A com valores 1 e -1 para a reconciliação
    void preencherA() {
        for (int i = 0; i < numMedidores; i++) {
            A[0][i] = 1;
        }
        A[0][numMedidores] = -1; // Preenche a última posição com -1
    }

    // Preenche o array auxiliar com valores de distância em que cada medidor está localizado
    void preencherAux() {
        for (int i = 0; i < aux.length; i++) {
            aux[i] = distanciaMedidores * (i + 1);
        }
    }

    // Obtém a velocidade recomendada pelo Medidor utilizando a reconciliação de dados
    private double getVelocidadeRecomendada() {
        rec = new Reconciliation(y, v, A);
        return (distanciaMedidores / 1000) / rec.getReconciledFlow()[0]; // Cálculo da velocidade pela reconciliação de dados
    }

    // Atualiza a medição do Medidor
    void atualizarMedicao() {
        if (veiculo.getDistanciaPercorrida() > aux[aux.length - numMedidores] && numMedidores > 0) {
            numMedidores -= 1;
            recalcularVariancia();
            A = new double[1][numMedidores + 1];
            preencherA();
            y = new double[numMedidores + 1];
            preencherY();
        }
        iniciarMedidor();
        veiculo.setVelocidadeRecomendada(this.getVelocidadeRecomendada());
    }

    // Recalcula a variância do Medidor
    // ((Medida - valorReconciliado)/Incerteza)²
    private void recalcularVariancia() {
        tempoTranscorridoEntreMedidores = veiculo.getTempoTranscorrido() - tempoTranscorridoEntreMedidores;
        double INCERTEZA = 0.01; // Incerteza da medição padronizada como 1%
        double variancia = Math.pow((tempoTranscorridoEntreMedidores - rec.getReconciledFlow()[0]) / INCERTEZA, 2);
        v = Arrays.copyOfRange(v, 1, v.length);
        for (int i = 0; i < numMedidores; i++) {
            v[i] += variancia / numMedidores;
        }
    }
}
