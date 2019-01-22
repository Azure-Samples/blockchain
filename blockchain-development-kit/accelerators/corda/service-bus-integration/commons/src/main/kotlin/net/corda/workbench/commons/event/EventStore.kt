package net.corda.workbench.commons.event

import java.util.UUID

/**
 * Bare bones repo for very basic event sourcing
 */
interface EventStore {
    /**
     * Store a list of events
     */
    fun storeEvents(events: List<Event>)

    /**
     * Store a single event
     */
    fun storeEvent(event: Event) {
        storeEvents(listOf(event))
    }

    /**
     * Read everything
     */
    fun retrieve(): List<Event>

    /**
     * Retrieves events with filtering applied
     */
    fun retrieve(filter: Filter): List<Event>
}

data class Filter(
        val type: String? = null,  // Comma separated list of event types (the 'type' key) to filter on
        val pageSize: Int? = null,
        val lastId: UUID? = null,  // Typically combined with the 'pageSize' to retrieve from a position within the event stream. Note that this exclusive, i.e. the query will return the matching events directly after this event id
        val aggregateId: String? = null,
        val sessionId: String? = null

) {
    object ModelMapper {

        /**
         * Unpack the map of http query params and build a filter
         */
        fun fromQueryMap(map: Map<String, Array<String>>): Filter {
            val type = safeUnpack(map["type"])
            val pageSize = safeToInteger(safeUnpack(map["pageSize"]))
            val lastId = safeToUUID(safeUnpack(map["lastId"]))
            val aggregateId = safeUnpack(map["aggregateId"])
            val sessionId = safeUnpack(map["sessionId"])

            return Filter(
                    type = type,
                    pageSize = pageSize,
                    lastId = lastId,
                    aggregateId = aggregateId,
                    sessionId = sessionId
            )
        }

        private fun safeToInteger(value: String?): Int? {
            return if (value != null) Integer.parseInt(value) else null
        }

        private fun safeToUUID(value: String?): UUID? {
            return if (value != null) UUID.fromString(value) else null
        }

        private fun safeUnpack(array: Array<String>?): String? {
            if (array != null && array.isNotEmpty()) {
                return array[0]
            }
            return null
        }
    }
}