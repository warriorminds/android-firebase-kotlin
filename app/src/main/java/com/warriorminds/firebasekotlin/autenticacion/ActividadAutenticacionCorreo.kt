package com.warriorminds.firebasekotlin.autenticacion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_autenticacion_correo.*

class ActividadAutenticacionCorreo : AppCompatActivity(), ICrearCuenta {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_autenticacion_correo)

        tvRegistrarse.setOnClickListener {
            val dialogo = DialogoCrearCuenta()
            dialogo.show(supportFragmentManager, "dialogo")
        }
    }

    override fun crearCuenta(correo: String, contraseña: String) {

    }
}
