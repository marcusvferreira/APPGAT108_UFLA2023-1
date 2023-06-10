package android.marcusvferreira.appgat108.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.marcusvferreira.appgat108.view.MainActivity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Comentar acerca do código...
 */
public class ControleLocalizacao implements Runnable, LocationListener {

    private static final DecimalFormat decfor1 = new DecimalFormat("0.00"); //Converter os valores para formato decimal
    private static final DecimalFormat decfor2 = new DecimalFormat("#"); //Converter os valores para formato sem casas decimais
    private final Context context;
    private final Handler handler;
    private GoogleMap mMap;
    private boolean isOrigemObtida = false;
    private final Veiculo veiculo;
    private Thread thread;

    //Objetos para manipular os campos TextView presentes na activity main
    private final TextView campoVelocidadeMedia, campoVelocidadeRecomendada, campoLocalizacaoAtual,
            campoDistanciaTotal, campoDistanciaPercorrida, campoConsumo;

    //Construtor
    public ControleLocalizacao(Activity activity, Context context, Veiculo veiculo, Handler handler) {
        this.context = context;
        this.veiculo = veiculo;
        this.handler = handler;

        TextView campoLocalizacaoDestino = activity.findViewById(R.id.tv_loc_destino_dados);
        campoLocalizacaoAtual = activity.findViewById(R.id.tv_loc_atual_dados);
        campoVelocidadeMedia = activity.findViewById(R.id.tv_velocidade_media);
        campoVelocidadeRecomendada = activity.findViewById(R.id.tv_velocidade_recomendada);
        campoDistanciaTotal = activity.findViewById(R.id.tv_distancia_total);
        campoDistanciaPercorrida = activity.findViewById(R.id.tv_distancia_percorrida);
        campoConsumo = activity.findViewById(R.id.tv_consumo);

        //Configura o destino como sendo no final da Av. Norte da UFLA
        veiculo.getDestino().setLatitude(-21.222635);
        veiculo.getDestino().setLongitude(-44.970962);
        String textoDestino = "Latitude: " + veiculo.getDestino().getLatitude() + "\nLongitude: "
                + veiculo.getDestino().getLongitude();
        campoLocalizacaoDestino.setText(textoDestino);
        this.thread = new Thread(this); // instancia a thread
        this.thread.start();
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        Looper.prepare();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }
        Looper.loop();
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

        Thread thread = new Thread(new Processamento(location, veiculo, handler, this));
        thread.start();
    }

    public void atualizarLocalizacao(double latitude, double longitude) {
        String textoVelocidadeRecomendada = "Recomendada\n" + decfor1.format(veiculo.getVelociddadeRecomendada()) + " km/h";
        String textoConsumo = "Consumo\n" + decfor1.format(veiculo.getConsumo()) + " L";
        String textoLocalizacao = "Latitude: " + latitude + "\nLongitude: " + longitude;
        String textoVelocidadeMedia = "Média\n" + decfor1.format(veiculo.getVelocidadeMedia()) + " km/h";
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
        campoConsumo.setText(textoConsumo);

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
