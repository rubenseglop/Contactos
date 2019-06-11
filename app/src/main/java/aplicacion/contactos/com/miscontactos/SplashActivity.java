package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    // Duración en milisegundos que se mostrará el splash
    private static int tiempo_splash=1500;
    private ImageView logo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        logo = findViewById(R.id.logo);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mysplashanimation);
        logo.startAnimation(myanim);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        },tiempo_splash);
    }
}
