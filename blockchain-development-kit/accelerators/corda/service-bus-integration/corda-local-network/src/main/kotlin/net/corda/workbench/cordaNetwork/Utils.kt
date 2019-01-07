package net.corda.workbench.cordaNetwork

import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

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

fun isSocketAlive(host: String, port: Int): Boolean {
    var isAlive = false
    val socketAddress = InetSocketAddress(host, port)
    val socket = Socket()
    try {
        socket.connect(socketAddress, 1000)
        isAlive = true
    } catch (steIgnored: SocketTimeoutException) {
    } catch (ioeIgnored: IOException) {
    }

    return isAlive
}
