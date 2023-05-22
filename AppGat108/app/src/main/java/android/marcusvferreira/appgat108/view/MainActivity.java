package android.marcusvferreira.appgat108.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.marcusvferreira.appgat108.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView campoTempoTranscorrido;

    Button btnIniciarPausar, btnSelecionarTempo;

    Timer timer;
    TimerTask timerTask;
    Double tempoTranscorrido = 0.0;

    public int horasSelecionada, minutosSelecionado;

    boolean timerIniciado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelecionarTempo = (Button) findViewById(R.id.btn_selecionar_tempo);


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

    private String getTimerText() {
        int rounded = (int) Math.round(tempoTranscorrido);

        int segundos = ((rounded % 86400) % 3600) % 60;
        int minutos = ((rounded % 86400) % 3600) / 60;
        int horas = (rounded % 86400) / 3600;

        return String.format("%02d", horas) + " : " + String.format("%02d", minutos) + " : " +
                String.format("%02d", segundos);
    }

    public void clickSelecionarTempo(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int horas, int minutos) {
                horasSelecionada = horas;
                minutosSelecionado = minutos;
                btnSelecionarTempo.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        horasSelecionada, minutosSelecionado));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                AlertDialog.THEME_HOLO_DARK,onTimeSetListener, horasSelecionada, minutosSelecionado,
                true);
        timePickerDialog.setTitle("Selecione o tempo");
        timePickerDialog.show();
    }
}