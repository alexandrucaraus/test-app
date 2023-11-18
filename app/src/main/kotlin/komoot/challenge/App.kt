package komoot.challenge

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.defaultModule

@ComponentScan
class App : Application() {

    override fun onCreate() {
        setupKoinDI()
        println("------------------------------------------------------------------------------------------")
        super.onCreate()
    }

    private fun setupKoinDI() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            defaultModule()
        }
    }
}
