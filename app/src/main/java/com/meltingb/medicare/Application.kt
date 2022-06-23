package com.meltingb.medicare

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.meltingb.medicare.di.appModules
import com.meltingb.medicare.utils.AppPreference
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@Application)
            modules(appModules)
        }
        AppPreference.init(applicationContext)
    }
}