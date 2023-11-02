package onenone.coding.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


class AppNetworkClient {
}

@Module
class NetworkModule {

    @Single
    fun networkEngine(): HttpClientEngineFactory<CIOEngineConfig> = CIO

    @Single
    fun networkClient (engine: HttpClientEngineFactory<CIOEngineConfig>): HttpClient = HttpClient(CIO)
}