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

/**
 * Comentar acerca do código...
 */
public class ControleLocalizacao implements Runnable, LocationListener {

    private static final DecimalFormat decfor1 = new DecimalFormat("0.00"); //Converter os valores para formato decimal
    private static final DecimalFormat decfor2 = new DecimalFormat("#"); //Converter os valores para formato sem casas decimais
    private final Context context;

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    GoogleMap mMap;
    private boolean isOrigemObtida = false;
    private final Veiculo veiculo;
    double distanciaTotal;

    //Objetos para manipular os campos TextView
    private final TextView campoVelocidadeMedia, campoVelocidadeRecomendada, campoLocalizacaoAtual,
            campoDistanciaTotal, campoDistanciaPercorrida, campoConsumo;

    public ControleLocalizacao(Activity activity, Context context, Veiculo veiculo) { //Construtor
        this.context = context;
        this.veiculo = veiculo;

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
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        Looper.prepare();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
        Looper.loop();
    }

    //Método responsável por atualizar os dados ao haver mudança na localização
    @Override
    public void onLocationChanged(Location location) {

        // Verificar se a origem ainda não foi definida. Armazena a primeira localização lida como origem
        // e calcula a distância total do percurso
        if (!isOrigemObtida) {
            isOrigemObtida = true;
            veiculo.getOrigem().setLatitude(location.getLatitude());
            veiculo.getOrigem().setLongitude(location.getLongitude());
            distanciaTotal = location.distanceTo(veiculo.getDestino());
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double distanciaPercorrida = location.distanceTo(veiculo.getOrigem()); //Distância do ponto de origem até a localização atual
                double velocidade = location.getSpeed()*3.6; //Converte de m/s para km/h
                atualizarLocalizacao(latitude, longitude, distanciaPercorrida, velocidade);
            }
        });
    }

    private void atualizarLocalizacao(double latitude, double longitude, double distanciaPercorrida, double velocidade) {
        veiculo.setVelocidade(velocidade);

        String textoLocalizacao = "Latitude: " + latitude + "\nLongitude: " + longitude;
        String textoVelocidadeMedia = "Média\n" + decfor1.format(veiculo.getVelocidade() * 3.6) + " km/h";
        String textoDistanciaPercorrida, textoDistanciaTotal;

        if (distanciaPercorrida > 1000) { //Se maior que 1000m, exibe a distância em km
            textoDistanciaPercorrida = "Percorrida\n" + decfor1.format(distanciaPercorrida / 1000) + " km";
        } else {
            textoDistanciaPercorrida = "Percorrida\n" + decfor2.format(distanciaPercorrida) + " m";
        }

        if (distanciaTotal > 1000) { //Se maior que 1000m, exibe a distância em km
            textoDistanciaTotal = "Total\n" + decfor1.format(distanciaTotal / 1000) + " km";
        } else {
            textoDistanciaTotal = "Total\n" + decfor2.format(distanciaTotal) + " m";
        }

        campoDistanciaTotal.setText(textoDistanciaTotal);
        campoLocalizacaoAtual.setText(textoLocalizacao);
        campoVelocidadeMedia.setText(textoVelocidadeMedia);
        campoDistanciaPercorrida.setText(textoDistanciaPercorrida);

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
