package br.edu.infnet.firebasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var lblStatus: TextView
    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        lblStatus = findViewById<TextView>(R.id.lblStatus)

        val btnCriarConta = this.findViewById<Button>(R.id.btnCriarConta)
        btnCriarConta.setOnClickListener {
            val txtEmail = this.findViewById<EditText>(R.id.editTextEmail)
            val txtSenha = this.findViewById<EditText>(R.id.editTextSenha)
            mAuth
                .createUserWithEmailAndPassword(txtEmail.text.toString(), txtSenha.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        mUser = mAuth.currentUser
                        updateStatus()
                        nextActivity(mUser!!)
                    }
                    if (it.exception != null) {
                        Toast.makeText(this.applicationContext, "Falha ao criar usuário: ${it.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        val btnSignInAcessar = this.findViewById<Button>(R.id.btnLogar)
        btnSignInAcessar.setOnClickListener {
            val txtEmail = this.findViewById<EditText>(R.id.editTextEmail)
            val txtSenha = this.findViewById<EditText>(R.id.editTextSenha)
            mAuth
                .signInWithEmailAndPassword(txtEmail.text.toString(), txtSenha.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        mUser = mAuth.currentUser
                        updateStatus()
                        nextActivity(mUser!!)
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
                    updateStatus()
                    nextActivity(mUser!!)
                } else {
                    Toast.makeText(this.applicationContext, "Você precisa estar logado para entrar", Toast.LENGTH_LONG).show()
            }
        }

        val btnDeslogar = this.findViewById<Button>(R.id.btnDeslogar)
        btnDeslogar.setOnClickListener {
            if (mUser == null) {
                updateStatus()
                Toast.makeText(this.applicationContext, "Nenhum usuário logado", Toast.LENGTH_LONG).show()
            } else {
                mAuth.signOut()
                updateStatus()
            }
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

    fun nextActivity(user: FirebaseUser) {
        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
    }
}
