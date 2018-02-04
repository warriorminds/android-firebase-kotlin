package com.warriorminds.firebasekotlin.autenticacion

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_iniciar_sesion_proveedores.*

class ActividadIniciarSesionProveedores : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val CODIGO_INICIAR_SESION_GOOGLE = 2001
    private var clienteGoogleApi : GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_iniciar_sesion_proveedores)
        botonInicioSesionGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        inicializarGoogle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun iniciarSesionGoogle() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(clienteGoogleApi)
        startActivityForResult(intent, CODIGO_INICIAR_SESION_GOOGLE)
    }
}
