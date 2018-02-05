package com.warriorminds.firebasekotlin.autenticacion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_sesion_telefono.*
import java.util.concurrent.TimeUnit

class ActividadSesionTelefono : AppCompatActivity() {

    private val VERIFICACION = "VERIFICACION"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var manejadorLlamadas: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var verificacionEnProceso: Boolean = false
    private var idVerificacion: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_sesion_telefono)

        botonEnviarCodigo.setOnClickListener {
            enviarCodigoVerificacion()
        }

        botonCodigoVerificacion.setOnClickListener {
            verificarTelefono()
        }

        botonCerrarSesionTelefono.setOnClickListener {
            firebaseAuth.signOut()
            contenedorCapturaDatos.visibility = View.VISIBLE
            botonCerrarSesionTelefono.visibility = View.GONE
        }

        inicializar()
    }

    override fun onStart() {
        super.onStart()
        if (verificacionEnProceso) {
            verificarTelefono()
        }
        firebaseAuth.currentUser?.let {
            contenedorCapturaDatos.visibility = View.GONE
            botonCerrarSesionTelefono.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(VERIFICACION, verificacionEnProceso)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            verificacionEnProceso = it.getBoolean(VERIFICACION)
        }
    }

    private fun inicializar() {
        manejadorLlamadas = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credencial: PhoneAuthCredential) {
                verificacionEnProceso = false
                iniciarSesionEnFirebase(credencial)
            }

            override fun onVerificationFailed(excepcion: FirebaseException?) {
                verificacionEnProceso = false
                when (excepcion) {
                    is FirebaseAuthInvalidCredentialsException -> Toast.makeText(this@ActividadSesionTelefono, getString(R.string.numero_incorrecto), Toast.LENGTH_SHORT).show()
                    is FirebaseTooManyRequestsException -> Toast.makeText(this@ActividadSesionTelefono, getString(R.string.verificaciones_excedidas), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this@ActividadSesionTelefono, getString(R.string.error_verificacion), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(idVerificacion: String?, token: PhoneAuthProvider.ForceResendingToken?) {
                this@ActividadSesionTelefono.idVerificacion = idVerificacion
                this@ActividadSesionTelefono.token = token
                Toast.makeText(this@ActividadSesionTelefono, getString(R.string.codigo_verificacion_enviado), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarCodigoVerificacion() {
        val telefono = etNumeroTelefonico.text.toString()
        if (TextUtils.isEmpty(telefono)) {
            layoutTelefono.error = "Debes ingresar un número telefónico."
        } else {
            verificacionEnProceso = true
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    telefono, 60, TimeUnit.SECONDS, this, manejadorLlamadas!!)
        }
    }

    private fun verificarTelefono() {
        val codigo = etCodigoVerificacion.text.toString()
        if (TextUtils.isEmpty(codigo)) {
            layoutCodigoVerificacion.error = "Debes ingresar el código de verificación."
        } else {
            idVerificacion?.let {
                val credencial = PhoneAuthProvider.getCredential(it, codigo)
                iniciarSesionEnFirebase(credencial)
            }
        }
    }

    private fun iniciarSesionEnFirebase(credencial: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credencial)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        contenedorCapturaDatos.visibility = View.GONE
                        botonCerrarSesionTelefono.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this@ActividadSesionTelefono, R.string.error_iniciar_sesion, Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
