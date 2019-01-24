package net.corda.workbench.commons

import java.io.File
import java.util.concurrent.TimeUnit



/**
 * A single place to useful Kotlin extension functions
 *
 */

fun String.runCommand() {
    ProcessBuilder(*split(" ").toTypedArray())
            //.directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
}

fun String.runCommand(workingDir: File) {
    ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
}