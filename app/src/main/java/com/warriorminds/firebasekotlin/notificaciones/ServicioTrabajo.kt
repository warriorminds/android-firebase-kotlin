package com.warriorminds.firebasekotlin.notificaciones

import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

/**
 * Created by rodrigo on 11/02/18.
 */
class ServicioTrabajo : JobService() {

    private val TAG = ServicioTrabajo::class.java.simpleName

    override fun onStartJob(job: JobParameters?): Boolean {
        Log.d(TAG, "Comenzando el trabajo de larga duración.")
        job?.extras?.let {
            for (dato in it.keySet()) {
                Log.d(TAG, "Llave: $dato, Valor: ${it[dato]}")
            }
        }
        stopSelf()
        return false
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }
}