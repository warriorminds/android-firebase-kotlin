package com.warriorminds.firebasekotlin.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.warriorminds.firebasekotlin.ActividadPrincipal
import com.warriorminds.firebasekotlin.R

/**
 * Created by rodrigo on 11/02/18.
 */
class ServicioDeMensajes : FirebaseMessagingService() {

    private val TAG = ServicioDeMensajes::class.java.simpleName

    override fun onMessageReceived(mensaje: RemoteMessage?) {

        mensaje?.let {
            if (it.data != null && !it.data.isEmpty()) {
                procesarDatos(it.data)
            }
            if (it.notification != null) {
                mostrarNotificacion(it.notification)
            }
        }
    }

    private fun mostrarNotificacion(notificacion: RemoteMessage.Notification?) {
        val canalNotificacion: NotificationChannel? = obtenerCanalNotificacion()
        val constructor: NotificationCompat.Builder = obtenerConstructorNotificacion(canalNotificacion)
        constructor.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificacion?.title)
                .setContentText(notificacion?.body)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val intent = Intent(this, ActividadPrincipal::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        constructor.setContentIntent(pendingIntent)
        val idNotificacion = 100

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manejadorNotificaciones = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manejadorNotificaciones.createNotificationChannel(canalNotificacion)
            manejadorNotificaciones.notify(idNotificacion, constructor.build())
        } else {
            val manejadorNotificaciones = NotificationManagerCompat.from(this)
            manejadorNotificaciones.notify(idNotificacion, constructor.build())
        }
    }

    private fun obtenerCanalNotificacion() : NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var canalNotificacion: NotificationChannel?
            canalNotificacion = NotificationChannel(getString(R.string.notificacion_id_default), "Canal Notificacion", NotificationManager.IMPORTANCE_DEFAULT)
            canalNotificacion.description = "Canal de notificación"
            canalNotificacion.enableLights(true)
            canalNotificacion.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            return canalNotificacion
        }
        return null
    }

    private fun obtenerConstructorNotificacion(canal: NotificationChannel?) =
        if (canal != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationCompat.Builder(this, canal.id)
        else
            NotificationCompat.Builder(this)

    private fun procesarDatos(datos: Map<String, String>) {
        // Simulamos que el procesamiento de estos datos tardará más de 10 segundos.
        // Utilizamos el JobDispatcher en estos casos.
        val manejadorTrabajo = FirebaseJobDispatcher(GooglePlayDriver(this))
        val trabajo = manejadorTrabajo.newJobBuilder()
                .setService(ServicioTrabajo::class.java)
                .setTag("servicio-larga-duracion")
                .setExtras(obtenerBundle(datos))
                .build()
        manejadorTrabajo.schedule(trabajo)
    }

    private fun obtenerBundle(datos: Map<String, String>) : Bundle {
        val bundle = Bundle()
        for (dato in datos.keys) {
            bundle.putString(dato, datos[dato])
        }
        return bundle
    }
}