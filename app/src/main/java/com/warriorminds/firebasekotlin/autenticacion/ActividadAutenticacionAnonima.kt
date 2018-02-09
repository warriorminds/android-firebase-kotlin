package com.warriorminds.firebasekotlin.autenticacion

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_autenticacion_anonima.*

class ActividadAutenticacionAnonima : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val manejadorLlamadas: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.actividad_autenticacion_anonima)

        botonCerrarSesionAnonima.setOnClickListener {
            firebaseAuth.signOut()
            LoginManager.getInstance().logOut()
            finish()
        }

        inicializarFacebook()
    }

    override fun onStart() {
        super.onStart()
        botonCerrarSesionAnonima.visibility = View.GONE
        if (firebaseAuth.currentUser == null) {
            iniciarSesionAnonima()
        } else {
            firebaseAuth.currentUser?.let {
                if (it.isAnonymous) {
                    botonInicioSesionFacebookEnlazar.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.usuario_anonimo), Toast.LENGTH_SHORT).show()
                } else {
                    botonCerrarSesionAnonima.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        manejadorLlamadas.onActivityResult(requestCode, resultCode, data)
    }

    private fun inicializarFacebook() {
        botonInicioSesionFacebookEnlazar.setReadPermissions("email", "public_profile")
        botonInicioSesionFacebookEnlazar.registerCallback(manejadorLlamadas, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                enlazarCuenta(result?.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.inicio_sesion_fb_cancelado), Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.error_inicio_sesion_fb), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enlazarCuenta(token: AccessToken?) {
        token?.let {
            val credencial = FacebookAuthProvider.getCredential(it.token)
            firebaseAuth.currentUser?.linkWithCredential(credencial)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            botonCerrarSesionAnonima.visibility = View.VISIBLE
                            botonInicioSesionFacebookEnlazar.visibility = View.GONE
                        } else {
                            Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.error_facebook_firebase), Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun iniciarSesionAnonima() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        botonInicioSesionFacebookEnlazar.visibility = View.VISIBLE
                        Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.sesion_anonima_iniciada), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ActividadAutenticacionAnonima, getString(R.string.error_sesion_anonima), Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
