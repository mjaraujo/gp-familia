package br.com.mjaraujo.grupodefamilia.ondemand


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mjaraujo.dynamicfeature.agenda.model.Evento
import br.com.mjaraujo.dynamicfeature.agenda.model.Mes
import br.com.mjaraujo.dynamicfeature.ondemand.R

class MesAdapter(private val meses: List<Mes>) : RecyclerView.Adapter<MesAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.mes_recycler, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return meses.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val parent = meses[position]
        holder.textView.text = parent.nome
        val childLayoutManager = LinearLayoutManager(
            holder.recyclerView.context,
            RecyclerView.HORIZONTAL,
            false
        )
        childLayoutManager.initialPrefetchItemCount = 4
        holder.recyclerView.apply {
            layoutManager = childLayoutManager
            adapter = EventoAdapter(parent.children)
            setRecycledViewPool(viewPool)
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_child)
        val textView: TextView = itemView.findViewById(R.id.tx_mes_agenda)
    }
}