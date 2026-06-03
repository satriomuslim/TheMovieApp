package com.main.themovieapp

import android.app.Application
import com.main.themovieapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TheMovieApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TheMovieApp)
            modules(appModules)
        }
    }
}