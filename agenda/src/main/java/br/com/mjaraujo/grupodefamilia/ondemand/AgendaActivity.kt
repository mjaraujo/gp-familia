package br.com.mjaraujo.grupodefamilia.ondemand


import android.app.AlertDialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mjaraujo.dynamicfeature.agenda.model.Evento
import br.com.mjaraujo.dynamicfeature.agenda.model.Mes
import br.com.mjaraujo.dynamicfeature.ondemand.R
import com.google.firebase.database.*


class AgendaActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    var listMeses: ArrayList<Mes> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        requestMeses()
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.rcv_agenda)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AgendaActivity, RecyclerView.VERTICAL, false)

            adapter = MesAdapter(listMeses.toList())
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
                        val mes: Mes = Mes(
                            nomeMes,
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
