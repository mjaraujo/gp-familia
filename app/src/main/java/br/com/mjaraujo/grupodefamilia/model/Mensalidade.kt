package br.com.mjaraujo.grupodefamilia.model

data class Mensalidade(
    var id: String? = "",
    var mes: String? = "",
    var valor: Int,
    var dtPagamento:String

)