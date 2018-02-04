package com.warriorminds.firebasekotlin.autenticacion

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.dialogo_recuperar_contrasena.*

/**
 * Created by rodrigo on 31/01/18.
 */
class DialogoRecuperarContrasena : DialogFragment() {

    private var listener : IRecuperarContrasena? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is IRecuperarContrasena) {
            listener = activity as IRecuperarContrasena
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
                              savedInstanceState: Bundle?) = inflater?.inflate(R.layout.dialogo_recuperar_contrasena, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botonRecuperar.setOnClickListener {
            val correo = etCorreoRecuperar.text.toString()

            if (!TextUtils.isEmpty(correo)) {
                listener?.recuperarContrasena(correo)
                dismiss()
            }
        }
    }
}

interface IRecuperarContrasena {
    fun recuperarContrasena(correo: String)
}