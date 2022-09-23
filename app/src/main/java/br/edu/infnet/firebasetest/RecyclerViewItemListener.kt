package br.edu.infnet.firebasetest

import android.view.View

interface RecyclerViewItemListener {

    fun recicleViewItemClicked(view: View, id: String)

    fun editCLicked(view: View, position: Int, empresa: Empresa)

    fun deleteClicked(view: View, position: Int, empresa: Empresa)
}