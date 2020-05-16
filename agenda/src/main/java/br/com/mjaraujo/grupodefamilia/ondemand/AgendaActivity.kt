package br.com.mjaraujo.grupodefamilia.ondemand


import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mjaraujo.dynamicfeature.agenda.model.Evento
import br.com.mjaraujo.dynamicfeature.agenda.model.Mes
import br.com.mjaraujo.dynamicfeature.ondemand.R
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AgendaActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var btnFechar: Button
    var listMeses: ArrayList<Mes> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        btnFechar = findViewById(R.id.btn_fechar_agenda)
        bntFecharClick()
        requestMeses()
    }

    private fun bntFecharClick() {
        btnFechar.setOnClickListener {
            finish()
        }
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.rcv_agenda)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AgendaActivity, RecyclerView.VERTICAL, false)

            adapter = MesAdapter(listMeses.sorted().toList())
        }

    }

    private fun requestMeses() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val eventos: DatabaseReference = database.getReference("eventos")
        var cpfCadastrado: Boolean = false
        val input = ProgressBar(this)

        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(input)
            .setCancelable(false)
            .create()
        dialog.show()


        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        val nomeMes: String = data.key.toString().toUpperCase()
                        val eventos: ArrayList<Evento> = ArrayList()
                        data.children.toList().forEach {
                            val evento: Evento = Evento(
                                it.child("descricao").value.toString(),
                                it.child("data").value.toString(),
                                it.child("inicio").value.toString()
                            )
                            eventos.add(evento)
                        }
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                        val mes: Mes = Mes(
                            nomeMes,
                            formatter.parse(eventos.get(0).data.replaceRange(0, 1, "01")),
                            eventos
                        )
                        listMeses.add(mes)
                    }

                    dialog.dismiss()
                    initRecycler()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Cancelled")
            }
        }

        eventos.orderByKey().addListenerForSingleValueEvent(itemListener)
    }
}
