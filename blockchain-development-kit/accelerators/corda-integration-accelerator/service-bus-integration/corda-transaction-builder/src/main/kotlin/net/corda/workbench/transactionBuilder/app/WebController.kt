package net.corda.workbench.transactionBuilder.app

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import io.javalin.ApiBuilder
import io.javalin.Javalin
import net.corda.core.utilities.loggerFor
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.transactionBuilder.readFileAsText
import org.slf4j.Logger
import java.io.FileInputStream


/**
 * A basic UI
 */
class WebController(private val registry: Registry) {

    private val logger: Logger = loggerFor<WebController>()

    fun register() {
        val app = registry.retrieve(Javalin::class.java)

        app.routes {
            ApiBuilder.get("/") { ctx ->
                ctx.redirect("/web/index")
            }
        }

        ApiBuilder.path("web") {

            app.routes {
                ApiBuilder.get("index") { ctx ->
                    val page = renderTemplate("index.md", mapOf("name" to "Corda"))
                    ctx.contentType("text/html")
                    ctx.result(page)
                }
            }
        }
    }


}

fun renderTemplate(path: String, params: Map<String, Any?> = emptyMap()): String {
    // mustache processing
    val content = readFileAsText("src/main/resources/www/$path", params)

    // markdown processing
    val options = MutableDataSet()
    val parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
    val document = parser.parse(content)
    val html = renderer.render(document)

    // merge with layout
    val layout = FileInputStream("src/main/resources/www/layout").bufferedReader().use { it.readText() }
    return layout.replace("BODYTEXT", html, false)

}