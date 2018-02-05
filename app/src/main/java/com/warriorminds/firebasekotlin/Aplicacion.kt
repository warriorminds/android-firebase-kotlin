package com.warriorminds.firebasekotlin

import android.app.Application
import com.twitter.sdk.android.core.Twitter

/**
 * Created by rodrigo on 4/02/18.
 */
class Aplicacion : Application() {

    override fun onCreate() {
        super.onCreate()
        Twitter.initialize(this)
    }
}