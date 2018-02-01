package com.warriorminds.firebasekotlin.autenticacion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_autenticacion_correo.*

class ActividadAutenticacionCorreo : AppCompatActivity(), ICrearCuenta {

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_autenticacion_correo)

        tvRegistrarse.setOnClickListener {
            val dialogo = DialogoCrearCuenta()
            dialogo.show(supportFragmentManager, "dialogo")
        }

        botonCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            mostrarInfoUsuario(null)
        }
    }

    override fun onStart() {
        super.onStart()
        mostrarInfoUsuario(firebaseAuth.currentUser)
    }

    override fun crearCuenta(correo: String, contraseña: String) {
        progreso.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener {
                    progreso.visibility = View.GONE
                    if (it.isSuccessful) {
                        mostrarInfoUsuario(firebaseAuth.currentUser)
                    } else {
                        Toast.makeText(this@ActividadAutenticacionCorreo, getString(R.string.hubo_error), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun mostrarInfoUsuario(usuario: FirebaseUser?) {
        usuario?.let {
            contenedorIniciarSesion.visibility = View.GONE
            contenedorOpcionesSesion.visibility = View.GONE
            botonCerrarSesion.visibility = View.VISIBLE
        }

        if (usuario == null) {
            contenedorIniciarSesion.visibility = View.VISIBLE
            contenedorOpcionesSesion.visibility = View.VISIBLE
            botonCerrarSesion.visibility = View.GONE
        }
    }
}
