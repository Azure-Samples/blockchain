package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.IQueueClient
import com.microsoft.azure.servicebus.Message
import com.natpryce.hamkrest.Matcher
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.serviceBus.readFileAsText
import org.json.JSONObject
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.HashMap


fun loadIngressDataFile(app: String, scenario: String, name: String, substitutions: Map<String, Any?>): String {
    val params = HashMap<String, Any?>(substitutions)
    params["requestId"] = UUID.randomUUID().toString()
    val json = readFileAsText("src/test/resources/datasets/$app/$scenario/ingress/$name", params)
    return json
}

fun loadCordaDataFile(app: String, scenario: String, name: String, substitutions: Map<String, Any?>): String {
    val json = readFileAsText("src/test/resources/datasets/$app/$scenario/corda/$name", substitutions)
    return json
}



fun processMsg(registry: Registry, scenario: String, name: String, substitutions: Map<String, Any?>, returnQueue: IQueueClient = FakeQueueClient()) {
    val json = loadIngressDataFile("refrigeratedTransportation", scenario, name, substitutions)
    //println("Trying: $json")
    val create = Message(json, "application/json")
    IngressMessageProcessor(registry, create, returnQueue).run()
    //println("done")
}

fun messageContent(msg: IMessage): Map<String, Any> {
    val body = msg.body
    val bodyText = String(body, StandardCharsets.UTF_8)
    return JSONObject(bodyText).toMap()
}

fun checkMessageContent(app: String, scenario: String, name: String, substitutions: Map<String, Any?>, msg: IMessage): String {
    val json = readFileAsText("src/test/resources/datasets/$app/$scenario/egress/$name", substitutions)
    val expected = JSONObject(json).toMap()
    expected.remove("requestId")

    val body = msg.body
    val bodyText = String(body, StandardCharsets.UTF_8)
    val actual = JSONObject(bodyText).toMap()
    actual.remove("requestId")

    return compareMaps(actual, expected)
}

fun contains(r: Regex): Matcher<String> = Matcher(::_contains, r)

private fun _contains(s: CharSequence, regex: Regex): Boolean = regex.containsMatchIn(s)


//fun _messageContentMatcher(scenario: String, name: String, substitutions: Map<String, Any?>) : KFunction2<IMessage,Boolean>{
//
//}

fun compareMaps(actual: Map<String, Any?>, expected: Map<String, Any>, nesting: String = ""): String {
    val sb = StringBuilder()
    for (k in expected.keys) {
        if (!actual.containsKey(k)) {
            sb.append("entry for $k is missing in actual\n")
        }
        val vA = actual[k]
        val vE = expected[k]
        if (vA != null) {
            if (vA != vE) {
                sb.append("$k[$vA] doesn't match expected $vE\n")
            }
        } else {
            if (vE != null) {
                sb.append("$k[$vA] doesn't match expected value of null\n")
            }
        }

    }
    return sb.toString()
}


class DataSetHelper(val app: String, val scenario: String,
                    val defaultSubstitutions: Map<String, Any?> = emptyMap()) {

    /**
     * return the raw message text from the test file
     */
    fun rawIngressMessage(name: String, substitutions: Map<String, Any?> = emptyMap()): String {
        return loadIngressDataFile(app, scenario, name, merge(substitutions))
    }

    /**
     * return the raw  text from the test file holding the fake response from
     * the Corda flow
     */
    fun rawCordaResponse(name: String, substitutions: Map<String, Any?> = emptyMap()): String {
        return loadCordaDataFile(app, scenario, name, merge(substitutions))
    }

    /**
     * return a message object built from the data in the test file
     */
    fun ingressMessage(name: String, substitutions: Map<String, Any?> = emptyMap()): IMessage {
        val json = rawIngressMessage(name, substitutions)
        return Message(json, "application/json")
    }

    /**
     * Check the actual message against the stored expectation file
     */
    fun checkEgressMessage(actual: IMessage, name: String, substitutions: Map<String, Any?> = emptyMap()): String {
        return checkMessageContent(app, scenario, name, merge(substitutions), actual)
    }


    private fun merge(substitutions: Map<String, Any?>): Map<String, Any?> {
        if (substitutions.isEmpty()) {
            return defaultSubstitutions
        } else {
            val working = HashMap(defaultSubstitutions)
            return working.plus(substitutions)
        }
    }
}