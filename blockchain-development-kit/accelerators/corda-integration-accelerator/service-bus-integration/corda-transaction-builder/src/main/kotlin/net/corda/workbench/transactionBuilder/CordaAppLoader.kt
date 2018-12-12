package net.corda.workbench.transactionBuilder

import com.fasterxml.jackson.core.JsonProcessingException
import io.github.classgraph.ClassGraph
import io.github.classgraph.Resource
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger
import java.nio.charset.StandardCharsets
import java.util.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import kotlin.collections.HashMap

class CordaAppLoader {
    private val configs = ArrayList<CordaAppConfig>()
    private val lookup = HashMap<String, CordaAppConfig?>()

    companion object {
        private val logger: Logger = loggerFor<CordaAppLoader>()
    }

    /**
     * Scan CordApps for config files
     */
    fun scan(customerLoader: ClassLoader? = null): CordaAppLoader {
        configs.clear()

        val classGraph = ClassGraph()
        if (customerLoader != null) {
            classGraph.addClassLoader(customerLoader)
        }
        classGraph.whitelistPathsNonRecursive("META-INF/services")
                .scan()
                .use { scanResult ->
                    scanResult.getResourcesWithExtension("json")
                            .forEachByteArray { res: Resource,
                                                content: ByteArray ->
                                processJson(res.path, String(content, StandardCharsets.UTF_8), res.classpathElementFile)
                            }
                }
        return this
    }

    /**
     * All loaded files
     */
    fun allApps(): List<CordaAppConfig> {
        return configs
    }

    /**
     * Find a matching app by id or slug or null not found
     */
    fun findApp(path: String): CordaAppConfig? {
        if (lookup.containsKey(path)) {
            return lookup.get(path)
        } else {

            var result: CordaAppConfig?
            result = configs.firstOrNull { it.id.toString().equals(path, true) }
            if (result == null) {
                result = configs.firstOrNull { path.equals(it.slug, ignoreCase = true) }
            }
            if (result == null) {
                result = configs.firstOrNull { checkSlugs(path, it.slugs) }
            }
            lookup.put(path, result)
            return result
        }

    }

    private fun checkSlugs(path: String, slugs: List<String>?): Boolean {
        if (slugs != null) {
            for (slug in slugs) {
                if (path.equals(slug, ignoreCase = true)) return true
            }
        }
        return false
    }

    private fun processJson(fileName: String, json: String, sourceFile: File) {
        val mapper = ObjectMapper().registerModule(KotlinModule())

        if ("META-INF/services/net.corda.workbench.Registry.json" == fileName) {
            logger.info("Loading workbench config from ${sourceFile.name}")

            try {
                val config = mapper.readValue<CordaAppConfig>(json, CordaAppConfig::class.java)
                configs.add(config)
            } catch (jsonEx: JsonProcessingException) {
                logger.error("JsonProcessingException: ", jsonEx)
            } catch (ex: Exception) {
                logger.error("Exception: ", ex)
            }
        }
    }


}

/**
 * All the config that can be held in the JSON
 */
data class CordaAppConfig(val id: UUID,
                          val name: String,
                          val scannablePackages: List<String>,
                          val summary: String? = null,
                          val version: String? = null,
                          val slug: String? = null,
                          val slugs: List<String>? = null,
                          val authors: List<String> = emptyList(),
                          val url: String? = null)


