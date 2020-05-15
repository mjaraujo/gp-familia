package br.com.mjaraujo.grupodefamilia.ondemand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import br.com.mjaraujo.ondemmand.financeiro.R

class RecursosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recursos)

        var btnFechar: Button = findViewById(R.id.btn_fechar)

        btnFechar.setOnClickListener { finish() }
    }
}
