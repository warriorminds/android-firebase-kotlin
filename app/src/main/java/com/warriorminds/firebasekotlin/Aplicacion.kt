package com.warriorminds.firebasekotlin

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.twitter.sdk.android.core.Twitter

/**
 * Created by rodrigo on 4/02/18.
 */
class Aplicacion : Application() {

    override fun onCreate() {
        super.onCreate()
        Twitter.initialize(this)
        MobileAds.initialize(this, "ca-app-pub-7034494771274338~8545650534")
    }
}