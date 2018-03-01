package com.warriorminds.firebasekotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.firebase.appinvite.FirebaseAppInvite
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.warriorminds.firebasekotlin.almacenamiento.ActividadAlmacenamiento
import com.warriorminds.firebasekotlin.analytics.ActividadAnalytics
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionAnonima
import com.warriorminds.firebasekotlin.autenticacion.ActividadAutenticacionCorreo
import com.warriorminds.firebasekotlin.autenticacion.ActividadIniciarSesionProveedores
import com.warriorminds.firebasekotlin.autenticacion.ActividadSesionTelefono
import com.warriorminds.firebasekotlin.base_datos.ActividadBaseDatosNotas
import com.warriorminds.firebasekotlin.configuracion_remota.ActividadConfiguracionRemota
import com.warriorminds.firebasekotlin.notificaciones.ActividadNotificaciones
import kotlinx.android.synthetic.main.actividad_principal.*
import java.lang.Exception

class ActividadPrincipal : AppCompatActivity() {

    private val TAG = ActividadPrincipal::class.java.simpleName
    private val CODIGO_INVITACION = 4000
    private val configuracionRemota = FirebaseRemoteConfig.getInstance()
    private var ligaDinamica: Uri? = null
    private var anuncio: InterstitialAd? = null
    private var videoAnuncio: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_principal)

        intent.extras?.let {
            for (llave in intent.extras.keySet()) {
                Log.d(TAG, "Llave: $llave, Valor: ${intent.extras[llave]}")
            }
        }

        botonAutenticarCorreo.setOnClickListener {
            iniciarActividad(ActividadAutenticacionCorreo::class.java)
        }

        botonIniciarSesionProveedores.setOnClickListener {
            iniciarActividad(ActividadIniciarSesionProveedores::class.java)
        }

        botonIniciarSesionTelefono.setOnClickListener {
            iniciarActividad(ActividadSesionTelefono::class.java)
        }

        botonIniciarAutenticacionAnonima.setOnClickListener {
            iniciarActividad(ActividadAutenticacionAnonima::class.java)
        }

        botonAnalytics.setOnClickListener {
            iniciarActividad(ActividadAnalytics::class.java)
        }

        botonNotificaciones.setOnClickListener {
            iniciarActividad(ActividadNotificaciones::class.java)
        }

        botonAlmacenamiento.setOnClickListener {
            iniciarActividad(ActividadAlmacenamiento::class.java)
        }

        botonBaseDatos.setOnClickListener {
            iniciarActividad(ActividadBaseDatosNotas::class.java)
        }

        botonCrash.setOnClickListener {
            FirebaseCrash.log("Reportando evento desde la app.")    
            throw Exception("Excepción causada para probar Crashlytics.")
        }

        botonEnviarInvitacion.setOnClickListener {
            enviarInvitacion()
        }

        botonAnuncioIntersticial.setOnClickListener {
            anuncio?.let { it.show() }
        }

        botonVideo.setOnClickListener {
            videoAnuncio?.let {
                if (it.isLoaded) {
                    it.show()
                }
            }
        }

        inicializarConfiguracionRemota()
        mostrarActividadConfiguracionRemota()
        crearLigaDinamica()
        recibiendoLigaDinamica()
        inicializarBanner()
        crearIntersticial()
        crearVideoAnuncio()
    }

    override fun onPause() {
        super.onPause()
        videoAnuncio?.let {
            it.pause(this)
        }
    }

    override fun onResume() {
        super.onResume()
        videoAnuncio?.let { it.resume(this) }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoAnuncio?.let { it.destroy(this) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)

        val menuCompartir = menu.findItem(R.id.menu_compartir)

        ligaDinamica?.let {
            val proveedorCompartir = MenuItemCompat.getActionProvider(menuCompartir) as ShareActionProvider
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"

            intent.putExtra(Intent.EXTRA_SUBJECT, "Ligas dinámicas")
            intent.putExtra(Intent.EXTRA_TEXT, "Esta es una liga dinámica: ${ligaDinamica.toString()}")
            proveedorCompartir.setShareIntent(intent)
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_INVITACION && resultCode == Activity.RESULT_OK) {
            val ids = AppInviteInvitation.getInvitationIds(resultCode, data)
            for (id in ids) {
                Log.d(TAG, "Id invitación: $id")
            }
        }
    }

    private fun iniciarActividad(claseDeActividad: Class<*>) {
        val intent = Intent(this, claseDeActividad)
        startActivity(intent)
    }

    private fun inicializarConfiguracionRemota() {
        val propiedadesConfigRemota = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        configuracionRemota.setConfigSettings(propiedadesConfigRemota)
        configuracionRemota.setDefaults(R.xml.parametros_default_configuracion_remota)
        obtenerValoresConfiguracionRemota()
    }

    private fun obtenerValoresConfiguracionRemota() {
        var expiracionCache = 3600L
        if (configuracionRemota.info.configSettings.isDeveloperModeEnabled) {
            expiracionCache = 0
        }

        configuracionRemota.fetch(expiracionCache)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        configuracionRemota.activateFetched()
                    }
                }
    }

    private fun mostrarActividadConfiguracionRemota() {
        val mostrarPantalla = configuracionRemota.getBoolean(ActividadConfiguracionRemota.MOSTRAR_PANTALLA)
        if (mostrarPantalla) {
            val titulo = configuracionRemota.getString(ActividadConfiguracionRemota.TITULO)
            val texto = configuracionRemota.getString(ActividadConfiguracionRemota.TEXTO)

            val intent = Intent(this, ActividadConfiguracionRemota::class.java)
            intent.putExtra(ActividadConfiguracionRemota.TITULO, titulo)
            intent.putExtra(ActividadConfiguracionRemota.TEXTO, texto)
            startActivity(intent)
        }
    }

    private fun crearLigaDinamica() {
        val ligaDinamica = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://warriorminds.github.io/ligas"))
                .setDynamicLinkDomain("g5b98.app.goo.gl")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(1)
                        .build())
                .setIosParameters(DynamicLink.IosParameters.Builder("com.warriorminds")
                        .setFallbackUrl(Uri.parse("https://warriorminds.github.io/"))
                        .build())
                .setGoogleAnalyticsParameters(DynamicLink.GoogleAnalyticsParameters.Builder()
                        .setSource("compartir")
                        .setMedium("social")
                        .setCampaign("prueba")
                        .build())
                .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Título para redes sociales.")
                        .setDescription("Descripción para redes sociales.")
                        .build())
                .buildShortDynamicLink()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        ligaDinamica = it.result.shortLink
                        invalidateOptionsMenu()
                    }
                }
    }

    private fun recibiendoLigaDinamica() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
                .addOnSuccessListener {
                    var liga: Uri?
                    it?.let {
                        liga = it.link
                        liga?.let {
                            Toast.makeText(this@ActividadPrincipal, "Recibimos la acción ${it.lastPathSegment}", Toast.LENGTH_SHORT).show()
                        }
                        val invitacion = FirebaseAppInvite.getInvitation(it)
                        invitacion?.let {
                            Toast.makeText(this@ActividadPrincipal, "Invitación ${it.invitationId}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Hubo un error al leer la liga dinámica", Toast.LENGTH_SHORT).show()
                }
    }

    private fun enviarInvitacion() {
        val intent = AppInviteInvitation.IntentBuilder("Visita WarriorMinds!")
                .setMessage("Ven a visitar la app de Firebase!")
                .setDeepLink(Uri.parse("https://warriorminds.github.io/invitaciones"))
                .setCustomImage(Uri.parse("https://warriorminds.github.io//images/site-logo.png"))
                .setCallToActionText("WarriorMinds")
                .build()
        startActivityForResult(intent, CODIGO_INVITACION)
    }

    private fun inicializarBanner() {
        banner.loadAd(AdRequest.Builder().build())
        banner.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(errorCode : Int) {

            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

            }

            override fun onAdClosed() {

            }
        }
    }

    private fun crearIntersticial() {
        anuncio = InterstitialAd(this)
        anuncio!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        anuncio!!.loadAd(AdRequest.Builder().build())
        anuncio!!.adListener = object: AdListener() {
            override fun onAdClosed() {
                anuncio!!.loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun crearVideoAnuncio() {
        videoAnuncio = MobileAds.getRewardedVideoAdInstance(this)
        videoAnuncio!!.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
                videoAnuncio!!.loadAd("ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build())
            }

            override fun onRewardedVideoAdLeftApplication() {

            }

            override fun onRewardedVideoAdLoaded() {

            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewarded(recompensa: RewardItem) {
                Toast.makeText(this@ActividadPrincipal, "Recompensa: ${recompensa.amount} de ${recompensa.type}", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedVideoStarted() {

            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {

            }
        }
        videoAnuncio!!.loadAd("ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build())
    }
}
