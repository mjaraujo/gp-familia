package br.com.mjaraujo.grupodefamilia.model

enum class TipoLogradouro(val tipoLogradouro: String) {
    RUA("Rua"),
    ALAMEDA("Alameda"),
    AVENIDA("Avenida"),
    PRACA("Praça"),
    LARGO("Largo"),
    CHACARA("Chácara");

    companion object {
        private val mapping = values().associateBy(TipoLogradouro::tipoLogradouro)
        fun fromValue(value: String) =
            mapping[value] ?: error("Look up failed for ${this.javaClass.declaringClass}")
    }

    override fun toString(): String {
        return tipoLogradouro
    }
}
