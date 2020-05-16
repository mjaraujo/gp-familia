package br.com.mjaraujo.dynamicfeature.agenda.model

import java.util.*

data class Mes(
    val nome: String = "",
    val primeiroDia: Date,
    val children: List<Evento>
) : Comparable<Mes> {
    override fun compareTo(other: Mes) = compareValuesBy(this, other, { it.primeiroDia })
}