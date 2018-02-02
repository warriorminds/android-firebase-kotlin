package com.warriorminds.firebasekotlin.autenticacion

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.dialogo_crear_cuenta.*

/**
 * Created by rodrigo on 31/01/18.
 */
class DialogoCrearCuenta : DialogFragment() {

    private var listener : ICrearCuenta? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is ICrearCuenta) {
            listener = activity as ICrearCuenta
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
                              savedInstanceState: Bundle?) = inflater?.inflate(R.layout.dialogo_crear_cuenta, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botonCrearCuenta.setOnClickListener {
            val correo = etCorreo.text.toString()
            val contraseña = etContrasena.text.toString()
            val confirmarContraseña = etVerificarContrasena.text.toString()

            if (!TextUtils.equals(contraseña, confirmarContraseña)) {
                inputLayoutContrasena.error = "Las contraseñas no son iguales"
            } else {
                inputLayoutContrasena.error = ""
                listener?.crearCuenta(correo, contraseña)
                dismiss()
            }
        }
    }
}

interface ICrearCuenta {
    fun crearCuenta(correo: String, contraseña: String)
}