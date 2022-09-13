package br.edu.infnet.firebasetest

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

//              FIXME: val empresaAdress = criptografador.descriptografar(empresa!!.adress!!)
                val empresaAdress = empresa!!.adress!!
                lblTexto.append("${empresa!!.name} - ${empresa!!.comments} - ${empresaAdress} - ${empresa!!.is_approved}\n")
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

            if (empresa?.name == null || empresa?.comments == null || empresa.adress == null) {
                Toast.makeText(this.applicationContext, "Preencha todos os campos", Toast.LENGTH_LONG)
            } else {
                val empresaDAO = EmpresaDAO()
                empresaDAO.inserir(empresa)?.addOnSuccessListener {
                    Log.i("MainActivity", "Empresa inserida")
                }?.addOnFailureListener {
                    Log.i("MainActivity", "Erro ao inserir empresa")
                }

                val database = FirebaseDatabase.getInstance()
                val myref = database.getReference("empresa")
                myref.setValue(empresa)

                txtAdress.setText(null)
                txtComments.setText(null)
                txtName.setText(null)
            }
        }
    }

//    private fun atualizarLista() {
//        EmpresaDAO().listar()!!.addOnSuccessListener { listaDeDocumentos ->
//            val nomes = ArrayList<String>()
//            for(documento in listaDeDocumentos) {
//
//                var empresa = documento.toObject(Empresa::class.java)
//                nomes.add(empresa.name.toString())
//            }
//            val lstEmpresas = this.fin
//        }
//    }

    override fun recicleViewItemClicked(view: View, id: String) {

    }
}