package com.warriorminds.firebasekotlin

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.Menu
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.warriorminds.firebasekotlin.almacenamiento.ActividadAlmacenamiento
import com.warriorminds.firebasekotlin.analytics.ActividadAnalytics
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionAnonima
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionCorreo
import com.warriorminds.firebasekotlin.autenticacion.ActividadIniciarSesionProveedores
import com.warriorminds.firebasekotlin.autenticacion.ActividadSesionTelefono
import com.warriorminds.firebasekotlin.base_datos.ActividadBaseDatosNotas
import com.warriorminds.firebasekotlin.configuracion_remota.ActividadConfiguracionRemota
import com.warriorminds.firebasekotlin.notificaciones.ActividadNotificaciones
import kotlinx.android.synthetic.main.actividad_principal.*
import java.lang.Exception

class ActividadPrincipal : AppCompatActivity() {

    private val TAG = ActividadPrincipal::class.java.simpleName
    private val configuracionRemota = FirebaseRemoteConfig.getInstance()

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

        botonAlmacenamiento.setOnClickListener {
            iniciarActividad(ActividadAlmacenamiento::class.java)
        }

        botonBaseDatos.setOnClickListener {
            iniciarActividad(ActividadBaseDatosNotas::class.java)
        }

        botonCrash.setOnClickListener {
            FirebaseCrash.log("Reportando evento desde la app.")    
            throw Exception("Excepción causada para probar Crashlytics.")
        }

        inicializarConfiguracionRemota()
        mostrarActividadConfiguracionRemota()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)

        val menuCompartir = menu.findItem(R.id.menu_compartir)
        val proveedorCompartir = MenuItemCompat.getActionProvider(menuCompartir) as ShareActionProvider
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Texto de prueba")
        proveedorCompartir.setShareIntent(intent)
        return true
    }

    private fun iniciarActividad(claseDeActividad: Class<*>) {
        val intent = Intent(this, claseDeActividad)
        startActivity(intent)
    }

    private fun inicializarConfiguracionRemota() {
        val propiedadesConfigRemota = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        configuracionRemota.setConfigSettings(propiedadesConfigRemota)
        configuracionRemota.setDefaults(R.xml.parametros_default_configuracion_remota)
        obtenerValoresConfiguracionRemota()
    }

    private fun obtenerValoresConfiguracionRemota() {
        var expiracionCache = 3600L
        if (configuracionRemota.info.configSettings.isDeveloperModeEnabled) {
            expiracionCache = 0
        }

        configuracionRemota.fetch(expiracionCache)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        configuracionRemota.activateFetched()
                    }
                }
    }

    private fun mostrarActividadConfiguracionRemota() {
        val mostrarPantalla = configuracionRemota.getBoolean(ActividadConfiguracionRemota.MOSTRAR_PANTALLA)
        if (mostrarPantalla) {
            val titulo = configuracionRemota.getString(ActividadConfiguracionRemota.TITULO)
            val texto = configuracionRemota.getString(ActividadConfiguracionRemota.TEXTO)

            val intent = Intent(this, ActividadConfiguracionRemota::class.java)
            intent.putExtra(ActividadConfiguracionRemota.TITULO, titulo)
            intent.putExtra(ActividadConfiguracionRemota.TEXTO, texto)
            startActivity(intent)
        }
    }
}
