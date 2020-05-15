package br.com.mjaraujo.grupodefamilia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

private const val SPLASH_TIME_OUT = 3000

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed(
            {
                mostrarLogin()
                finish()
            }, SPLASH_TIME_OUT.toLong()
        )

    }

    private fun mostrarLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
