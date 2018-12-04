package net.corda.workbench.commons.event

import net.corda.workbench.commons.JsonHelper
import org.json.JSONObject
import java.util.UUID

/**
 * Data class to define an Event for a simple event sourcing pattern
 */
data class Event(
        val id: UUID = UUID.randomUUID(),
        val type: String,
        val timestamp: Long = System.currentTimeMillis(),
        val creator: String = "???",
        val aggregateId: String? = null,
        val payload: Map<String, Any?> = emptyMap()
) {

    /**
     * Move between different representations
     */
    object ModelMapper {
        fun asMap(ev: Event): Map<String, Any> {
            val map = mutableMapOf(
                    "id" to ev.id,
                    "type" to ev.type,
                    "creator" to ev.creator,
                    "timestamp" to ev.timestamp,
                    "payload" to ev.payload
            )

            if (ev.aggregateId != null) map["aggregateId"] = ev.aggregateId
            
            return map
        }

        fun fromJSON(json: JSONObject): Event {
            val id = UUID.fromString(json.get("id") as String)
            val type = json.getString("type")
            val creator = json.getString("creator")
            val timestamp = json.get("timestamp") as Long
            val aggregateId = if (json.has("aggregateId")) json.getString("aggregateId") else null
            val payload = if (json.has("payload")) JsonHelper.jsonToMap(json.getJSONObject("payload")) else emptyMap()

            return Event(id = id,
                    type = type,
                    creator = creator,
                    timestamp = timestamp,
                    aggregateId = aggregateId,
                    payload = payload)
        }

        fun toJSON(event: Event): JSONObject = JSONObject(asMap(event))
    }
}




