package com.inensus.android

import com.inensus.android.di.DaggerAppComponent
import com.inensus.android.util.SharedPreferencesWrapper
import com.inensus.android.util.Utils
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class InensusApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .create(this)
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        SharedPreferencesWrapper.getInstance(this)

        Utils.checkConnectivity(this)
    }
}