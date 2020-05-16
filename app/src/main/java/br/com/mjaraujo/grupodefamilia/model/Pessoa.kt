package br.com.mjaraujo.grupodefamilia.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Pessoa(
    var id: String? = "",
    var cpf: String? = "",
    var nome: String? = "",
    var telefone: String? = "",
    var celular: String? = "",
    var logradouro: String? = "",
    var tipoLogradouro: String? = "",
    var senha: String? = "",
    var foto: String? = ""
) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "cpf" to cpf,
            "nome" to nome,
            "celular" to celular,
            "logradouro" to logradouro,
            "tipoLogradouro" to tipoLogradouro,
            "senha" to senha,
            "foto" to foto
        )
    }

}