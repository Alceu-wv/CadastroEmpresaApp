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

                val empresa = snapshot.getValue(Empresa::class.java)
                if (empresa?.name != null && empresa?.comments != null) {
                    lblTexto.append("${empresa!!.name} - ${empresa!!.comments}\n")
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG)
            }
        })
        btnSalvar.setOnClickListener {
            val empresa = Empresa()
            empresa.name = Date().toString()
            empresa.comments = txtTexto.text.toString()
            val database = FirebaseDatabase.getInstance()
            val myref = database.getReference("mensagens")
            myref.setValue(empresa)
            txtTexto.setText(null)
        }
    }
}