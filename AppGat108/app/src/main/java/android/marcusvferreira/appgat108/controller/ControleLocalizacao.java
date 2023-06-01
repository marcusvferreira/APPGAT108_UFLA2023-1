package android.marcusvferreira.appgat108.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.marcusvferreira.appgat108.view.MainActivity;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ControleLocalizacao implements Runnable, LocationListener {

    private static final DecimalFormat decfor = new DecimalFormat("0.00"); //Converter os valores para formato decimal
    private final Context context;
    private final Activity activity;
    private LocationManager locationManager;
    private Location destino = new Location(""); //Armazena a localização do destino
    private Location origem = null; //Armazena a localização inicial (origem)
    private Veiculo veiculo;
    double distanciaTotal;

    //Objetos para manipular os campos TextView
    private TextView campoVelocidadeMedia, campoVelocidadeRecomendada, campoLocalizacaoAtual,
            campoLocalizacaoDestino, campoDistanciaTotal, campoDistanciaPercorrida, campoConsumo;

    public ControleLocalizacao(Activity activity, Context context, Veiculo veiculo) { //Construtor
        this.context = context;
        this.activity = activity;
        this.veiculo = veiculo;

        campoLocalizacaoAtual = activity.findViewById(R.id.tv_loc_atual_dados);
        campoLocalizacaoDestino = activity.findViewById(R.id.tv_loc_destino_dados);
        campoVelocidadeMedia = activity.findViewById(R.id.tv_velocidade_media);
        campoVelocidadeRecomendada = activity.findViewById(R.id.tv_velocidade_recomendada);
        campoDistanciaTotal = activity.findViewById(R.id.tv_distancia_total);
        campoDistanciaPercorrida = activity.findViewById(R.id.tv_distancia_percorrida);

        //Configura o destino como sendo no final da Av. Norte da UFLA
        destino.setLatitude(-21.222635);
        destino.setLongitude(-44.970962);
        String textoDestino ="Latitude: " + destino.getLatitude() + "\nLongitude: " + destino.getLongitude();
        campoLocalizacaoDestino.setText(textoDestino);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        Looper.prepare();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Looper.loop();
    }

    @Override
    public void onLocationChanged(Location location) {

        // Verificar se a origem ainda não foi definida. Armazena a primeira localização lida como origem
        // e calcula a distância total do percurso
        if (origem == null) {
            origem = new Location("");
            origem.setLatitude(location.getLatitude());
            origem.setLongitude(location.getLongitude());
            distanciaTotal = location.distanceTo(destino);
        }

        //Atualizar localização
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity mainActivity = (MainActivity) context;
                veiculo.setVelocidade(location.getSpeed());
                atualizarLocalizacao(location.getLatitude(), location.getLongitude(), location.distanceTo(origem));
               // campoVelocidadeMedia.setText("Média\n" + decfor.format(location.getSpeed()*3.6) + " km/h");
                /*
                try {
                    // Atraso de 1 segundo
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private void atualizarLocalizacao(double latitude, double longitude, double distancia) {
        String textoLocalizacao = "Latitude: " + latitude + "\nLongitude: " + longitude;
        String textoVelocidadeMedia = "Média\n" + decfor.format(veiculo.getVelocidade()*3.6) + " km/h";
        String textoDistanciaPercorrida, textoDistanciaTotal;

        if(distancia > 1000){ //Se maior que 1000m, exibe a distância em km
            textoDistanciaPercorrida = "Percorrida\n" + decfor.format(distancia/1000)+ " km";
        } else {
            textoDistanciaPercorrida = "Percorrida\n" + decfor.format(distancia)+ " m";
        }

        if(distanciaTotal > 1000){ //Se maior que 1000m, exibe a distância em km
            textoDistanciaTotal = "Total\n" + decfor.format(distanciaTotal/1000)+ " km";
        } else {
            textoDistanciaTotal = "Total\n" + decfor.format(distanciaTotal)+ " m";
        }

        campoDistanciaTotal.setText(textoDistanciaTotal);
        campoLocalizacaoAtual.setText(textoLocalizacao);
        campoVelocidadeMedia.setText(textoVelocidadeMedia);
        campoDistanciaPercorrida.setText(textoDistanciaPercorrida);


    }

}
