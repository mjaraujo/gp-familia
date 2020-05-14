package br.com.mjaraujo.grupodefamilia

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import br.com.mjaraujo.grupodefamilia.model.Pessoa
import br.com.mjaraujo.grupodefamilia.sqlite.DataBaseHandler
import br.com.mjaraujo.grupodefamilia.util.CpfCnpjValidator
import br.com.mjaraujo.grupodefamilia.view.pagamento.MensalidadeActivity
import br.com.mjaraujo.grupodefamilia.view.perfil.PerfilActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_perfil.*
import java.io.Serializable
import kotlin.system.exitProcess

private const val PACKAGE_NAME = "br.com.mjaraujo.grupodefamilia.installtime"
private const val PACKAGE_NAME_ON_DEMMAND = "br.com.mjaraujo.ondemmand.financeiro"
private const val FINANCEIRO_CLASSNAME = "$PACKAGE_NAME_ON_DEMMAND.RecursosActivity"


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var pessoa: Pessoa = Pessoa()
    private var primeiroAcesso: Boolean = false
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var btnLogin: Button
    lateinit var swiUtilizarBio: Switch
    lateinit var txtNome: TextView
    lateinit var imgFoto: ImageView
    lateinit var pbProgress: ProgressBar

    private lateinit var manager: SplitInstallManager
    private val moduleBio by lazy { getString(R.string.module_bio) }
    private val moduleFinanceiro by lazy { getString(R.string.module_financeiro) }
    private lateinit var dataBase: DatabaseReference

    private val CADASTRO_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtNome = findViewById(R.id.txt_nome)
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        btnLogin = findViewById(R.id.btnLogin)
        swiUtilizarBio = findViewById(R.id.swi_utilizar_bio)
        pbProgress = findViewById(R.id.pbr_progress)
        imgFoto = findViewById(R.id.img_perfil)
        setSupportActionBar(toolbar)

        manager = SplitInstallManagerFactory.create(this)


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        initViews()
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pessoaCallBack = intent.extras?.get("pessoa") as Pessoa
            this.pessoa = pessoaCallBack
        }

        updateComponents()

    }

    private fun initViews() {

        pbProgress.visibility = View.INVISIBLE
        navView.setNavigationItemSelectedListener(this)

        val bioInstalled: Boolean = manager.installedModules.contains(moduleBio)

        swiUtilizarBio.visibility = if (bioInstalled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }


        val db = DataBaseHandler(applicationContext)
        val data = db.readData()
        if (data.size == 0) {
            primeiroAcesso = true
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle(R.string.primeiro_acesso)
                .setMessage(R.string.primeiro_acesso_info)
                .setView(input)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create()

            dialog.setOnShowListener {
                var cpf: String? = null
                val buttonPositive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                buttonPositive.setText(R.string.continuar)
                buttonPositive.setOnClickListener {
                    cpf = input.text.toString()
                    if (!CpfCnpjValidator.isCpfValido(input.text.toString())) {
                        Toast.makeText(
                            this@MainActivity,
                            resources.getString(R.string.cpf_inválido),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        pessoa.cpf = cpf
                        verificarCpf(pessoa, false)
                        dialog.dismiss()
                    }
                }

                val buttonCancel = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
                buttonCancel.setOnClickListener {
                    finishAffinity()
                    moveTaskToBack(true)
                    exitProcess(-1)
                }
            }
            dialog.show()
        } else {
            pessoa.cpf = data[0].cpf
            verificarCpf(pessoa, true)
        }

        setupClickListener()
    }

    fun verificarCpf(pessoa: Pessoa, isInDevice: Boolean) {
        val input = ProgressBar(this)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.verificando_informacoes)
            .setView(input)
            .setCancelable(false)
            .create()
        dialog.show()

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val pessoas: DatabaseReference = database.getReference("pessoas")
        var cpfCadastrado: Boolean = false
        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        pbProgress.setVisibility(View.INVISIBLE)
                        if (pessoa.cpf.toString().equals(data.child("cpf").value)) {
                            cpfCadastrado = true
                            pessoa.logradouro = data.child("logradouro").value as String
                            pessoa.foto = data.child("foto").value as String
                            pessoa.celular = data.child("celular").value as String
                            pessoa.tipoLogradouro = data.child("tipoLogradouro").value as String
                            pessoa.nome = data.child("nome").value as String
                            pessoa.id = data.child("id").value as String
                            pessoa.telefone = data.child("telefone").value as String
                        }
                    }
                    if (cpfCadastrado) {
                        updateComponents()
                        val db = DataBaseHandler(applicationContext)
                        db.insertData(pessoa)
                    } else {
                        solicitarCadastro(pessoa)
                    }
                }
                dialog.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.falha_atualizacao_informacoes),
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                println("Cancelled")
            }
        }

        pessoas.orderByKey().addListenerForSingleValueEvent(itemListener)
    }

    private fun updateComponents() {
        val input = ProgressBar(this)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.atualizando_informacoes)
            .setView(input)
            .setCancelable(false)
            .create()
        dialog.show()


        txtNome.setText(pessoa.nome)

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

        dialog.dismiss()
    }

    private fun solicitarCadastro(pessoa: Pessoa) {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.cpf_nao_cadastrado)
            .setMessage(R.string.cpf_nao_cadastrado_info)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            var cpf: String? = null
            val buttonPositive = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            buttonPositive.setText(R.string.continuar)
            buttonPositive.setOnClickListener {
                val intent = Intent(this@MainActivity, PerfilActivity::class.java)
                intent.putExtra("pessoa", pessoa as Serializable)
                intent.putExtra("novo", true)
                startActivityForResult(intent, CADASTRO_REQUEST)
                dialog.dismiss()
                finish()
            }

            val buttonCancel = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            buttonCancel.setOnClickListener {
                finishAffinity()
                moveTaskToBack(true)
                exitProcess(-1)
            }
        }
        dialog.show()
    }

    private fun setupClickListener() {
        setClickListener(R.id.btnLogin, clickListener)
        /*setClickListener(R.id.btn_load_java, clickListener)
        setClickListener(R.id.btn_load_assets, clickListener)
        setClickListener(R.id.btn_load_native, clickListener)*/
    }

    private fun setClickListener(id: Int, listener: View.OnClickListener) {
        findViewById<View>(id).setOnClickListener(listener)
    }

    private fun displayBio() {

    }

    private fun btnLoginClick() {
        val bioInstalled: Boolean = manager.installedModules.contains(moduleBio)
        if (bioInstalled && swiUtilizarBio.isChecked) {
            val intent = Intent()
            intent.setClassName(
                packageName,
                "br.com.mjaraujo.bio.installtime.AutenticacaoBioActivity"
            )
            startActivity(intent)

        } else {
            Toast.makeText(this, "não instalado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAndLaunchModule(name: String) {

        // Skip loading if the module already is installed. Perform success action directly.
        if (manager.installedModules.contains(name)) {
            onSuccessfulLoad(name, launch = true)
            return
        }

        // Create request to install a feature module by name.
        val request = SplitInstallRequest.newBuilder()
            .addModule(name)
            .build()

        // Load and install the requested feature module.
        manager.startInstall(request)
    }

    private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            when (moduleName) {

                moduleFinanceiro -> launchActivity(FINANCEIRO_CLASSNAME)/*
                 moduleJava -> launchActivity(JAVA_SAMPLE_CLASSNAME)
                 moduleNative -> launchActivity(NATIVE_SAMPLE_CLASSNAME)
                 moduleAssets -> displayAssets()
                 instantModule -> launchActivity(INSTANT_SAMPLE_CLASSNAME)*/
            }
        }
    }

    private fun launchActivity(className: String) {
        Intent().setClassName(BuildConfig.APPLICATION_ID, className)
            .also {
                startActivity(it)
            }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> {
                val intent = Intent(this, PerfilActivity::class.java)
                intent.putExtra("novo", false)
                intent.putExtra("pessoa", pessoa as Serializable)
                startActivity(intent)
                finish()
            }
            R.id.nav_avisos -> {

            }
            R.id.nav_pagamentos -> {
                val intent = Intent(this, MensalidadeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_agenda -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_financeiro -> {
                loadAndLaunchModule(resources.getString(R.string.module_financeiro))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private val clickListener by lazy {
        View.OnClickListener {
            when (it.id) {
                R.id.btnLogin -> btnLoginClick()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CADASTRO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }


}