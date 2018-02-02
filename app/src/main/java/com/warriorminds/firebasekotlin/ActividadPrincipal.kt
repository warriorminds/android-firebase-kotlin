package com.warriorminds.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionCorreo
import kotlinx.android.synthetic.main.actividad_principal.*

class ActividadPrincipal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_principal)

        botonAutenticarCorreo.setOnClickListener {
            iniciarActividad(ActividadAutenticacionCorreo::class.java)
        }
    }

    private fun iniciarActividad(claseDeActividad: Class<*>) {
        val intent = Intent(this, claseDeActividad)
        startActivity(intent)
    }
}