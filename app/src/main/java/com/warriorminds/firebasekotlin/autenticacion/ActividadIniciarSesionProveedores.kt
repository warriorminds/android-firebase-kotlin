package com.warriorminds.firebasekotlin.autenticacion

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_iniciar_sesion_proveedores.*

class ActividadIniciarSesionProveedores : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val CODIGO_INICIAR_SESION_GOOGLE = 2001
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var clienteGoogleApi : GoogleApiClient? = null
    private val manejadorLlamadas : CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.actividad_iniciar_sesion_proveedores)
        botonInicioSesionGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        botonCerrarSesionProveedores.setOnClickListener {
            cerrarSesion()
        }

        inicializarGoogle()
        inicializarFacebook()
    }

    override fun onStart() {
        super.onStart()
        actualizarInterfaz()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        manejadorLlamadas.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_INICIAR_SESION_GOOGLE) {
            val resultado = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (resultado.exception == null && resultado.result != null) {
                iniciarSesionConFirebase(resultado.result)
            } else {
                Toast.makeText(this, getString(R.string.error_iniciar_sesion_google), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    private fun iniciarSesionConFirebase(cuentaGoogle: GoogleSignInAccount?) {
        cuentaGoogle?.let {
            progresoProveedores.visibility = View.VISIBLE
            val credencial = GoogleAuthProvider.getCredential(it.idToken, null)
            firebaseAuth.signInWithCredential(credencial)
                    .addOnCompleteListener {
                        progresoProveedores.visibility = View.GONE
                        if (it.isSuccessful) {
                            actualizarInterfaz()
                        } else {
                            Toast.makeText(this@ActividadIniciarSesionProveedores, getString(R.string.error_inicio_sesion_google), Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun iniciarSesionConFirebase(tokenAcceso: AccessToken?) {
        val credencial = FacebookAuthProvider.getCredential(tokenAcceso?.token!!)
        progresoProveedores.visibility = View.VISIBLE
        firebaseAuth.signInWithCredential(credencial)
                .addOnCompleteListener {
                    progresoProveedores.visibility = View.GONE
                    if (it.isSuccessful) {
                        actualizarInterfaz()
                    } else {
                        LoginManager.getInstance().logOut()
                        Toast.makeText(this@ActividadIniciarSesionProveedores, getString(R.string.error_facebook_firebase), Toast.LENGTH_SHORT).show()
                    }
                }

    }

    private fun inicializarGoogle() {
        val opcionesInicioSesionGoogle = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_cliente_web_id))
                .requestEmail()
                .requestProfile()
                .build()

        clienteGoogleApi = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, opcionesInicioSesionGoogle)
                .build()
    }

    private fun inicializarFacebook() {
        botonInicioSesionFacebook.setReadPermissions("email", "public_profile")
        botonInicioSesionFacebook.registerCallback(manejadorLlamadas, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                iniciarSesionConFirebase(result?.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@ActividadIniciarSesionProveedores, getString(R.string.inicio_sesion_fb_cancelado), Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@ActividadIniciarSesionProveedores, getString(R.string.error_inicio_sesion_fb), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun iniciarSesionGoogle() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(clienteGoogleApi)
        startActivityForResult(intent, CODIGO_INICIAR_SESION_GOOGLE)
    }

    private fun actualizarInterfaz() {
        val sesionIniciada = firebaseAuth.currentUser != null
        contenedorBotones.visibility = if (sesionIniciada) View.GONE else View.VISIBLE
        contenedorSesionIniciada.visibility = if (sesionIniciada) View.VISIBLE else View.GONE
    }

    private fun cerrarSesion() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()
        actualizarInterfaz()
    }
}
