package com.warriorminds.firebasekotlin.base_datos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_nota.*

class ActividadNota : AppCompatActivity() {

    companion object {
        val ID_NOTA = "idNota"
    }

    private val referencia = FirebaseDatabase.getInstance().reference
    private var listenerNota: ListenerNota? = null
    private var listenerComentarios: ListenerComentarios? = null
    private var idNota: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_nota)

        idNota = intent.getStringExtra(ID_NOTA)
        listenerNota  = ListenerNota(tvTituloNotaDetalles, tvContenidoNotaDetalles, tvAutorNotaDetalles)
        val adaptadorComentarios = ComentariosAdaptador()
        listenerComentarios = ListenerComentarios(adaptadorComentarios)
        rvComentarios.layoutManager = LinearLayoutManager(this)
        rvComentarios.adapter = adaptadorComentarios

        botonEnviarComentario.setOnClickListener {
            if (TextUtils.isEmpty(etComentario.text)) {
                tiComentario.error = "Debes ingresar un comentario."
            } else {
                tiComentario.error = ""
                enviarComentario()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        referencia.child("notas").child(idNota).addValueEventListener(listenerNota)
        referencia.child("comentarios").child(idNota).addChildEventListener(listenerComentarios)
    }

    override fun onPause() {
        super.onPause()
        referencia.child("notas").child(idNota).removeEventListener(listenerNota)
        referencia.child("comentarios").child(idNota).removeEventListener(listenerComentarios)
    }

    private fun enviarComentario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            referencia.child("usuarios").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(datos: DataSnapshot) {
                    val usuario = datos.getValue(Usuario::class.java)
                    usuario?.let {
                        val comentario = Comentario(it.nombreUsuario, etComentario.text.toString(), uid)
                        referencia.child("comentarios").child(idNota).push().setValue(comentario)
                        etComentario.text.clear()
                    }
                }
            })
        }
    }

    private class ListenerNota(val tvTitulo: TextView,
                               val tvContenido: TextView,
                               val tvAutor: TextView) : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(datos: DataSnapshot) {
            val nota = datos.getValue(Nota::class.java)
            nota?.let {
                tvTitulo.text = it.titulo
                tvContenido.text = it.texto
                tvAutor.text = it.autor
            }
        }
    }

    private class ListenerComentarios(val adaptador: ComentariosAdaptador) : ChildEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

        }

        override fun onChildAdded(datos: DataSnapshot, nombrePrevio: String?) {
            val comentario = datos.getValue(Comentario::class.java)
            comentario?.let {
                adaptador.idsComentarios.add(datos.key)
                adaptador.comentarios.add(it)
                adaptador.notifyItemInserted(adaptador.itemCount - 1)
            }
        }

        override fun onChildRemoved(datos: DataSnapshot) {
            val idComentario = datos.key
            val posicionComentario = adaptador.idsComentarios.indexOf(idComentario)
            if (posicionComentario > -1) {
                adaptador.idsComentarios.removeAt(posicionComentario)
                adaptador.comentarios.removeAt(posicionComentario)
                adaptador.notifyItemRemoved(posicionComentario)
            }
        }
    }
}
