package android.marcusvferreira.appgat108.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.marcusvferreira.appgat108.R;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    public static final int TEMPO_TELA_SPLASH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Realiza a troca da tela Splash para a principal
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mudarParaActivityMain();
            }
        }, TEMPO_TELA_SPLASH);
    }

    private void mudarParaActivityMain(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent); // inicializa a activity main
        finish(); // finaliza a activity splash
    }
}


