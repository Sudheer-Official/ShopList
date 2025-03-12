package uk.ac.tees.mad.shoplist.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ShopListApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG) // Optional: Log Koin activities
            androidContext(this@ShopListApplication)
            modules(appModules)
        }
    }
}