package net.corda.workbench.commons.processManager

import java.lang.StringBuilder

fun createDirListProcess(): Process {

    val process = ProcessBuilder(listOf("ls"))
            .start()
    return process
}

fun createNoOpProcess(): Process {

    return ProcessBuilder(listOf("/usr/bin/false")).start()
}

fun createSleepProcess(duration: Int = 5): Process {
    return ProcessBuilder(listOf("sleep", duration.toString())).start()
}


fun createPrint3Process(): Process {
    return ProcessBuilder(listOf("src/test/resources/bash/print3.sh")).start()
}

fun createError3Process(): Process {
    return ProcessBuilder(listOf("src/test/resources/bash/error3.sh")).start()
}

fun createExitCodeProcess(): Process {
    return ProcessBuilder(listOf("src/test/resources/bash/exitCode.sh")).start()
}

class CaptureOutput {
    var sb = StringBuilder()

    fun messageSink(m: String) {
        sb.appendln(m)
    }

    override fun toString(): String {
        return sb.toString()
    }
}

class CaptureCompleted {
    lateinit var pm: ProcessManager.ManagedProcess
    var code: Int? = null


    fun sink(pm: ProcessManager.ManagedProcess, code: Int) {
        this.pm = pm
        this.code = code
    }
}

fun nullSink(m: String) {}

