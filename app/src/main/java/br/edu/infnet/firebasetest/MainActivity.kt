package br.edu.infnet.firebasetest

import android.content.Intent
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

class MainActivity : AppCompatActivity(), RecyclerViewItemListener {

    val criptografador = Criptografador()
    private lateinit var mUser: FirebaseUser
    private lateinit var empresaDAO: EmpresaDAO
    private lateinit var mAuth: FirebaseAuth

    private lateinit var txtName: EditText
    private lateinit var txtAdress: EditText
    private lateinit var txtComments: EditText
    private lateinit var checkBoxIsApproved: CheckBox
    private lateinit var btnSalvar: Button
    private lateinit var btnExit: Button


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lstEmpresas = this.findViewById<RecyclerView>(R.id.lstEmpresas)
        lstEmpresas.layoutManager = LinearLayoutManager(this)
        val adapter = ListaEmpresaAdapter(this)
        lstEmpresas.adapter = adapter

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        this.atualizarLista()

        empresaDAO = EmpresaDAO()

        txtName = this.findViewById(R.id.txtName)
        txtAdress = this.findViewById(R.id.txtAdress)
        txtComments = this.findViewById(R.id.txtComments)
        checkBoxIsApproved = this.findViewById(R.id.checkBoxIsApproved)
        btnSalvar = this.findViewById(R.id.btnSalvar)
        btnExit = this.findViewById(R.id.btnExit)

        empresaDAO.setUpEmpresaSnapshotListener { querySnapshot, firebaseFirestoreException ->
            Log.i("MainActivity", "------empresaDAO.setUpEmpresaSanpshotListener-------")
            if (querySnapshot != null) {
                for (documento in querySnapshot) {
                    var empresa = documento.toObject(Empresa::class.java)
                    Log.i("MainActivity", "${empresa.id} - ${empresa.name}")
                }
            }
        }

//        val database = FirebaseDatabase.getInstance()
//        val myref = database.getReference("empresa")
//        myref.addValueEventListener(object: ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                val empresas = obter_empresas_de_usuario(mUser!!.uid)


//              FIXME: val empresaAdress = criptografador.descriptografar(empresa!!.adress!!)
//                if (empresa != null) {
//                    val empresaAdress = empresa!!.adress!!
//                    lblTexto.append("${empresa!!.name} - ${empresa!!.comments} - ${empresaAdress} - ${empresa!!.is_approved}\n")
//                }
//            }

//            override fun onCancelled(error: DatabaseError) {
//
//                Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG)
//            }
//        })

        btnExit.setOnClickListener {
            this.exit()
        }

        btnSalvar.setOnClickListener {
            val empresa = Empresa()

            empresa.userId = mUser!!.uid
            empresa.name = txtName.text.toString()
            empresa.comments = txtComments.text.toString()
            empresa.adress = criptografador.criptografar(txtAdress.text.toString())
            empresa.is_approved = checkBoxIsApproved.isChecked

            if (empresa?.name == null || empresa?.comments == null || empresa.adress == null) {
                Toast.makeText(this.applicationContext, "Preencha todos os campos", Toast.LENGTH_LONG)
            } else {
                empresaDAO.inserir(empresa)?.addOnSuccessListener {
                    Log.i("MainActivity", "Empresa inserida")
                }?.addOnFailureListener {
                    Log.i("MainActivity", "Erro ao inserir empresa")
                }

                txtAdress.setText(null)
                txtComments.setText(null)
                txtName.setText(null)

                this.atualizarLista()
            }
        }
    }

    private fun atualizarLista() {
        EmpresaDAO().obter_empresas_por_usuario(this.mUser.uid)!!.addOnSuccessListener { listaDeDocumentos ->

            val empresas = ArrayList<Empresa>()
            for(documento in listaDeDocumentos) {

                var empresa = documento.toObject(Empresa::class.java)
                empresas.add(empresa)
            }
            val lstEmpresas = this.findViewById<RecyclerView>(R.id.lstEmpresas)
            lstEmpresas.layoutManager = LinearLayoutManager(this)
            val adapter = ListaEmpresaAdapter(this)
            adapter.listaEmpresa = empresas
            adapter.setRecyclerViewItemListener(this)
            lstEmpresas.adapter = adapter

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

    override fun editCLicked(view: View, position: Int, empresa: Empresa) {
        txtName.setText(empresa.name)
        txtAdress.setText(empresa.adress)
        txtComments.setText(empresa.comments)
    }

    override fun deleteClicked(view: View, position: Int, empresa: Empresa) {
        empresaDAO.deletar(empresa.id!!).addOnSuccessListener {
            Log.i("MainActivity", "Empresa ${empresa.name} deletada")
        }
        this.atualizarLista()
    }

    fun exit() {
        mAuth.signOut()
        val intent = Intent(this@MainActivity, Login::class.java)
        startActivity(intent)
    }
}