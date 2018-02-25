package com.warriorminds.firebasekotlin.base_datos

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.elemento_nota.view.*

/**
 * Created by rodrigo on 23/02/18.
 */
class NotasAdaptador(var notas: List<Nota> = ArrayList()) : RecyclerView.Adapter<NotasAdaptador.NotasViewHolder>() {
    override fun onBindViewHolder(holder: NotasViewHolder, position: Int) = holder.bind(notas[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NotasViewHolder(inflater.inflate(R.layout.elemento_nota, parent, false))
    }

    override fun getItemCount() = notas.size

    class NotasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(nota: Nota) = with(itemView) {
            tvTituloNota.text = nota.titulo
            tvContenidoNota.text = nota.texto
            tvAutorNota.text = nota.autor
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ActividadNota::class.java)
                intent.putExtra(ActividadNota.ID_NOTA, nota.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}