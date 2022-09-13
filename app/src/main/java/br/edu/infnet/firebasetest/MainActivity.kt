package br.edu.infnet.firebasetest

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), RecyclerViewItemListener {

    val criptografador = Criptografador()
    private lateinit var mUser: FirebaseUser


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtName = this.findViewById<EditText>(R.id.txtName)
        val txtAdress = this.findViewById<EditText>(R.id.txtAdress)
        val txtComments = this.findViewById<EditText>(R.id.txtComments)
        val checkBoxIsApproved = this.findViewById<CheckBox>(R.id.checkBoxIsApproved)
        val lblTexto = this.findViewById<TextView>(R.id.txtNameRow)
        val btnSalvar = this.findViewById<Button>(R.id.btnSalvar)
        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference("mensagens")
        myref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val empresa = snapshot.getValue(Empresa::class.java)

                // Essa verificação não está funcionando pois parametro vazios vem como string vazia.
                // TODO: Passar essa verificação para o momento do salvamento
                if (empresa?.name != null && empresa?.comments != null && empresa.adress != "") {

                    // FIXME: O descritografador não está funcionando
//                    val empresaAdress = criptografador.descriptografar(empresa!!.adress!!)
                    val empresaAdress = empresa!!.adress!!
                    lblTexto.append("${empresa!!.name} - ${empresa!!.comments} - ${empresaAdress} - ${empresa!!.is_approved}\n")
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
            empresa.adress = criptografador.criptografar(txtAdress.text.toString())
            empresa.is_approved = checkBoxIsApproved.isChecked

            val database = FirebaseDatabase.getInstance()
            val myref = database.getReference("mensagens")
            myref.setValue(empresa)

            txtAdress.setText(null)
            txtComments.setText(null)
            txtName.setText(null)
        }
    }

    override fun recicleViewItemClicked(view: View, id: String) {

    }
}