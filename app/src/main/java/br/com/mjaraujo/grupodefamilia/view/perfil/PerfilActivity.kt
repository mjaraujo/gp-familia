package br.com.mjaraujo.grupodefamilia.view.perfil

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import br.com.mjaraujo.grupodefamilia.MainActivity
import br.com.mjaraujo.grupodefamilia.R
import br.com.mjaraujo.grupodefamilia.model.Pessoa
import br.com.mjaraujo.grupodefamilia.model.TipoLogradouro
import br.com.mjaraujo.grupodefamilia.sqlite.DataBaseHandler
import br.com.mjaraujo.grupodefamilia.util.CpfCnpjValidator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_perfil.*
import java.io.ByteArrayOutputStream
import java.io.Serializable
import kotlin.system.exitProcess


class PerfilActivity : AppCompatActivity() {

    private lateinit var pessoa: Pessoa
    private lateinit var dataBase: DatabaseReference

    lateinit var txtCpf: TextView
    lateinit var txtNome: TextView
    lateinit var txtTelefone: TextView
    lateinit var txtCelular: TextView
    lateinit var txtLogradouro: TextView
    lateinit var btnCancelar: Button
    lateinit var btnConfirmar: Button
    lateinit var imgFoto: ImageView
    var modoAdicao: Boolean = false
    val CAMERA_REQUEST = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        txtCpf = findViewById(R.id.txtCpf)
        txtCelular = findViewById(R.id.txtCelular)
        txtTelefone = findViewById(R.id.txtTelefone)
        txtLogradouro = findViewById(R.id.txtLogradouro)
        txtNome = findViewById(R.id.txt_nome_perfil)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        imgFoto = findViewById(R.id.img_foto)

        dataBase = FirebaseDatabase.getInstance().getReference("pessoas")
        modoAdicao = intent.extras?.getBoolean("novo")!!

        initViews()

    }


    private fun initViews() {
        btnCancelarClick()
        btnConfirmarClick()
        imgFotoClick()
        initSpinner()

        pessoa = intent.extras?.get("pessoa") as Pessoa
        txtCpf.text = pessoa.cpf
        txtCpf.isFocusable = false
        txtCpf.isFocusableInTouchMode = false
        txtCpf.isClickable = false

        txtNome.text = pessoa.nome
        txtCelular.text = pessoa.celular
        txtLogradouro.text = pessoa.logradouro
        txtTelefone.text = pessoa.telefone

        if (pessoa.tipoLogradouro?.isNotEmpty()!!) {
            val ordinal = TipoLogradouro.fromValue(pessoa.tipoLogradouro.toString()).ordinal
            spnTipoLogradouro.setSelection(ordinal)
        }

        val bMap: Bitmap?
        bMap = if (pessoa.foto?.isEmpty()!!) {
            BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_account_box_24px
            )
        } else {
            val decodedString: ByteArray = Base64.decode(
                pessoa.foto,
                Base64.DEFAULT
            )
            BitmapFactory.decodeByteArray(
                decodedString,
                0,
                decodedString.size
            )
        }
        imgFoto.setImageBitmap(bMap)
    }

    private fun initSpinner() {
        val toList = TipoLogradouro.values().toList()
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            toList
        ).also { }
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spnTipoLogradouro.adapter = adapter
            }
    }

    private fun imgFotoClick() {
        imgFoto.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, CAMERA_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imgFoto.setImageBitmap(data.extras?.get("data") as Bitmap)
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun btnConfirmarClick() {

        btnConfirmar.setOnClickListener {
            btnConfirmar.isClickable = false
            val bitmap = imgFoto.drawable.toBitmap()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val encodedString: String = android.util.Base64.encodeToString(
                stream.toByteArray(),
                android.util.Base64.DEFAULT
            )
            pessoa.foto = encodedString
            if (modoAdicao) {
                writeNewPessoa(
                    txtCpf.text.toString(),
                    txtNome.text.toString(),
                    txtTelefone.text.toString(),
                    txtCelular.text.toString(),
                    txtLogradouro.text.toString(),
                    spnTipoLogradouro.selectedItem as String,
                    encodedString
                )
            } else {
                updatePessoa()
            }

        }
    }

    private fun updatePessoa() {
        val input = ProgressBar(this)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.atualizando_informacoes)
            .setView(input)
            .setCancelable(false)
            .create()
        dialog.show()

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val pessoas: DatabaseReference = database.getReference("pessoas")
        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        if (pessoa.cpf.toString().equals(data.child("cpf").value)) {
                            pessoa.nome = txtNome.text.toString()
                            pessoa.tipoLogradouro = spnTipoLogradouro.selectedItem.toString()
                            pessoa.logradouro = txtLogradouro.text.toString()
                            pessoa.celular = txtCelular.text.toString()
                            pessoa.telefone = txtTelefone.text.toString()
                            val childUpdates = HashMap<String, Any>()
                            childUpdates.put("/pessoas/" + data.key, pessoa)
                            database.reference.updateChildren(childUpdates).addOnCompleteListener {
                                Toast.makeText(
                                    this@PerfilActivity,
                                    resources.getString(R.string.informacoes_atualizadas),
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@PerfilActivity, MainActivity::class.java)
                                intent.putExtra("pessoa", pessoa as Serializable)
                                startActivity(intent)
                                dialog.dismiss()
                                finish()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Cancelled")
            }
        }

        pessoas.orderByKey().addListenerForSingleValueEvent(itemListener)
    }

    private fun writeNewPessoa(
        cpf: String,
        nome: String,
        telefone: String,
        celular: String,
        logradouro: String,
        tipoLogradouro: String,
        foto: String
    ) {
        val input = ProgressBar(this)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.atualizando_informacoes)
            .setView(input)
            .setCancelable(false)
            .create()
        dialog.show()

        val pessoaId = dataBase.push().key
        val pessoa = Pessoa(
            pessoaId.toString(),
            cpf,
            nome,
            telefone,
            celular,
            logradouro,
            tipoLogradouro,
            foto
        )


        dataBase.child(pessoaId.toString()).setValue(pessoa).addOnCompleteListener(
            OnCompleteListener {
                val db = DataBaseHandler(applicationContext)
                db.insertData(pessoa)
                Toast.makeText(
                    this,
                    resources.getString(R.string.informacoes_atualizadas),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@PerfilActivity, MainActivity::class.java)
                intent.putExtra("pessoa", pessoa as Serializable)
                startActivity(intent)
                dialog.dismiss()
                finish()
            }).addOnFailureListener(OnFailureListener {
            Toast.makeText(
                this,
                resources.getString(R.string.falha_atualizacao_informacoes),
                Toast.LENGTH_LONG
            ).show()
            dialog.dismiss()
        })

    }

    private fun btnCancelarClick() {
        btnCancelar.setOnClickListener(
            View.OnClickListener {
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("eitta")

                myRef.setValue("Hello, World!").addOnCompleteListener {
                    val intent = Intent(this@PerfilActivity, MainActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener(OnFailureListener { exception ->
                    exception
                })


            }

        )
    }

}
