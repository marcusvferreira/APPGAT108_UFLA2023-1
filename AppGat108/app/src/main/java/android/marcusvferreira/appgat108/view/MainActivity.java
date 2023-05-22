package android.marcusvferreira.appgat108.view;

import androidx.appcompat.app.AppCompatActivity;

import android.marcusvferreira.appgat108.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView campoTempoTranscorrido;

    Button btnIniciarPausar, btnPausar;

    Timer timer;
    TimerTask timerTask;
    Double tempo = 0.0;

    boolean timerIniciado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        campoTempoTranscorrido = (TextView) findViewById(R.id.tv_tempo_transcorrido);
        btnIniciarPausar = (Button) findViewById(R.id.btn_iniciar_pausar);

        timer = new Timer();
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

    public void clickIniciarPausar(View view) {
        if (timerIniciado == false) {
            timerIniciado = true;
            btnIniciarPausar.setText("PAUSAR");
            iniciarTimer();
        } else {
            timerIniciado = false;
            btnIniciarPausar.setText("INICAR");

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
                        tempo++;
                        campoTempoTranscorrido.setText("Transcorrido \n" + getTimerText());
                    }
                });

            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private String getTimerText() {
        int rounded = (int) Math.round(tempo);

        int segundos = ((rounded % 86400) % 3600) % 60;
        int minutos = ((rounded % 86400) % 3600) / 60;
        int horas = (rounded % 86400) / 3600;

        return String.format("%02d", horas) + " : " + String.format("%02d", minutos) + " : " +
                String.format("%02d", segundos);
    }
}