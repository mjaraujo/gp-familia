package br.com.mjaraujo.bio.installtime

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.widget.Toast
import androidx.core.app.ActivityCompat
import br.com.mjaraujo.grupodefamilia.view.login.LoginActivity

@SuppressLint("ByteOrderMark")
class FingerprintHelper(private val appContext: Context) :
    FingerprintManager.AuthenticationCallback() {
    private var listener: IAutenticacaoBio? = null

    interface IAutenticacaoBio {
        fun onAutenticado()
    }

    lateinit var cancellationSignal: CancellationSignal

    fun startAuth(
        manager: FingerprintManager,
        cryptoObject: FingerprintManager.CryptoObject
    ) {
        listener = appContext as IAutenticacaoBio
        cancellationSignal = CancellationSignal()

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.USE_FINGERPRINT
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(
        errMsgId: Int,
        errString: CharSequence
    ) {
        Toast.makeText(
            appContext,
            "Authentication error\n" + errString,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onAuthenticationHelp(
        helpMsgId: Int,
        helpString: CharSequence
    ) {
        Toast.makeText(
            appContext,
            "Authentication help\n" + helpString,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(
            appContext,
            "Authentication failed.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onAuthenticationSucceeded(
        result: FingerprintManager.AuthenticationResult
    ) {
        listener?.onAutenticado()
    }
}