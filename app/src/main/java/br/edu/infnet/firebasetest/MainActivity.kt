package br.edu.infnet.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtTexto = this.findViewById<EditText>(R.id.txtInsert)
        val lblTexto = this.findViewById<TextView>(R.id.textView)
        val btnSalvar = this.findViewById<Button>(R.id.btnSalvar)
        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference("mensagens")
        myref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val mensagem = snapshot.getValue(Mensagem::class.java)
                if (mensagem?.data != null && mensagem?.texto != null) {
                    lblTexto.append("${mensagem!!.data} - ${mensagem!!.texto}\n")
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG)
            }
        })
        btnSalvar.setOnClickListener {
            val mensagem = Mensagem()
            mensagem.data = Date().toString()
            mensagem.texto = txtTexto.text.toString()
            val database = FirebaseDatabase.getInstance()
            val myref = database.getReference("mensagens")
            myref.setValue(mensagem)
            txtTexto.setText(null)
        }
    }
}