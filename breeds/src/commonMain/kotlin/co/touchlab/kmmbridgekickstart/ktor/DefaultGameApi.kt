package co.touchlab.kmmbridgekickstart.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


class DefaultKMMApi(engine: HttpClientEngine) : KMMApi {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                contentType = ContentType(
                    contentType = "application",
                    contentSubtype = "vnd.nuglif.rubicon.htmlGame+json"
                ),
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

    override suspend fun getGameContent(url: String) = client.get(url).body<GameContent>()
}


interface KMMApi {
    suspend fun getGameContent(url: String): GameContent?
}


object VisualSerializer : JsonContentPolymorphicSerializer<Visual>(Visual::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Visual> {
        val jsonObject = element.jsonObject
        return when (val content = jsonObject["kind"]?.jsonPrimitive?.content) {
            "photo" -> Visual.PhotoVisual.serializer()
            "video" -> Visual.VideoVisual.serializer()
            else -> throw IllegalArgumentException("Unknown argument $content")
        }
    }
}

@Serializable
data class GameContent(
    val id: String,
    val kind: String,
    val styles: Styles,
    val game: GameWeb,
    val buttonText: TextObject,
    val header: GameHeader? = null,
    val ad: GameAdInfo? = null,
    val visual: Visual.PhotoVisual? = null,
    val label: TextObject? = null,
    val text: TextObject? = null,
)

@Serializable
data class GameWeb(
    val url: String,
    val type: String,
    val supportedVersions: JSBridgeSupportedVersions? = null,
)

@Serializable
data class JSBridgeSupportedVersions(
    val min: String?,
    val max: String?,
)

@Serializable
data class GameAdInfo(
    val tagUrl: String,
    val imageUrl: String?,
    val message: String?,
    val description: String?,
)

@Serializable
data class GameHeader(val title: TextObject)

@Serializable
data class TextObject(
    val text: String,
    val styles: Styles? = null,
    val color: String? = null,
)

@Serializable
data class Styles(
    val color: String? = null,
    val fontSize: Int? = null,
    val fontFamily: String? = null,
    val backgroundColor: String? = null,
    val padding: String? = null,
    val textTransform: String? = null,
    val borderWidth: String? = null,
    val borderColor: String? = null,
    val textDecoration: TextDecoration = TextDecoration.NONE,
) {
    @Serializable
    enum class TextDecoration {
        UNDERLINE,
        LINE_THROUGH,
        NONE;
    }
}

@Serializable(with = VisualSerializer::class)
sealed class Visual {
    abstract val kind: String
    abstract val url: String

    @Serializable
    data class PhotoVisual(
        override val kind: String, override val url: String
    ) : Visual()

    @Serializable
    data class VideoVisual(
        override val kind: String,
        override val url: String,
        val thumbnail: String,
        val displayedDuration: Int,
        val control: Boolean? = null
    ) : Visual()
}
