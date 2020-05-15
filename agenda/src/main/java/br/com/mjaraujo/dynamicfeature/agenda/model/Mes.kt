package br.com.mjaraujo.dynamicfeature.agenda.model

data class Mes(
    val nome: String = "",
    val children: List<Evento>
)