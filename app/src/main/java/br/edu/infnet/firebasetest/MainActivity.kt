package br.edu.infnet.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtName = this.findViewById<EditText>(R.id.txtName)
        val txtAdress = this.findViewById<EditText>(R.id.txtAdress)
        val txtComments = this.findViewById<EditText>(R.id.txtComments)
        val checkBoxIsApproved = this.findViewById<CheckBox>(R.id.checkBoxIsApproved)
        val lblTexto = this.findViewById<TextView>(R.id.textView)
        val btnSalvar = this.findViewById<Button>(R.id.btnSalvar)
        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference("mensagens")
        myref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val empresa = snapshot.getValue(Empresa::class.java)
                if (empresa?.name != null && empresa?.comments != null) {
                    lblTexto.append("${empresa!!.name} - ${empresa!!.comments} - ${empresa!!.adress} - ${empresa!!.is_approved}\n")
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG)
            }
        })
        btnSalvar.setOnClickListener {
            val empresa = Empresa()
            empresa.name = txtName.text.toString()
            empresa.comments = txtComments.text.toString()
            empresa.adress = txtAdress.text.toString()
            empresa.is_approved = checkBoxIsApproved.isChecked

            val database = FirebaseDatabase.getInstance()
            val myref = database.getReference("mensagens")
            myref.setValue(empresa)

            txtAdress.setText(null)
            txtComments.setText(null)
            txtName.setText(null)
        }
    }
}