package br.com.mjaraujo.grupodefamilia.util


/**
 *
 * @author Márcio J. Araújo
 */
object CpfCnpjValidator {
    private val pesoCPF = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    private val pesoCNPJ = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    private fun calcularDigito(str: String, peso: IntArray): Int {
        var soma = 0
        var indice = str.length - 1
        var digito: Int
        while (indice >= 0) {
            digito = str.substring(indice, indice + 1).toInt()
            soma += digito * peso[peso.size - str.length + indice]
            indice--
        }
        soma = 11 - soma % 11
        return if (soma > 9) 0 else soma
    }

    fun isCpfValido(cpf: String?): Boolean {
        var cpf = cpf ?: return false
        cpf = cpf.replace("\\D+".toRegex(), "")
        if (cpf.length != 11) {
            return false
        }
        if (isDigitosIguais(cpf)) {
            return false
        }
        val digito1 =
            calcularDigito(cpf.substring(0, 9), pesoCPF)
        val digito2 =
            calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF)
        return cpf == cpf.substring(0, 9) + digito1.toString() + digito2.toString()
    }

    fun isCnpjValido(cnpj: String?): Boolean {
        var cnpj = cnpj ?: return false
        cnpj = cnpj.replace("\\D+".toRegex(), "")
        if (cnpj.length != 14) {
            return false
        }
        if (isDigitosIguais(cnpj)) {
            return false
        }
        val digito1 =
            calcularDigito(cnpj.substring(0, 12), pesoCNPJ)
        val digito2 = calcularDigito(
            cnpj.substring(0, 12) + digito1,
            pesoCNPJ
        )
        return cnpj == cnpj.substring(0, 12) + digito1.toString() + digito2.toString()
    }

    private fun isDigitosIguais(input: String): Boolean {
        val digitos = input.toCharArray()
        var digitosIguais = true
        var digitoAnterior = digitos[0]
        for (indice in 1 until digitos.size) {
            if (digitos[indice] != digitoAnterior) {
                digitosIguais = false
                break
            }
            digitoAnterior = digitos[indice]
        }
        return digitosIguais
    }
}
