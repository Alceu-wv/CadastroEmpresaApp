package br.edu.infnet.firebasetest

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class EmpresaDAO {

    private val collection = "empresa"
    private val db = FirebaseFirestore.getInstance()

    fun inserir(empresa: Empresa): Task<Void>? {
        var task: Task<Void>? = null
        if(empresa.id == null) {
            val ref: DocumentReference = db.collection(collection).document()
            empresa.id = ref.id
            task = ref.set(empresa)
        }
        return task
    }

    fun listar(): Task<QuerySnapshot>? {
        return db.collection(collection).get()
    }
}