package net.corda.workbench.commons.event

import org.json.JSONObject
import java.io.File
import java.util.ArrayList
import java.util.Collections

/**
 * A simple file based event store with an in memory cache. Good for
 * simple use cases with not too many events
 */
class FileEventStore : EventStore {
    private var directory: String? = null
    private val events = ArrayList<Event>()

    /**
     * Used to load a set of events from JSON files stored on disk. Any new events
     * will also ve written to this directory
     */
    fun load(directory: String): FileEventStore {
        this.directory = directory

        if (File(directory).exists()) {
            // using extension function walk
            File(directory).walk().forEach {
                if (it.name.endsWith(".json")) {
                    val content = it.readText()
                    val json = JSONObject(content)
                    events.add(Event.ModelMapper.fromJSON(json))
                }
            }
        } else {
            File(directory).mkdirs()
        }

        return this;
    }

    /**
     * Stores (saves) the provided events.
     */
    override fun storeEvents(events: List<Event>) {
        synchronized(this) {
            var internalId = this.events.size + 1

            for (ev in events) {
                if (directory != null) {
                    val json = Event.ModelMapper.toJSON(ev).toString(2)
                    val prefix = "%06d".format(internalId)
                    File("$directory/$prefix-event.json").writeText(json)
                    internalId++
                }

                this.events.add(ev)
            }
        }
    }

    /**
     * Retrieves all events, without any filtering.
     */
    override fun retrieve(): List<Event> {
        return this.events
    }

    /**
     * Drop all events (similar to TRUNCATE TABLE in the SQL world)
     *
     */
    fun truncate() {
        events.clear()
    }

    /**
     * Retrieves events with filtering applied
     */
    override fun retrieve(filter: Filter): List<Event> {
        if (filter.lastId != null) {
            for (i in events.indices) {
                if (events[i].id == filter.lastId) {// not the last event
                    val filtered = this.events
                            .subList(i + 1, events.size)
                            .filter { it -> matchesFilter(it, filter) }

                    if (filter.pageSize != null) {
                        return filtered.take(filter.pageSize)
                    } else {
                        return filtered
                    }
                }
            }
            return Collections.emptyList()
        } else {
            val filtered = this.events.filter { it -> matchesFilter(it, filter) }
            if (filter.pageSize != null) {
                return filtered.take(filter.pageSize)
            } else {
                return filtered
            }
        }
    }

    private fun matchesFilter(ev: Event, filter: Filter): Boolean {
        val matchedType = if (filter.type != null) (ev.type == filter.type) else true
        val matchedAggregate = if (filter.aggregateId != null) (ev.aggregateId == filter.aggregateId) else true

        return matchedType && matchedAggregate
    }
}
