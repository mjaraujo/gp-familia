package br.com.mjaraujo.grupodefamilia.view.pagamento

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mjaraujo.grupodefamilia.R
import br.com.mjaraujo.grupodefamilia.model.Mensalidade
import kotlinx.android.synthetic.main.detalhe_pagamento.view.*

class ListaPagamentoAdapter(
    private val itens: List<Mensalidade>,
    private val context: Context
) : RecyclerView.Adapter<ListaPagamentoAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itens[position]
        holder?.let {
            it.itemView.txt_mes.text = item.mes
            it.itemView.txt_valor.text = "R$ " + item.valor.toString() + ",00"
            it.itemView.txt_data.text = "Dia " + item.dtPagamento
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.detalhe_pagamento, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itens.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(mensalidade: Mensalidade) {
            val mes = itemView.txt_mes
            val valor = itemView.txt_valor
            val dtPagamento = itemView.txt_data

            mes.text = mensalidade.mes
            dtPagamento.text = mensalidade.dtPagamento
            valor.text = mensalidade.valor.toString()
        }
    }


}
