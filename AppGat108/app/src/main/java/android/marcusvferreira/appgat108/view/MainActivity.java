package android.marcusvferreira.appgat108.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.controller.ControleLocalizacao;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Comentar acerca do código...
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private static final DecimalFormat decfor = new DecimalFormat("0.00");
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    GoogleMap mMap;

    //Objetos para controlar os campos TextView
    TextView campoTempoTranscorrido;

    //Objetos para controlar os botões
    Button btnIniciar, btnSelecionarTempo;

    //Objetos e variáveis para controle do timer
    Timer timer;
    TimerTask timerTask;
    //Double tempoTranscorrido = 0.0;
    int horasSelecionada, minutosSelecionado;
    private boolean isTimerIniciado = false; //Controla se o timer foi iniciado
    private boolean isTempoDesejadoSelecionado = false; //Controla se o tempo desejado foi selecionado

    //Criação do obejto veículo
    Veiculo veiculo = new Veiculo();

    // private TextView campoLocalizacaoAtual;
    private ControleLocalizacao controleLocalizacao;

    Fragment mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Mantém a tela ligada

        mapa = getSupportFragmentManager().findFragmentById(R.id.map);
        mapa.getView().setVisibility(View.INVISIBLE);


        btnSelecionarTempo = findViewById(R.id.btn_selecionar_tempo);
        campoTempoTranscorrido = findViewById(R.id.tv_tempo_transcorrido);
        btnIniciar = findViewById(R.id.btn_iniciar);
        timer = new Timer();
        controleLocalizacao = new ControleLocalizacao(this, this, veiculo);

        Spinner opcaoVeiculos = findViewById(R.id.spinner_veiculos);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opcaoVeiculos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        opcaoVeiculos.setAdapter(adapter);
        opcaoVeiculos.setOnItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void iniciarObtencaoLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            //Se as permissões de localização foram concendidas, inicia o controle de localização via thread
            Thread thread = new Thread(controleLocalizacao);
            thread.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarObtencaoLocalizacao();
            }
        }
    }

    //Controla o click no botão iniciar/pausar
    public void clickIniciar(View view) {
        if (isTempoDesejadoSelecionado) {
            btnIniciar.setVisibility(View.INVISIBLE); // Define a visibilidade do botão como invisível
            Toast.makeText(this, "Percurso iniciado. Boa viagem!", Toast.LENGTH_LONG).show();
            iniciarTimer();
            iniciarObtencaoLocalizacao();
            mapa.getView().setVisibility(View.VISIBLE);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else{
            Toast.makeText(this, "Selecione o tempo desejado para o percurso!", Toast.LENGTH_LONG).show();
        }
    }

    private void iniciarTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        veiculo.setTempoTranscorrido(veiculo.getTempoTranscorrido() + 1);
                        campoTempoTranscorrido.setText("Transcorrido\n" + getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    //Realiza as conversões para obter o tempo em hrs, min e seg
    private String getTimerText() {
        int rounded = (int) Math.round(veiculo.getTempoTranscorrido());
        int segundos = ((rounded % 86400) % 3600) % 60;
        int minutos = ((rounded % 86400) % 3600) / 60;
        int horas = (rounded % 86400) / 3600;

        return String.format("%02d", horas) + " : " + String.format("%02d", minutos) + " : " +
                String.format("%02d", segundos);
    }

    //Implementa a funcionalidade do botão de selecionar o tempo desejado
    public void clickSelecionarTempo(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int horas, int minutos) {
                horasSelecionada = horas;
                minutosSelecionado = minutos;
                btnSelecionarTempo.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        horasSelecionada, minutosSelecionado));
                btnSelecionarTempo.setTextSize(14);
                isTempoDesejadoSelecionado = true;
                veiculo.setTempoDesejeado(minutos + horas * 60); //Passa para veículo o tempo selecionado em min
            }
        };
        @SuppressWarnings("deprecation") TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, AlertDialog.THEME_HOLO_DARK, onTimeSetListener, horasSelecionada,
                minutosSelecionado, true);
        timePickerDialog.setTitle("Selecione o tempo");
        timePickerDialog.show();
    }

    //Método referente ao Spinner opcaoVeiculos
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        veiculo.setModelo(parent.getItemAtPosition(position).toString());
    }

    //Método referente ao Spinner opcaoVeiculos (apesar de vazio para a necessidade da implementação
    //feita, é obrigatório)
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        controleLocalizacao.setmMap(googleMap);



    }
}