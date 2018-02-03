package com.warriorminds.firebasekotlin.autenticacion

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.dialogo_cambiar_contrasena.*

/**
 * Created by rodrigo on 3/02/18.
 */
class DialogoCambiarContrasena : DialogFragment() {

    var listener: ICambiarContrasena? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is ICambiarContrasena) {
            listener = activity as ICambiarContrasena
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater?.inflate(R.layout.dialogo_cambiar_contrasena, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botonCambiarContrasena.setOnClickListener {
            val contrasenaActual = etContrasenaActual.text.toString()
            val nuevaContrasena = etNuevaContrasena.text.toString()
            val confirmarContraseña = etConfirmarNuevaContrasena.text.toString()

            if (TextUtils.isEmpty(contrasenaActual)) {
                inputLayoutContrasenaActual.error = "Introduce tu contraseña actual."
            } else if (!TextUtils.equals(nuevaContrasena, confirmarContraseña)) {
                inputLayoutConfirmarContrasena.error = "Las contraseñas no son iguales"
            } else if (!TextUtils.isEmpty(nuevaContrasena) && !TextUtils.isEmpty(confirmarContraseña)) {
                inputLayoutConfirmarContrasena.error = ""
                listener?.cambiarContrasena(contrasenaActual, nuevaContrasena)
                dismiss()
            }
        }
    }
}

interface ICambiarContrasena {
    fun cambiarContrasena(contrasenaActual: String, nuevaContrasena: String)
}