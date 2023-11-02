package onenone.coding.main

import android.app.Application
import onenone.coding.db.DatabaseModule
import onenone.coding.network.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

@ComponentScan
class MainApplication : Application() {

    override fun onCreate() {
        setupKoinDI()
        super.onCreate()
    }

    private fun setupKoinDI() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            defaultModule()
            modules(NetworkModule().module, DatabaseModule().module)
        }
    }
}
