package android.marcusvferreira.appgat108.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.model.Servico;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.marcusvferreira.appgat108.view.MainActivity;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

/**
 * Controla a obtenção e atualização da localização do veículo.
 */
public class ControleLocalizacao implements Runnable, LocationListener {

    // Formato do numerais para exibição dos valores
    private static final DecimalFormat decfor1 = new DecimalFormat("0.00"); // Converter os valores para formato decimal
    private static final DecimalFormat decfor2 = new DecimalFormat("#"); // Converter os valores para formato sem casas decimais

    // Contexto e activity relacionados à localização
    private final Context context;
    private final MainActivity activity;
    //public Thread thread;

    private final Handler handler; // Manipulador da interface de usuário
    private GoogleMap mMap;
    private boolean isOrigemObtida = false; // Indica se a origem foi obtida
    private final Veiculo veiculo;
    private final Servico servico;

    // Criar uma instância de ControleServico
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("servicos");
    ControleServico controleServico = new ControleServico(databaseReference);

    private Medidor medidores;


    // Campos TextView na interface de usuário
    private final TextView campoVelocidadeMedia, campoVelocidadeRecomendada, campoVelocidadeAtual,
            campoLocalizacaoAtual, campoDistanciaTotal, campoDistanciaPercorrida, campoConsumo;

    /**
     * Construtor da classe.
     *
     * @param activity  Atividade principal
     * @param context   Contexto da aplicação
     * @param veiculo   Objeto do veículo
     * @param medidores
     * @param handler   Manipulador da interface de usuário
     * @param servico   Objeto servico
     */
    public ControleLocalizacao(MainActivity activity, Context context, Veiculo veiculo, Medidor medidores, Handler handler, Servico servico) {
        this.context = context;
        this.veiculo = veiculo;
        this.handler = handler;
        this.activity = activity;
        this.servico = servico;
        this.medidores = medidores;

        // Inicializa os campos TextView na main activity
        TextView campoLocalizacaoDestino = activity.findViewById(R.id.tv_loc_destino_dados);
        campoLocalizacaoAtual = activity.findViewById(R.id.tv_loc_atual_dados);
        campoVelocidadeMedia = activity.findViewById(R.id.tv_velocidade_media);
        campoVelocidadeRecomendada = activity.findViewById(R.id.tv_velocidade_recomendada);
        campoVelocidadeAtual = activity.findViewById(R.id.tv_velocidade_atual);
        campoDistanciaTotal = activity.findViewById(R.id.tv_distancia_total);
        campoDistanciaPercorrida = activity.findViewById(R.id.tv_distancia_percorrida);
        campoConsumo = activity.findViewById(R.id.tv_consumo);

        //Configura o destino como sendo no final da Av. Norte da UFLA
        veiculo.getDestino().setLatitude(-21.222635);
        veiculo.getDestino().setLongitude(-44.970962);
        String textoDestino = "Latitude: " + veiculo.getDestino().getLatitude() + "\nLongitude: "
                + veiculo.getDestino().getLongitude();
        campoLocalizacaoDestino.setText(textoDestino);
    }

    /**
     * Define o mapa do Google.
     *
     * @param mMap Mapa do Google
     */
    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    /**
     * Método executado pela thread para iniciar o controle de localização.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        if (activity.isTimerIniciado) {
            Looper.prepare();
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
            }
            Looper.loop();
        }
    }

    /**
     * Método chamado quando há uma mudança na localização do dispositivo.
     *
     * @param location Nova localização
     */
    @Override
    public void onLocationChanged(Location location) {
        // Verifica se a origem ainda não foi definida.
        // Armazena a primeira localização lida como origem e calcula a distância total do percurso
        if (!isOrigemObtida) {
            isOrigemObtida = true;
            controleServico.deletarTodosOsDados();
            veiculo.getOrigem().setLatitude(location.getLatitude());
            veiculo.getOrigem().setLongitude(location.getLongitude());
            veiculo.setDistanciaTotal(location.distanceTo(veiculo.getDestino()));
        }

        if (activity.isTimerIniciado) {
            // Inicia uma nova thread para processar os dados de localização
            Thread thread = new Thread(new Processamento(location, veiculo, medidores, handler, this));
            thread.start();


            // Escrever o objeto Servico no Firebase
            controleServico.escrever(servico);

            controleServico.ler((distanciaRestanteOutroVeiculo, tempoRestanteOutroVeiculo) -> {
                double distanciaRestante = veiculo.getDistanciaTotal() - veiculo.getDistanciaPercorrida();
                double tempoRestante = veiculo.getTempoDesejado() - veiculo.getTempoTranscorrido();

                double distanciaDesejada = Math.abs(distanciaRestante - distanciaRestanteOutroVeiculo);
                double tempoRestanteMax = Math.max(tempoRestante, tempoRestanteOutroVeiculo);

                double velocidadeDesejada = distanciaDesejada / tempoRestanteMax;
                veiculo.setVelociddadeRecomendada(velocidadeDesejada);

            }, servico);
        }
    }

    /**
     * Atualiza os dados relacionados na interface de usuário.
     *
     * @param latitude          Latitude atual
     * @param longitude         Longitude atual
     * @param distanciaRestante Distância restante até o destino
     */
    public void atualizarLocalizacao(double latitude, double longitude, double distanciaRestante) {

        // Verifica se a distância restante é menor ou igual a 15 metros, indicando que o destino foi alcançado
        // 15 metros é para tratar alguma possível imprecisão do sinal
        if (distanciaRestante <= 15) {
            activity.pausarTimer(); // Pausa o timer na MainActivity
            Toast.makeText(activity, "Chegou ao destino!", Toast.LENGTH_LONG).show();
        }
        // Verifica se o tempo transcorrido em é maior que o tempo desejado para chegar ao destino
        else if (veiculo.getTempoTranscorrido() / 3600 > veiculo.getTempoDesejado()) {
            activity.pausarTimer(); // Pausa o timer na MainActivity
            Toast.makeText(activity, "Não chegou ao destino!", Toast.LENGTH_LONG).show();
        }

        // Formatação dos dados para exibição
        String textoVelocidadeRecomendada = "Recomendada\n" + decfor1.format(veiculo.getVelociddadeRecomendada()) + " km/h";
        String textoVelocidadeMedia = "Média\n" + decfor1.format(veiculo.getVelocidadeMedia()) + " km/h";
        String textoVelocidadeAtual = "Atual\n" + decfor1.format(veiculo.getVelocidadeAtual()) + " km/h";
        String textoConsumo = "Consumo\n" + decfor1.format(veiculo.getConsumo()) + " L";
        String textoLocalizacao = "Latitude: " + latitude + "\nLongitude: " + longitude;
        String textoDistanciaPercorrida, textoDistanciaTotal;

        if (veiculo.getDistanciaPercorrida() > 1000) { //Se maior que 1000m, exibe a distância em km
            textoDistanciaPercorrida = "Percorrida\n" + decfor1.format(veiculo.getDistanciaPercorrida() / 1000) + " km";
        } else {
            textoDistanciaPercorrida = "Percorrida\n" + decfor2.format(veiculo.getDistanciaPercorrida()) + " m";
        }

        if (veiculo.getDistanciaTotal() > 1000) { //Se maior que 1000m, exibe a distância em km
            textoDistanciaTotal = "Total\n" + decfor1.format(veiculo.getDistanciaTotal() / 1000) + " km";
        } else {
            textoDistanciaTotal = "Total\n" + decfor2.format(veiculo.getDistanciaTotal()) + " m";
        }

        // Atualiza os campos TextView na MainActivity com os textos formatados
        campoLocalizacaoAtual.setText(textoLocalizacao);
        campoDistanciaPercorrida.setText(textoDistanciaPercorrida);
        campoDistanciaTotal.setText(textoDistanciaTotal);
        campoVelocidadeMedia.setText(textoVelocidadeMedia);
        campoVelocidadeRecomendada.setText(textoVelocidadeRecomendada);
        campoVelocidadeAtual.setText(textoVelocidadeAtual);
        campoConsumo.setText(textoConsumo);

        // Verifica se a velocidade atual do veículo é menor que a velocidade recomendada
        if (veiculo.getVelocidadeAtual() < veiculo.getVelociddadeRecomendada()) {
            campoVelocidadeAtual.setTextColor(Color.RED); // Altera a cor do texto da velocidade atual para vermelho
        }else{
            campoVelocidadeAtual.setTextColor(Color.BLACK); // Caso contrário a cor do texto da velocidade atual continua preto
        }

        // Limpa os marcadores existentes no mapa e adiciona um novo marcador para a posição atual e o destino
        if (mMap != null) {
            mMap.clear(); //Limpa os marcadores existentes

            //Marca a posição atual no mapa
            LatLng localizacao = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(localizacao).title("Estou aqui"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacao, 15));

            //Marca o destino atual no mapa
            LatLng destino = new LatLng(veiculo.getDestino().getLatitude(), veiculo.getDestino().getLongitude());
            mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));
        }
    }
}
