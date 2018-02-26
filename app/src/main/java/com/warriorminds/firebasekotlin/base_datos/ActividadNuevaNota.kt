package com.warriorminds.firebasekotlin.base_datos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_nueva_nota.*

class ActividadNuevaNota : AppCompatActivity() {

    private val referenciaBaseDatos = FirebaseDatabase.getInstance().reference
    private val referenciaAutenticacion = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_nueva_nota)

        if (referenciaAutenticacion.currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fabCrearNota.setOnClickListener {
            if (TextUtils.isEmpty(etTituloNota.text)) {
                tiTituloNota.error = "Debes agregar un título a la nota."
                tiContenidoNota.error = ""
            } else if (TextUtils.isEmpty(etContenidoNota.text)) {
                tiContenidoNota.error = "Debes agregar contenido a la nota."
                tiTituloNota.error = ""
            } else {
                tiContenidoNota.error = ""
                tiTituloNota.error = ""
                crearNota()
            }
        }
    }

    private fun crearNota() {
        val uid = referenciaAutenticacion.currentUser?.uid

        referenciaBaseDatos.child("usuarios").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(datos: DataSnapshot) {
                val usuario = datos.getValue(Usuario::class.java)
                if (usuario == null) {
                    etTituloNota.isEnabled = false
                    etContenidoNota.isEnabled = false
                    Toast.makeText(this@ActividadNuevaNota, getString(R.string.usuario_inexistente), Toast.LENGTH_SHORT).show()
                } else {
                    agregarNota(usuario, uid)
                }
            }
        })
    }

    private fun agregarNota(usuario: Usuario, uid: String?) {
        uid?.let {
            val idNota = referenciaBaseDatos.child("notas").push().key
            val nota = Nota(usuario.nombreUsuario, etTituloNota.text.toString(),
                    etContenidoNota.text.toString(), it)
            referenciaBaseDatos.child("notas").child(idNota).setValue(nota)
            etTituloNota.isEnabled = true
            etContenidoNota.isEnabled = true
            finish()
        }
    }
}
