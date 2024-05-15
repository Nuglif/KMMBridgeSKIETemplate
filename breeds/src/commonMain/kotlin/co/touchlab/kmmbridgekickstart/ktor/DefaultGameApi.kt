package co.touchlab.kmmbridgekickstart.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GameContent(
    val id: String,
    val kind: String
)

interface KMMApi {
    suspend fun getGameContent(url: String) : GameContent?
}

class DefaultKMMApi(engine: HttpClientEngine) : KMMApi {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                contentType = ContentType("application", "vnd.nuglif.rubicon.htmlGame+json"),
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
    
    override suspend fun getGameContent(url: String) : GameContent? {
        val response = client.get(url) {
        }
        println("response: $response")

        return response.body()
    }
}
