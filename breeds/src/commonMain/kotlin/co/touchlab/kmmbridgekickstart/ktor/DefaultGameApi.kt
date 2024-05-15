package co.touchlab.kmmbridgekickstart.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

@Serializable
data class GameContent(
    val id: String,
    val kind: String
)

interface KMMApi {
    suspend fun getGameContent(url: String) : GameContent?
}

class DefaultKMMApi(
    engine: HttpClientEngine
): KMMApi {
    
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
    }
    
    override suspend fun getGameContent(url: String) : GameContent? {
        return client.get {
            headers {
                append("Accept", "application/vnd.nuglif.rubicon.htmlGame+json")
            }
            url(url)
        }.body()
    }
}
