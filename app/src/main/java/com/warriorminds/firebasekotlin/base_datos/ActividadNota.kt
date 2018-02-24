package com.warriorminds.firebasekotlin.base_datos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_nota.*

class ActividadNota : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_nota)

        rvComentarios.layoutManager = LinearLayoutManager(this)
    }
}
