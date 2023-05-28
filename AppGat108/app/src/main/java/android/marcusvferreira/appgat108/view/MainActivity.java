package android.marcusvferreira.appgat108.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.controller.Permissoes;
import android.marcusvferreira.appgat108.model.Local;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Comentar acerca do código...
 * */
public class MainActivity extends AppCompatActivity {

    //Permissões necessárias
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    //Objetos para controlar os campos TextView
    TextView campoTempoTranscorrido, campoLocalizacaoAtual, campoLocalizacaoDestino;

    //Objetos para controlar os botões
    Button btnIniciarPausar, btnSelecionarTempo;

    //Objetos necessários para obter a localização via GPS
    private LocationManager locationManager;
    private LocationListener locationListener;

    //Objetos e variáveis para controle do timer
    Timer timer;
    TimerTask timerTask;
    Double tempoTranscorrido = 0.0;
    int horasSelecionada, minutosSelecionado;
    boolean timerIniciado = false;

    //Criação do obejto destino (estacionamento DAT UFLA)
    Local destino = new Local( -21.227932, -44.974912);

    //Criação do obejto veículo
    Veiculo veiculo = new Veiculo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //...
        campoLocalizacaoAtual = findViewById(R.id.tv_loc_atual_dados);
        campoLocalizacaoDestino = findViewById(R.id.tv_loc_destino_dados);
        btnSelecionarTempo = findViewById(R.id.btn_selecionar_tempo);
        campoTempoTranscorrido = findViewById(R.id.tv_tempo_transcorrido);
        btnIniciarPausar = findViewById(R.id.btn_iniciar_pausar);
        timer = new Timer();

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        //Objeto responsável por gerenciar a localização do usuário
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();

                campoLocalizacaoAtual.setText("Latitude: " + String.valueOf(latitude)
                        + "\nLongitude: " + String.valueOf(longitude));
            }
        };

        //Captura localização do dispostivo
        getLocalizacao();
    }



    //Verifica se as permissões foram concedidas
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults) {
            //Se permissão de localização aceita, captura localização do dispostivo
            if (permissaoResultado == PackageManager.PERMISSION_GRANTED)
                getLocalizacao();
            // Se permissão de localização negada,

            else {
                //video parte 1 . 8min
            }


        }
    }

    //Obtem a localização do dispostivo
    private void getLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    /*
    public void iniciar(View view) {
        EditText campoTempoDesejado = findViewById(R.id.et_tempo_desejado);
        EditText campoAutonomia = findViewById(R.id.et_autonomia);

        Double autonomia = Double.parseDouble(campoAutonomia.getText().toString());

        TextView campo_latitude = findViewById(R.id.tv_loc_atual_latitude);
        campo_latitude.setText("Latitude: " + autonomia.toString());

    }
*/

    //Controla o click no botão iniciar/pausar
    public void clickIniciarPausar(View view) {
        if (timerIniciado == false) {
            timerIniciado = true;
            btnIniciarPausar.setText("PAUSAR");
            iniciarTimer();
        } else {
            timerIniciado = false;
            btnIniciarPausar.setText("INICIAR");
            timerTask.cancel();
        }
    }

    private void iniciarTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tempoTranscorrido++;
                        campoTempoTranscorrido.setText("Transcorrido\n" + getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    //Realiza as conversões para obter o tempo em hrs, min e seg
    private String getTimerText() {
        int rounded = (int) Math.round(tempoTranscorrido);
        int segundos = ((rounded % 86400) % 3600) % 60;
        int minutos = ((rounded % 86400) % 3600) / 60;
        int horas = (rounded % 86400) / 3600;

        return String.format("%02d", horas) + " : " + String.format("%02d", minutos) + " : " +
                String.format("%02d", segundos);
    }

    //Implementa a funcionalidade do botão de selecionar o tempo desejado
    public void clickSelecionarTempo(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int horas, int minutos) {
                horasSelecionada = horas;
                minutosSelecionado = minutos;
                btnSelecionarTempo.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        horasSelecionada, minutosSelecionado));
                btnSelecionarTempo.setTextSize(14);
            }
        };
        @SuppressWarnings("deprecation") TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, AlertDialog.THEME_HOLO_DARK,onTimeSetListener, horasSelecionada,
                minutosSelecionado, true);
        timePickerDialog.setTitle("Selecione o tempo");
        timePickerDialog.show();
    }
}