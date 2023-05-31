package android.marcusvferreira.appgat108.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.marcusvferreira.appgat108.R;
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
    Location destino = new Location(""); //Criação do objeto destino

    //Objetos para manipular os campos TextView
    TextView campoVelocidadeMedia, campoLocalizacaoAtual, campoLocalizacaoDestino, campoDistanciaTotal;

    public ControleLocalizacao(Activity activity, Context context) { //Construtor
        this.context = context;
        this.activity = activity;

        campoVelocidadeMedia = activity.findViewById(R.id.tv_velocidade_media);
        campoLocalizacaoAtual = activity.findViewById(R.id.tv_loc_atual_dados);
        campoLocalizacaoDestino = activity.findViewById(R.id.tv_loc_destino_dados);
        campoDistanciaTotal = activity.findViewById(R.id.tv_distancia_total);

        //Configura o destino como o estacionamento do DAT-UFLA
        destino.setLatitude(-21.222635);
        destino.setLongitude(-44.970962);
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
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double distancia = location.distanceTo(destino);

        // Atualizar a localização na MainActivity
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                MainActivity mainActivity = (MainActivity) context;
                atualizarLocalizacao(latitude, longitude, distancia);
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
        campoLocalizacaoAtual.setText(textoLocalizacao);
        campoVelocidadeMedia.setText("Média\n" + decfor.format(distancia));
    }

    // Resto do código da interface LocationListener
    // ...
}
