package com.warriorminds.firebasekotlin.notificaciones

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by rodrigo on 11/02/18.
 */
class ServicioDeToken : FirebaseInstanceIdService() {

    private val TAG = ServicioDeToken::class.java.simpleName

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Token: $token")
        Log.d(TAG, "Creada el ${FirebaseInstanceId.getInstance().creationTime}")
    }
}