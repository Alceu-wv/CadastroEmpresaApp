package br.edu.infnet.firebasetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListaEmpresaAdapter(itemListener: RecyclerViewItemListener) : RecyclerView.Adapter<ListaEmpresaAdapter.ViewHolder>() {

    var listaEmpresa = ArrayList<Empresa>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    private var itemListener: RecyclerViewItemListener = itemListener

    fun setRecyclerViewItemListener(listener: RecyclerViewItemListener) {
        itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.empresa_listrow, parent, false)
        return ListaEmpresaAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaEmpresa.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listaEmpresa.get(position), itemListener, position)
    }


    // classe intera
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(empresa: Empresa, itemListener: RecyclerViewItemListener, position: Int) {

            val rowName = itemView.findViewById<TextView>(R.id.txtNameRow)
            rowName.setText(empresa.name)
            val rowAdress = itemView.findViewById<TextView>(R.id.txtAdressRow)
            rowAdress.setText(empresa.name)
            val rowComments = itemView.findViewById<TextView>(R.id.txtCommentsRow)
            rowComments.setText(empresa.name)

            itemView.setOnClickListener {
                itemListener.recicleViewItemClicked(it, empresa.id!!)
            }

            val btnEdit = itemView.findViewById<Button>(R.id.btnEdit)
            btnEdit.setOnClickListener {
                itemListener.editCLicked(it, position, empresa)
            }

            val btnDelete = itemView.findViewById<Button>(R.id.btnDelete)
            btnDelete.setOnClickListener {
                itemListener.deleteClicked(it, position, empresa)
            }
        }
    }


}