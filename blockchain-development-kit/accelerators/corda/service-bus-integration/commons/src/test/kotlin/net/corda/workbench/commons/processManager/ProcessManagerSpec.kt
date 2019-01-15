package net.corda.workbench.commons.processManager

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*


@RunWith(JUnitPlatform::class)
object ProcessManagerSpec : Spek({

    describe("Creating and monitoring processes") {

        it("should register a process") {
            val manager = ProcessManager()
            val p = createDirListProcess()
            manager.register(p)

            assertThat(manager.findByProcess(p)?.process, equalTo(p))
            assertThat(manager.findByProcess(createNoOpProcess()), absent())
        }

        it("should find process") {
            val manager = ProcessManager()
            val p = createDirListProcess()
            val id = UUID.randomUUID()
            manager.register(p, id, "myprocess")

            assertThat(manager.findByProcess(p)?.process, equalTo(p))
            assertThat(manager.findById(id)?.process, equalTo(p))
            assertThat(manager.findByLabel("myprocess")?.process, equalTo(p))
            assertThat(manager.findById(id)?.process, equalTo(p))
            assertThat(manager.findByLabel("myprocess")?.process, equalTo(p))
        }

        it("should monitor standard out") {
            val capture = CaptureOutput()
            val manager = ProcessManager(outputSink = { capture.messageSink(it) })
            val p = createPrint3Process()
            val id = UUID.randomUUID()
            manager.register(process = p, id = id)

            assertThat(manager.findByProcess(p)?.monitor, present())
            Thread.sleep(5000)
            assertThat(capture.toString(), equalTo("Welcome 1 times\n" +
                    "Welcome 2 times\n" +
                    "Welcome 3 times\n" +
                    "$id has completed with exit code 0\n"))

        }

        it("should monitor standard error") {
            val capture = CaptureOutput()
            val manager = ProcessManager(errorSink = { capture.messageSink(it) })
            val p = createError3Process()
            val id = UUID.randomUUID()
            manager.register(process = p, id = id)

            assertThat(manager.findByProcess(p)?.monitor, present())
            Thread.sleep(5000)
            assertThat(capture.toString(), equalTo("Goodbye, World 1 times!\n" +
                    "Goodbye, World 2 times!\n" +
                    "Goodbye, World 3 times!\n"))

        }

        it("should capture exit code") {
            val manager = ProcessManager()
            val p = createExitCodeProcess()
            manager.register(p)

            Thread.sleep(3000)
            assertThat(manager.findByProcess(p)!!.monitor.exitCode(), equalTo(123))
        }


        it("should list all processes") {
            val manager = ProcessManager()
            val p1 = createSleepProcess(1)
            manager.register(p1)
            val p2 = createSleepProcess(1)
            manager.register(p2)
            val p3 = createSleepProcess(1)
            manager.register(p3)

            assertThat(manager.allProcesses().size, equalTo(3))
            assertThat(manager.allProcesses().count { it.monitor.isRunning() }, equalTo(3))

            // wait for them to end
            Thread.sleep(3000)
            assertThat(manager.allProcesses().count { it.monitor.isRunning() }, equalTo(0))
        }

        it("should notify when process is completed") {
            val capture = CaptureCompleted()
            val manager = ProcessManager(processCompletedSink = { a,b -> capture.sink(a,b)})
            val p = createExitCodeProcess()
            manager.register(p)

            Thread.sleep(3000)
            assertThat(capture.pm.process, equalTo(p))
            assertThat(capture.code, equalTo(123))

        }

    }
})
