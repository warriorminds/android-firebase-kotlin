package com.warriorminds.firebasekotlin.base_datos

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.elemento_comentario.view.*

/**
 * Created by rodrigo on 24/02/18.
 */
class ComentariosAdaptador(val comentarios: List<Comentario>) : RecyclerView.Adapter<ComentariosAdaptador.ComentariosViewHolder>() {
    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) = holder.bind(comentarios[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ComentariosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.elemento_comentario, parent, false))

    override fun getItemCount() = comentarios.size

    class ComentariosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comentario: Comentario) = with(itemView) {
            tvComentario.text = comentario.comentario
            tvAutorComentario.text = comentario.autor
        }
    }
}