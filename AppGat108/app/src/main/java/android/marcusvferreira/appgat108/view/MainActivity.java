package android.marcusvferreira.appgat108.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.marcusvferreira.appgat108.R;
import android.marcusvferreira.appgat108.controller.ControleLocalizacao;
import android.marcusvferreira.appgat108.controller.Medidor;
import android.marcusvferreira.appgat108.model.Servico;
import android.marcusvferreira.appgat108.model.Veiculo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A classe MainActivity é a atividade principal do aplicativo, controlando a interface do usuário.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private TextView campoTempoTranscorrido; //Objeto para controlar o campo TextView do tempo transcorrido
    private Button btnIniciar, btnSelecionarTempo; //Objetos para controlar os botões 'iniciar' e 'selecione o tempo'

    //Objetos e variáveis para controle do timer
    Timer timer;
    TimerTask timerTask;

    private int horasSelecionada, minutosSelecionado;
    public boolean isTimerIniciado = false; //Controla se o timer foi iniciado
    private boolean isTempoDesejadoSelecionado = false; //Controla se o tempo desejado foi selecionado
    private Veiculo veiculo; //Declaração do objeto veículo
    private ControleLocalizacao controleLocalizacao;
    private Fragment mapa;
    private Servico servico;
    private Medidor medidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Mantém a tela ligada

        Intent intent = getIntent();
        if (intent.hasExtra("servico")) {
            servico = intent.getParcelableExtra("servico");
        }

        mapa = getSupportFragmentManager().findFragmentById(R.id.map);
        mapa.getView().setVisibility(View.INVISIBLE); //Define o mapa como invisível ao iniciar
        btnSelecionarTempo = findViewById(R.id.btn_selecionar_tempo);
        campoTempoTranscorrido = findViewById(R.id.tv_tempo_transcorrido);
        btnIniciar = findViewById(R.id.btn_iniciar);
        timer = new Timer();
        veiculo = new Veiculo();
        medidores = new Medidor(veiculo);
        controleLocalizacao = new ControleLocalizacao(this, this, veiculo, medidores, new Handler(Looper.getMainLooper()), servico);

        Spinner opcaoVeiculos = findViewById(R.id.spinner_veiculos);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opcaoVeiculos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        opcaoVeiculos.setAdapter(adapter);
        opcaoVeiculos.setOnItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Inicia a obtenção da localização atual do dispositivo.
     * Verifica se as permissões necessárias foram concedidas.
     * Se as permissões de localização não foram concedidas, solicita ao usuário.
     * Caso contrário, inicia o controle de localização via thread.
     */
    private void iniciarObtencaoLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Thread threadControleLocalizacao = new Thread(controleLocalizacao);
            threadControleLocalizacao.start();
        }
    }

    /**
     * Trata a resposta do usuário em relação às permissões solicitadas.
     * Se as permissões de localização foram concedidas, inicia a obtenção da localização.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarObtencaoLocalizacao();
            }
        }
    }


    /**
     * Resposta ao clique do usuário no botão 'Iniciar'.
     * Verifica se o tempo desejado para o percurso foi selecionado.
     * Se o tempo desejado foi selecionado, inicia o timer e a obtenção da localização.
     * Define a visibilidade do mapa como visível.
     */
    public void clickIniciar(View view) {
        if (isTempoDesejadoSelecionado) {
            btnIniciar.setVisibility(View.INVISIBLE); //Define a visibilidade do botão como invisível
            Toast.makeText(this, "Percurso iniciado. Boa viagem!", Toast.LENGTH_LONG).show();
            iniciarTimer();
            servico.setDataHoraInicio(LocalDateTime.now());
            servico.setVeiculo(veiculo);

            iniciarObtencaoLocalizacao();
            mapa.getView().setVisibility(View.VISIBLE); //Define o mapa como visível

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Selecione o tempo desejado para o percurso!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Inicia o timer para contar o tempo transcorrido do percurso.
     * Atualiza o tempo transcorrido a cada segundo.
     */
    private void iniciarTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    veiculo.setTempoTranscorrido(veiculo.getTempoTranscorrido() + 1);
                    campoTempoTranscorrido.setText("Transcorrido\n" + getTimerText());
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
        isTimerIniciado = true;
    }

    /**
     * Pausa o timer, cancelando a tarefa programada.
     */
    public void pausarTimer() {
        if (isTimerIniciado && timerTask != null) {
            timerTask.cancel();
            isTimerIniciado = false;
        }
    }

    /**
     * Obtém o texto formatado para exibir o tempo transcorrido.
     * Realiza as conversões necessárias para obter o tempo em horas, minutos e segundos.
     * Retorna o tempo formatado como uma string.
     */
    @SuppressLint("DefaultLocale")
    private String getTimerText() {
        int rounded = (int) Math.round(veiculo.getTempoTranscorrido());
        int segundos = ((rounded % 86400) % 3600) % 60;
        int minutos = ((rounded % 86400) % 3600) / 60;
        int horas = (rounded % 86400) / 3600;

        return String.format("%02d", horas) + " : " + String.format("%02d", minutos) + " : " +
                String.format("%02d", segundos);
    }

    /**
     * Resposta ao clique do usuário no botão 'Selecionar Tempo'.
     * Exibe um dialog de seleção de tempo.
     * O tempo selecionado é armazenado na variável 'horasSelecionada' e 'minutosSelecionado'.
     * Define o tempo selecionado no botão 'Selecionar Tempo' e marca a flag 'isTempoDesejadoSelecionado' como true.
     * Passa o tempo selecionado para o objeto veículo.
     */
    public void clickSelecionarTempo(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view1, horas, minutos) -> {
            horasSelecionada = horas;
            minutosSelecionado = minutos;
            btnSelecionarTempo.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    horasSelecionada, minutosSelecionado));
            btnSelecionarTempo.setTextSize(14);
            isTempoDesejadoSelecionado = true;
            veiculo.setTempoDesejado((double) minutosSelecionado / 60 + horasSelecionada); //Passa para veículo o tempo selecionado em hrs
        };
        @SuppressWarnings("deprecation") TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, AlertDialog.THEME_HOLO_DARK, onTimeSetListener, horasSelecionada,
                minutosSelecionado, true);
        timePickerDialog.setTitle("Selecione o tempo");
        timePickerDialog.show();
    }

    /**
     * Implementação do método da interface AdapterView.OnItemSelectedListener.
     * Obtém o modelo de veículo selecionado pelo usuário no spinner 'opcaoVeiculos' e armazena no objeto veículo.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        veiculo.setModelo(parent.getItemAtPosition(position).toString());
    }

    /**
     * Implementação do método da interface AdapterView.OnItemSelectedListener.
     * Método vazio, pois não é necessário nenhuma ação quando nada é selecionado.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Implementação do método da interface OnMapReadyCallback.
     * Atualiza o mapa com a instância do GoogleMap recebida como parâmetro.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        controleLocalizacao.setmMap(googleMap);
    }
}