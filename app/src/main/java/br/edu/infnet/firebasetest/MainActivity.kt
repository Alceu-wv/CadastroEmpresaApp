package br.edu.infnet.firebasetest

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), RecyclerViewItemListener {

    val criptografador = Criptografador()
    private lateinit var mUser: FirebaseUser
    private lateinit var empresaDAO: EmpresaDAO

    private lateinit var mAuth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        empresaDAO = EmpresaDAO()

        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth.currentUser


        val txtName = this.findViewById<EditText>(R.id.txtName)
        val txtAdress = this.findViewById<EditText>(R.id.txtAdress)
        val txtComments = this.findViewById<EditText>(R.id.txtComments)
        val checkBoxIsApproved = this.findViewById<CheckBox>(R.id.checkBoxIsApproved)
//        val lstEmpresas = this.findViewById<TextView>(R.id.lstEmpresas)
        val btnSalvar = this.findViewById<Button>(R.id.btnSalvar)

        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference("empresa")
        myref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val empresas = obter_empresas_de_usuario(mUser!!.uid)


//              FIXME: val empresaAdress = criptografador.descriptografar(empresa!!.adress!!)
//                if (empresa != null) {
//                    val empresaAdress = empresa!!.adress!!
//                    lblTexto.append("${empresa!!.name} - ${empresa!!.comments} - ${empresaAdress} - ${empresa!!.is_approved}\n")
//                }
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

//                val database = FirebaseDatabase.getInstance()
//                val myref = database.getReference("empresa")
//                myref.setValue(empresa)

                txtAdress.setText(null)
                txtComments.setText(null)
                txtName.setText(null)
            }
        }
    }

    private fun atualizarLista() {
        EmpresaDAO().listar()!!.addOnSuccessListener { listaDeDocumentos ->

            val empresas = ArrayList<Empresa>()
            for(documento in listaDeDocumentos) {

                var empresa = documento.toObject(Empresa::class.java)
                empresas.add(empresa)
            }
            val lstEmpresas = this.findViewById<RecyclerView>(R.id.lstEmpresas)
            lstEmpresas.layoutManager = LinearLayoutManager(this)
            val adapter = ListaEmpresaAdapter()
            adapter.listaEmpresa = empresas
            adapter.setRecyclerViewItemListener(this)

        }?.addOnFailureListener { exception ->

            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()

        }
    }

    override fun recicleViewItemClicked(view: View, id: String) {
        Log.i("MainActivity", "O usuário $id foi clicado")
        empresaDAO.obter(id).addOnSuccessListener {
            val empresa = it.toObject(Empresa::class.java)
        }
    }

    fun obter_empresas_de_usuario(user: String): ArrayList<Empresa> {
        var empresas = arrayListOf<Empresa>()

        empresaDAO.obter_empresas_de_usuario(user).addOnSuccessListener {
            Log.i("MainActivity", "Obtendo empresas de usuário $user")

            for (documento in it) {
                var empresa = documento.toObject(Empresa::class.java)
                empresas.add(empresa)
            }
        }

        return empresas
    }
}

// 45m da aula 10 de fundamentos kotlin ------15/09 08:26