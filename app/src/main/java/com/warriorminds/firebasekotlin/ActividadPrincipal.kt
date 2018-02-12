package com.warriorminds.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.warriorminds.firebasekotlin.analytics.ActividadAnalytics
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionAnonima
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionCorreo
import com.warriorminds.firebasekotlin.autenticacion.ActividadIniciarSesionProveedores
import com.warriorminds.firebasekotlin.autenticacion.ActividadSesionTelefono
import com.warriorminds.firebasekotlin.notificaciones.ActividadNotificaciones
import kotlinx.android.synthetic.main.actividad_principal.*

class ActividadPrincipal : AppCompatActivity() {

    private val TAG = ActividadPrincipal::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_principal)

        intent.extras?.let {
            for (llave in intent.extras.keySet()) {
                Log.d(TAG, "Llave: $llave, Valor: ${intent.extras[llave]}")
            }
        }

        botonAutenticarCorreo.setOnClickListener {
            iniciarActividad(ActividadAutenticacionCorreo::class.java)
        }

        botonIniciarSesionProveedores.setOnClickListener {
            iniciarActividad(ActividadIniciarSesionProveedores::class.java)
        }

        botonIniciarSesionTelefono.setOnClickListener {
            iniciarActividad(ActividadSesionTelefono::class.java)
        }

        botonIniciarAutenticacionAnonima.setOnClickListener {
            iniciarActividad(ActividadAutenticacionAnonima::class.java)
        }

        botonAnalytics.setOnClickListener {
            iniciarActividad(ActividadAnalytics::class.java)
        }

        botonNotificaciones.setOnClickListener {
            iniciarActividad(ActividadNotificaciones::class.java)
        }
    }

    private fun iniciarActividad(claseDeActividad: Class<*>) {
        val intent = Intent(this, claseDeActividad)
        startActivity(intent)
    }
}
