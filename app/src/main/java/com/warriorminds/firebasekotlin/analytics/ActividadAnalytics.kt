package com.warriorminds.firebasekotlin.analytics

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_analytics.*
import java.util.*

class ActividadAnalytics : AppCompatActivity() {

    private var analytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_analytics)

        analytics = FirebaseAnalytics.getInstance(this)
        botonEventoCompra.setOnClickListener {
            enviarEventoCompra()
        }

        botonEventoPropio.setOnClickListener {
            enviarEventoPropio()
        }

        botonGrupo.setOnClickListener {
            enviarPropiedadUsuario()
        }
    }

    private fun enviarEventoCompra() {
        val informacion = Bundle()
        informacion.putString(FirebaseAnalytics.Param.CURRENCY, "MXN")
        informacion.putDouble(FirebaseAnalytics.Param.VALUE, 50.0)
        informacion.putString(FirebaseAnalytics.Param.TRANSACTION_ID, "1234567")
        analytics?.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, informacion)
    }

    private fun enviarEventoPropio() {
        val informacion = Bundle()
        val random = Random(System.currentTimeMillis())
        informacion.putInt(FirebaseAnalytics.Param.VALUE, random.nextInt(1000))
        informacion.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "prueba")
        analytics?.logEvent("analytics_prueba", informacion)
    }

    private fun enviarPropiedadUsuario() {
        val grupos = arrayOf("Beatles", "Pink Floyd", "Led Zeppelin")
        val random = Random(System.currentTimeMillis())
        analytics?.setUserProperty("grupo_musical_favorito", grupos[random.nextInt(3)])
    }
}
