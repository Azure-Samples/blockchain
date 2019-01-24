# Task Manager 

The taskManger package implements a simple framework for running arbitrary sets of commands as a simple Task. 

Each Task simply has an id and an exec method

```kotlin
interface Task {
    val taskID: UUID
    fun exec(executionContext: ExecutionContext = ExecutionContext())
}
``` 

The ExecutionContext is a simple abstraction that provides three different "sink" for consuming messages 
and the output of processes. 

* outputSink -  application output (standard out on *nix)
* errorSink -   application error (standard error on *nix)
* messageSink - basic logging and status messages from the Task itself


By default ExecutionContext simple echo all of these to the console.

The basic pattern for a Task is below. Optional messages via messageSink should be minimal.

```kotlin
class TestTask : BaseTask() {
    override fun exec(executionContext: ExecutionContext) {
        executionContext.messageSink.invoke("some useful message...")
        // do something
    }
}
```



A Task that fails should raise an exception

```kotlin
class FailingTask : BaseTask() {
    override fun exec(executionContext: ExecutionContext) {
        throw RuntimeException("forced an error")
    }
}
``` 




 
  

