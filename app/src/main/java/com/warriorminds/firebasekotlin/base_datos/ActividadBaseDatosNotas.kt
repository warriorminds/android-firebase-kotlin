package com.warriorminds.firebasekotlin.base_datos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_base_datos_notas.*

class ActividadBaseDatosNotas : AppCompatActivity() {

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
        
    }
}
