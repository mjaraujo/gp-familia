package br.com.mjaraujo.grupodefamilia.ondemand

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.mjaraujo.dynamicfeature.agenda.model.Evento
import br.com.mjaraujo.dynamicfeature.ondemand.R
import kotlinx.android.synthetic.main.evento_recycler.view.*

class EventoAdapter(private val children: List<Evento>) :
    RecyclerView.Adapter<EventoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.evento_recycler, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val child = children[position]
        holder.txtDescricao.text = child.nome
        holder.txtDataEvento.text = child.data
        holder.txtInicio.text = child.inicio
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDataEvento: TextView = itemView.txt_data_evento
        val txtDescricao: TextView = itemView.txt_nome_evento
        val txtInicio: TextView = itemView.txt_inicio_evento

    }
}