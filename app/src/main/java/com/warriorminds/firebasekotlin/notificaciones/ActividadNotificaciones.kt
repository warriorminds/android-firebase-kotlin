package com.warriorminds.firebasekotlin.notificaciones

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessaging
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_notificaciones.*

class ActividadNotificaciones : AppCompatActivity() {

    private val firebaseMessaging = FirebaseMessaging.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_notificaciones)

        switchDeportes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                firebaseMessaging.subscribeToTopic("deportes")
            } else {
                firebaseMessaging.unsubscribeFromTopic("deportes")
            }
        }

        switchEntretenimiento.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                firebaseMessaging.subscribeToTopic("entretenimiento")
            } else {
                firebaseMessaging.unsubscribeFromTopic("entretenimiento")
            }
        }

        switchNoticias.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                firebaseMessaging.subscribeToTopic("noticias")
            } else {
                firebaseMessaging.unsubscribeFromTopic("noticias")
            }
        }
    }
}
