package net.corda.workbench.serviceBus.executor

import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val rnd = Random()
var counter = 0


class MyTask (val name : String): Runnable{

    override fun run() {
        counter++
        val sleep = rnd.nextInt(10000).toLong()
        println("Starting $name, will take $sleep")
        Thread.sleep(sleep)
        println("done $name")
        counter--
    }

}

fun main(args : Array<String>) {

    val executor = Executors.newFixedThreadPool(1000)

    for (i in 1..1000){
        executor.submit(MyTask("Task #$i"))
    }

    executor.shutdown()
    executor.awaitTermination(10L,TimeUnit.SECONDS)
    println ("Count of active processed $counter")

}