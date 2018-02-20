package com.warriorminds.firebasekotlin.almacenamiento

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.warriorminds.firebasekotlin.R
import kotlinx.android.synthetic.main.actividad_almacenamiento.*
import java.io.ByteArrayOutputStream

class ActividadAlmacenamiento : AppCompatActivity() {

    private val CODIGO_PERMISOS: Int = 1000
    private val CODIGO_GALERIA: Int = 2001
    private val CODIGO_CAMARA: Int = 3001
    private var uriImagen: Uri? = null
    private var imagenCamara: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_almacenamiento)

        verificarPermisos()
        botonGaleria.setOnClickListener {
            abrirGaleria()
        }

        botonCamara.setOnClickListener {
            abrirCamara()
        }

        botonGuardarImagen.setOnClickListener {
            uriImagen?.let {
                ComprimirImagenUri(contentResolver).execute(uriImagen)
            }
            imagenCamara?.let {
                ComprimirImagenBitmap().execute(imagenCamara)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CODIGO_PERMISOS ->
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                        || grantResults[1] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    botonGaleria.isEnabled = true
                    botonCamara.isEnabled = true
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODIGO_GALERIA && resultCode == Activity.RESULT_OK) {
            uriImagen = data?.data
            imagenCamara = null
            Picasso.with(this).load(uriImagen).into(imagen)
            botonGuardarImagen.isEnabled = true
        } else if (requestCode == CODIGO_CAMARA && resultCode == Activity.RESULT_OK) {
            imagenCamara = data?.extras?.get("data") as Bitmap
            uriImagen = null
            imagen.setImageBitmap(imagenCamara)
            botonGuardarImagen.isEnabled = true
        }
    }

    private fun verificarPermisos() {
        if (!checarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE)
                || !checarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || !checarPermiso(Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA), CODIGO_PERMISOS)
        } else {
            botonGaleria.isEnabled = true
            botonCamara.isEnabled = true
        }
    }

    private fun checarPermiso(permiso: String) =
            ContextCompat.checkSelfPermission(applicationContext, permiso) == PackageManager.PERMISSION_GRANTED

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, CODIGO_GALERIA)
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CODIGO_CAMARA)
    }

    public class ComprimirImagenUri(val contentResolver: ContentResolver)
        : AsyncTask<Uri, Unit, ByteArray?>() {

        private val LIMITE_MB = 5.0
        private val MB = 1000000.0

        override fun doInBackground(vararg params: Uri?): ByteArray? {
            val imagen = MediaStore.Images.Media.getBitmap(contentResolver, params[0])
            return imagen.comprimirImagen(MB, LIMITE_MB)
        }

        override fun onPostExecute(result: ByteArray?) {
            
        }
    }

    public class ComprimirImagenBitmap() : AsyncTask<Bitmap, Unit, ByteArray?>() {
        private val LIMITE_MB = 5.0
        private val MB = 1000000.0

        override fun doInBackground(vararg params: Bitmap?): ByteArray? {
            return params[0]?.comprimirImagen(MB, LIMITE_MB)
        }

        override fun onPostExecute(result: ByteArray?) {

        }
    }
}

fun Bitmap.comprimirImagen(mb: Double, limiteMb: Double): ByteArray? {
    var bytes: ByteArray? = null
    for (i in 1..11) {
        val byteStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100 / i, byteStream)
        bytes = byteStream.toByteArray()
        if (bytes.size / mb < limiteMb) {
            return bytes
        }
    }
    return bytes
}
