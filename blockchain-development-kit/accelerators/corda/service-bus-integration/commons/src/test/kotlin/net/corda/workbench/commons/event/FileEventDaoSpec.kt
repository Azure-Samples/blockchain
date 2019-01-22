package eventstore.ianmorgan.github.io

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmpty
import net.corda.workbench.commons.event.Event
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.event.Filter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(JUnitPlatform::class)
object EventDaoSpec : Spek({

    describe("Reading from a DAO") {

        val dao = FileEventStore().load("src/test/resources/events/examples")
        
        it("should load events from a given directory") {

            assert.that(dao.retrieve().size, equalTo(3)) // number of example events
            assert.that(dao.retrieve()[0].type, equalTo("SimpleEvent"))
            assert.that(dao.retrieve()[1].type, equalTo("PayloadEvent"))
            assert.that(dao.retrieve()[2].type, equalTo("AggregateEvent"))
        }

        it("should filter by type") {
            val filtered = dao.retrieve(Filter(type = "SimpleEvent"))

            assert.that(filtered.size, equalTo(1))
            assert.that(filtered[0].type, equalTo("SimpleEvent"))
        }

        it("should filter by aggregate id") {
            val filtered = dao.retrieve(Filter(aggregateId = "123"))

            assert.that(filtered.size, equalTo(1))
            assert.that(filtered[0].type, equalTo("AggregateEvent"))
        }


        it("should only return events after the lastId") {
            val filtered = dao.retrieve(Filter(lastId = UUID.fromString("bed6a10c-ab5a-48bc-9129-60842fe10fd9")))

            assert.that(filtered.size, equalTo(1))
            assert.that(filtered[0].type, equalTo("AggregateEvent"))
        }

        it("should return an empty list if lastId on last event ") {
            val filtered = dao.retrieve(Filter(lastId = UUID.fromString("08ec6bfa-b167-43f3-bd26-f2498fa2e291")))
            assert.that(filtered.isEmpty(), equalTo(true))
        }

        it("should limit results by pageSize") {
            val filtered = dao.retrieve(Filter(pageSize = 2))

            assert.that(filtered.size, equalTo(2))
            assert.that(filtered[0].type, equalTo("SimpleEvent"))
            assert.that(filtered[1].type, equalTo("PayloadEvent"))
        }

    }

    describe("Writing to a DAO") {

        it("should persist new events") {
            // setup:
            val dataDir = ".test/${UUID.randomUUID()}"
            var dao = FileEventStore().load(dataDir)
            val ev1 = Event(type = "TestEvent1")
            val ev2 = Event(type = "TestEvent2")

            // start empty
            assert.that(dao.retrieve(), isEmpty)

            // store and read an event
            dao.storeEvents(listOf(ev1))
            assert.that(dao.retrieve(), equalTo(listOf(ev1)))

            // store and read a second event
            dao.storeEvents(listOf(ev2))
            assert.that(dao.retrieve(), equalTo(listOf(ev1, ev2)))

            // reloading should retrieve stored events
            dao = FileEventStore().load(dataDir)
            assert.that(dao.retrieve(), equalTo(listOf(ev1, ev2)))
        }
    }

    describe("An InMemory onlyDAO") {

        it("should persist new events") {
            // setup:
            var dao = FileEventStore()
            val ev1 = Event(type = "TestEvent1")
            val ev2 = Event(type = "TestEvent2")

            // start empty
            assert.that(dao.retrieve(), isEmpty)

            // store and read an event
            dao.storeEvents(listOf(ev1))
            assert.that(dao.retrieve(), equalTo(listOf(ev1)))

            // store and read a second event
            dao.storeEvents(listOf(ev2))
            assert.that(dao.retrieve(), equalTo(listOf(ev1, ev2)))

            // on reloading should be empty as this is not backed by a directory
            dao = FileEventStore()
            assert.that(dao.retrieve().size, equalTo(0))
        }
    }
})

//class MyTestEvent1 : Event(type = "MyTestEvent") {}
//class MyTestEvent2 : Event(type = "MyTestEven2") {}
//class MyTestEvent3(name: String) : Event(type = "MyTestEven2",
//        payload = mapOf<String, Any?>("name" to name)) {
//
//}


