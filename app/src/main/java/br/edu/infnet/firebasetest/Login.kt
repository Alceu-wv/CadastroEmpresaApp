package br.edu.infnet.firebasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var lblStatus: TextView
    private var mUser: FirebaseUser? = null
    private lateinit var mAdView: AdView

    private lateinit var btnEmpresasAprovadas: Button
    private lateinit var btnCriarConta: Button
    private lateinit var btnSignInAcessar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtSenha: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAdView = this.findViewById(R.id.adView)
        btnEmpresasAprovadas = this.findViewById(R.id.btnEmpresasAprovadas)
        btnCriarConta = this.findViewById(R.id.btnCriarConta)
        btnSignInAcessar = this.findViewById(R.id.btnLogar)
        txtEmail = this.findViewById(R.id.editTextEmail)
        txtSenha = this.findViewById(R.id.editTextSenha)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAuth = FirebaseAuth.getInstance()
        lblStatus = findViewById(R.id.lblStatus)

        btnCriarConta.setOnClickListener {
            mAuth
                .createUserWithEmailAndPassword(txtEmail.text.toString(), txtSenha.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        mUser = mAuth.currentUser
                        nextActivity()
                    }
                    if (it.exception != null) {
                        Toast.makeText(this.applicationContext, "Falha ao criar usuário: ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnSignInAcessar.setOnClickListener {
            mAuth
                .signInWithEmailAndPassword(txtEmail.text.toString(), txtSenha.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        nextActivity()
                    }
                    if (it.exception != null){
                        Toast.makeText(this.applicationContext, "Falha ao logar: ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this.applicationContext, "Falha desconhecida ao logar", Toast.LENGTH_LONG).show()
                    }
                }

        }

        val btnEntrar = this.findViewById<Button>(R.id.btnEntrar)
        btnEntrar.setOnClickListener {
            if (mUser != null) {
                    nextActivity()
                } else {
                    Toast.makeText(this.applicationContext, "Você precisa estar logado para entrar", Toast.LENGTH_LONG).show()
            }
        }


        btnEmpresasAprovadas.setOnClickListener {
            updateStatus()
            val intent = Intent(this@Login, EmpresasAprovadas::class.java)
            startActivity(intent)
        }
    }


    fun updateStatus() {
        if(mUser == null) {
            lblStatus.setText("Usuário não autenticado")
        } else {
            lblStatus.setText("Usuário autenticado - ${mUser!!.email}")
        }
    }

    override fun onStart() {
        super.onStart()
        mUser = mAuth.currentUser
        updateStatus()
    }

    override fun onStop() {
        super.onStop()
        mAuth.signOut()
    }

    fun nextActivity() {
        updateStatus()
        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
    }
}
