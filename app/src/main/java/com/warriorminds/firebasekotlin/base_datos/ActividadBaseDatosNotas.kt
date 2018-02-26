package com.warriorminds.firebasekotlin.base_datos

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_base_datos_notas.*

class ActividadBaseDatosNotas : AppCompatActivity() {

    private val referenciaBaseDatos = FirebaseDatabase.getInstance().reference
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_base_datos_notas)

        fab.setOnClickListener {
            startActivity(Intent(this@ActividadBaseDatosNotas, ActividadNuevaNota::class.java))
        }

        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        rvNotas.layoutManager = manager
        val adaptador = NotasAdaptador()
        rvNotas.adapter = adaptador

        listener = ListenerNotas(adaptador)
    }

    override fun onPause() {
        super.onPause()
        referenciaBaseDatos.child("notas").removeEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        obtenerNotas()
    }

    private fun obtenerNotas() {
        val query = referenciaBaseDatos.child("notas").limitToFirst(100)
        query.addValueEventListener(listener)
    }

    class ListenerNotas(val adaptador: NotasAdaptador) : ValueEventListener {

        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(datos: DataSnapshot) {
            val notas = ArrayList<Nota>()
            datos.children.forEach {
                val nota = it.getValue(Nota::class.java) as Nota
                nota.id = it.key
                notas.add(nota)
            }
            adaptador.notas = notas
            adaptador.notifyDataSetChanged()
        }
    }
}
