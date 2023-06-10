package android.marcusvferreira.appgat108.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.marcusvferreira.appgat108.view.MainActivity;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

/**
 * Comentar acerca do código...
 */
public class ControleLocalizacao implements Runnable, LocationListener {

    private static final DecimalFormat decfor1 = new DecimalFormat("0.00"); //Converter os valores para formato decimal
    private static final DecimalFormat decfor2 = new DecimalFormat("#"); //Converter os valores para formato sem casas decimais
    private final Context context;
    private final MainActivity activity;
    private final Handler handler;
    private GoogleMap mMap;
    private boolean isOrigemObtida = false;
    private final Veiculo veiculo;

    //Objetos para manipular os campos TextView presentes na activity main
    private final TextView campoVelocidadeMedia, campoVelocidadeRecomendada, campoVelocidadeAtual,
            campoLocalizacaoAtual, campoDistanciaTotal, campoDistanciaPercorrida, campoConsumo;

    //Construtor
    public ControleLocalizacao(MainActivity activity, Context context, Veiculo veiculo, Handler handler) {
        this.context = context;
        this.veiculo = veiculo;
        this.handler = handler;
        this.activity = activity;

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

        Thread thread = new Thread(this); // instancia a thread
        thread.start();
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {

       if(activity.isTimerIniciado){
            Looper.prepare();
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
            }
            Looper.loop();



       }


    }

    //Método responsável por atualizar os dados qnd haver mudança na localização
    @Override
    public void onLocationChanged(Location location) {
        // Verifica se a origem ainda não foi definida. Armazena a primeira localização lida como origem
        // e calcula a distância total do percurso
        if (!isOrigemObtida) {
            isOrigemObtida = true;
            veiculo.getOrigem().setLatitude(location.getLatitude());
            veiculo.getOrigem().setLongitude(location.getLongitude());
            veiculo.setDistanciaTotal(location.distanceTo(veiculo.getDestino()));
        }

        if(activity.isTimerIniciado) {
            Thread thread = new Thread(new Processamento(location, veiculo, handler, this));
            thread.start();
        }
/*
        }else{
            Toast.makeText(activity, "Você chegou ao destino!", Toast.LENGTH_LONG).show();
        }*/
    }

    public void atualizarLocalizacao(double latitude, double longitude, double distanciaRestante) {

       // if(distanciaRestante <=0.0001){
       //     activity.pausarTimer();
       // }


        String textoVelocidadeRecomendada = "Recomendada\n" + decfor1.format(veiculo.getVelociddadeRecomendada()) + " km/h";
        String textoVelocidadeMedia = "Média\n" + decfor1.format(veiculo.getVelocidadeMedia()) + " km/h";
        String textoVelocidadeAtual = "Atual\n" + decfor1.format(veiculo.getVelocidadeAtual()) + " km/h";
        String textoConsumo = "Consumo\n" + decfor1.format(veiculo.getConsumo()) + " L";
        String textoLocalizacao = "Latitude: " + latitude + "\nLongitude: " + longitude;
        String textoDistanciaPercorrida, textoDistanciaTotal;

        if (veiculo.getDistanciaPercorrida() > 1000) { //Se maior que 1000m, exibe a distância em km
            textoDistanciaPercorrida = "Percorrida\n" + decfor1.format(veiculo.getDistanciaPercorrida()  / 1000) + " km";
        } else {
            textoDistanciaPercorrida = "Percorrida\n" + decfor2.format(veiculo.getDistanciaPercorrida() ) + " m";
        }

        if (veiculo.getDistanciaTotal() > 1000) { //Se maior que 1000m, exibe a distância em km
            textoDistanciaTotal = "Total\n" + decfor1.format(veiculo.getDistanciaTotal() / 1000) + " km";
        } else {
            textoDistanciaTotal = "Total\n" + decfor2.format(veiculo.getDistanciaTotal()) + " m";
        }

        //Atualiza os campos Text View na activity main
        campoLocalizacaoAtual.setText(textoLocalizacao);
        campoDistanciaPercorrida.setText(textoDistanciaPercorrida);
        campoDistanciaTotal.setText(textoDistanciaTotal);
        campoVelocidadeMedia.setText(textoVelocidadeMedia);
        campoVelocidadeRecomendada.setText(textoVelocidadeRecomendada);
        campoVelocidadeAtual.setText(textoVelocidadeAtual);
        campoConsumo.setText(textoConsumo);
        if(veiculo.getVelocidadeAtual() < veiculo.getVelociddadeRecomendada()){
            campoVelocidadeAtual.setTextColor(Color.RED);
        }

        //Adiciona pin marker no mapa
        if (mMap != null) {
            mMap.clear(); //Limpa marcadores

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
