package br.edu.infnet.firebasetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListaEmpresaAdapter : RecyclerView.Adapter<ListaEmpresaAdapter.ViewHolder> {

    var listaEmpresa = ArrayList<Empresa>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    lateinit var itemListener: RecyclerViewItemListener
    fun setRecyclerViewItemListener(listener: RecyclerViewItemListener) {
        itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.empresa_listrow, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listaEmpresa.get(position), itemListener, position)
    }

    override fun getItemCount(): Int {
        return listaEmpresa.size
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
        }
    }


}