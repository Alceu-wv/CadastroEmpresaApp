package br.edu.infnet.firebasetest

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class EmpresaDAO {

    private val COLLECTION = "empresa"
    private val db = FirebaseFirestore.getInstance()

    fun setUpEmpresaSnapshotListener(
        listener:
            (
            QuerySnapshot?,
            FirebaseFirestoreException?
            ) -> Unit
    ) = db
        .collection(COLLECTION)
        .addSnapshotListener(listener)

    fun inserir(empresa: Empresa): Task<Void>? {
        var task: Task<Void>? = null
        if(empresa.id == null) {
            val ref: DocumentReference = db.collection(COLLECTION).document()
            empresa.id = ref.id
            task = ref.set(empresa)
        }
        return task
    }

    fun listar(): Task<QuerySnapshot>? {
        return db.collection(COLLECTION).get()
    }

    fun obter(id: String): Task<DocumentSnapshot> {
        return db.collection(COLLECTION).document(id).get()
    }

    fun deletar(id: String): Task<Void> {
        return db.collection(COLLECTION).document(id).delete()
    }

    fun obter_empresas_de_usuario(user: String): Task<QuerySnapshot> {
        return db.collection(COLLECTION).whereEqualTo("userId", user).get()
    }
}