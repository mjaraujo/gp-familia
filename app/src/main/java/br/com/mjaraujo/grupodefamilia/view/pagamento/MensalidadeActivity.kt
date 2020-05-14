package br.com.mjaraujo.grupodefamilia.view.pagamento

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.mjaraujo.grupodefamilia.R
import br.com.mjaraujo.grupodefamilia.model.Mensalidade
import kotlinx.android.synthetic.main.activity_mensalidade.*

class MensalidadeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensalidade)
        val menslidades = menslidades()
        val recyclerView = rcv_mensalidades
        recyclerView.adapter = ListaPagamentoAdapter(menslidades, this)
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        val btnFechar: Button = findViewById(R.id.btn_fechar)
        btnFechar.setOnClickListener {
            finish()
        }

        var total: Int = 0
        menslidades.forEach {
            total += it.valor
        }

        val txtValorTotal: TextView = findViewById(R.id.txt_valor_total)
        txtValorTotal.text = "R$ " + total + ",00"


    }

    private fun menslidades(): List<Mensalidade> {
        return listOf(
            Mensalidade("", "Janeiro", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade("", "Janeiro", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade(
                "",
                "Fevereiro",
                (5 until 20).random(),
                (1 until 30).random().toString()
            ),
            Mensalidade("", "Março", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade("", "Abril", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade("", "Maio", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade("", "Junho", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade("", "Julho", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade("", "Agosto", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade(
                "",
                "Setembro",
                (5 until 20).random(),
                (1 until 30).random().toString()
            ),
            Mensalidade("", "Outubro", (5 until 20).random(), (1 until 30).random().toString()),
            Mensalidade(
                "",
                "Novembro",
                (5 until 20).random(),
                (1 until 30).random().toString()
            ),
            Mensalidade("", "Dezembro", (5 until 20).random(), (1 until 30).random().toString())
        )
    }
}
