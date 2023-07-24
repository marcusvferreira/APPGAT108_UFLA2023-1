package android.marcusvferreira.appgat108.controller;

import android.marcusvferreira.appgat108.model.Veiculo;

import java.util.Arrays;
import java.util.Random;

public class Medidor {

    //double distanciaRestante = (veiculo.getDistanciaTotal() - veiculo.getDistanciaPercorrida()); // Distância restante em metros
    private static int numMedidores = 10;
    private double distanciaMedidores;
    private Veiculo veiculo;
    //= veiculo.getDistanciaTotal()/numMedidores; //distâcia entre medidores
    //= veiculo.getTempoDesejado(); // Tempo desejado já é retornado em hrs

    // arrays para o cálculo de reconciliação
    double[] y = new double[numMedidores + 1];
    double[] v = new double[numMedidores + 1];
    double[][] A = new double[1][numMedidores + 1];
    double[] aux = new double[numMedidores]; // array auxiliar

    public Medidor(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.iniciarMedidor();
    }

    public void iniciarMedidor(){

        this.distanciaMedidores = veiculo.getDistanciaTotal()/10;
        preencherV();
        preencherA();
        preencherY();
        preencherAux();
    }

    void preencherY(){
        for (int i = 0; i < y.length; i++) {
            if (i == y.length - 1) {
                y[i] = veiculo.getTempoDesejado() - veiculo.getTempoTranscorrido()/ 3600;
            } else {
                y[i] = (veiculo.getTempoDesejado() - veiculo.getTempoTranscorrido()/ 3600) / (y.length - 1);
            }
        }
    }

    void preencherV(){
        Random random = new Random();
        for (int i = 0; i < v.length; i++) {
            v[i] = random.nextDouble() * 0.005; //Gerando um número aleatório entre 0 e 0.005 para simular a variância
        }
    }

    void preencherA(){
        for (int i = 0; i < numMedidores; i++) {
            A[0][i] = 1;
        }
        A[0][numMedidores] = -1; // Preenche a última posição com -1
    }

    void preencherAux(){
        for (int i = 0; i < aux.length; i++) {
            aux[i] = distanciaMedidores*(i+1);
        }
    }

    double getVelocidadeRecomendada(){
        Reconciliation rec = new Reconciliation(y, v, A);
     /*   double somador = 0;
        for (double tempo:rec.getReconciledFlow()) {
            somador+=tempo;
        }*/

        return (distanciaMedidores/1000)/rec.getReconciledFlow()[0]; // cálculo da velocidade pela reconciliação de dados
    }

    void atualizarMedicao(){
        if(veiculo.getDistanciaPercorrida() > aux[aux.length-numMedidores] && numMedidores>0){
            numMedidores -= 1;
            //aux = Arrays.copyOfRange(aux, 1, aux.length);
            A = new double[1][numMedidores + 1];
            preencherA();
            y = new double[numMedidores+1];
            preencherY();
            v = Arrays.copyOfRange(v, 1, v.length);
            //preencherV();
        }
        iniciarMedidor();
        veiculo.setVelociddadeRecomendada(this.getVelocidadeRecomendada());
       // veiculo.setDistanciaPercorrida(numMedidores);
    }

}
