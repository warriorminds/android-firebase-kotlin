package com.warriorminds.firebasekotlin.autenticacion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_autenticacion_anonima.*

class ActividadAutenticacionAnonima : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_autenticacion_anonima)
    }

    override fun onStart() {
        super.onStart()
        botonCerrarSesionAnonima.visibility = View.GONE
        if (firebaseAuth.currentUser == null) {
            iniciarSesionAnonima()
        } else {
            firebaseAuth.currentUser?.let {
                if (it.isAnonymous) {
                    Toast.makeText(this, getString(R.string.usuario_anonimo), Toast.LENGTH_SHORT).show()
                } else {
                    botonCerrarSesionAnonima.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun iniciarSesionAnonima() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.sesion_anonima_iniciada), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.error_sesion_anonima), Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
