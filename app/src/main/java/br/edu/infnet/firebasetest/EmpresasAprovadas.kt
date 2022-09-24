package br.edu.infnet.firebasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EmpresasAprovadas : AppCompatActivity() {

    private lateinit var btnVoltar: Button
    private lateinit var lstEmpresasAprovadas: ListView
    val empresaDAO: EmpresaDAO = EmpresaDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresas_aprovadas)

        btnVoltar = this.findViewById(R.id.btnVoltar)
        lstEmpresasAprovadas = this.findViewById(R.id.lstEmpresasAprovadas)
        this.atualizarLista()

        btnVoltar.setOnClickListener { this.exit() }

        empresaDAO.setUpEmpresaSnapshotListener { querySnapshot, firebaseFirestoreException ->
            Log.i("EmpresasAprovadas", "------empresaDAO.setUpEmpresaSanpshotListener-------")
            this.atualizarLista()
            }
        }

    private fun atualizarLista() {
        EmpresaDAO().listar()!!.addOnSuccessListener { listaDeDocumentos ->

            val empresas = ArrayList<String>()
            for(documento in listaDeDocumentos) {

                var empresa = documento.toObject(Empresa::class.java)
                if (empresa.name.toString() != null && empresa.name.toString() != "")
                    empresas.add(empresa.name!!.toString())
            }

            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, empresas)
            lstEmpresasAprovadas.adapter = adapter

        }?.addOnFailureListener { exception ->

            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()

        }
    }

    private fun exit() {
        val intent = Intent(this@EmpresasAprovadas, Login::class.java)
        startActivity(intent)
    }
}