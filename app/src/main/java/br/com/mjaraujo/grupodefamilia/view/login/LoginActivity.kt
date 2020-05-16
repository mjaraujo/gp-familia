package br.com.mjaraujo.grupodefamilia.view.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import br.com.mjaraujo.grupodefamilia.R


/**
 * Activity para realização de login
 *
 * @author Márcio Araújo
 * @since 1.7.0.2
 */
class LoginActivity : DialogFragment() {
    private var passwordEditText: EditText? = null
    private var loginPosGo = false
    private var listener: ICredentialsDialogListener? = null

    /**
     * Interface para disparo de eventos na realização do login
     *
     * @author Márcio Araújo
     * @since 1.7.0.2
     */
    interface ICredentialsDialogListener {
        fun onDialogPositiveClick(
            password: String?
        )

        fun onDialogNegativeClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ICredentialsDialogListener
        } catch (e: IllegalStateException) {

        }
    }

    /**
     * Método disparado ao criar esta view
     *
     * @param savedInstanceState Objeto contendo o estado salvo anteriormente da activity
     * @return Objeto Dialog preenchido com as credenciais de acesso
     * @author Márcio Araújo
     * @since 1.7.0.2
     */

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_login, null)
        passwordEditText = view.findViewById<View>(R.id.login_edt_senha) as EditText
        val imagem =
            view.findViewById<View>(R.id.login_img_icone) as ImageView
        val bitmap = if (loginPosGo) BitmapFactory.decodeResource(
            getResources(),
            R.mipmap.ic_gp_famila
        ) else BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        imagem.setImageBitmap(bitmap)
        val builder: AlertDialog.Builder = AlertDialog.Builder(getActivity())
            .setView(view)
            .setTitle("Credenciais de acesso")
            .setNegativeButton("Cancelar") { dialog, which -> listener!!.onDialogNegativeClick() }
            .setPositiveButton("Continuar") { dialog, which ->
                if (listener != null) {
                    listener!!.onDialogPositiveClick(
                        passwordEditText!!.text.toString()
                    )
                }
            }
        return builder.create()
    }

}