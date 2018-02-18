package com.warriorminds.firebasekotlin.configuracion_remota

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_configuracion_remota.*

class ActividadConfiguracionRemota : AppCompatActivity() {

    companion object {
        val MOSTRAR_PANTALLA = "mostrar_pantalla"
        val TITULO = "titulo_pantalla"
        val TEXTO = "texto_pantalla"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_configuracion_remota)

        tvTitulo.text = intent.getStringExtra(TITULO)
        tvTexto.text = intent.getStringExtra(TEXTO)
    }
}
