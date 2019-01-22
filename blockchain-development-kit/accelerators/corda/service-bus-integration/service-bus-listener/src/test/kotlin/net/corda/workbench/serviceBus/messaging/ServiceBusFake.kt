package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.*
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService


class FakeQueueClient : IQueueClient {

    val messages = ArrayList<IMessage>()
    override fun registerMessageHandler(p0: IMessageHandler?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerMessageHandler(p0: IMessageHandler?, p1: ExecutorService?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerMessageHandler(p0: IMessageHandler?, p1: MessageHandlerOptions?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerMessageHandler(p0: IMessageHandler?, p1: MessageHandlerOptions?, p2: ExecutorService?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun complete(p0: UUID?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendAsync(p0: IMessage?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeAsync(p0: UUID?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPrefetchCount(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeAsync(): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelScheduledMessageAsync(p0: Long): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun abandon(p0: UUID?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun abandon(p0: UUID?, p1: MutableMap<String, Any>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetterAsync(p0: UUID?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetterAsync(p0: UUID?, p1: MutableMap<String, Any>?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetterAsync(p0: UUID?, p1: String?, p2: String?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetterAsync(p0: UUID?, p1: String?, p2: String?, p3: MutableMap<String, Any>?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendBatchAsync(p0: MutableCollection<out IMessage>?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scheduleMessageAsync(p0: IMessage?, p1: Instant?): CompletableFuture<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReceiveMode(): ReceiveMode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelScheduledMessage(p0: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetter(p0: UUID?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetter(p0: UUID?, p1: MutableMap<String, Any>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetter(p0: UUID?, p1: String?, p2: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deadLetter(p0: UUID?, p1: String?, p2: String?, p3: MutableMap<String, Any>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getQueueName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPrefetchCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scheduleMessage(p0: IMessage?, p1: Instant?): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendBatch(p0: MutableCollection<out IMessage>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun abandonAsync(p0: UUID?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun abandonAsync(p0: UUID?, p1: MutableMap<String, Any>?): CompletableFuture<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun send(p0: IMessage) {
        messages.add(p0)
    }

    override fun getEntityPath(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerSessionHandler(p0: ISessionHandler?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerSessionHandler(p0: ISessionHandler?, p1: ExecutorService?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerSessionHandler(p0: ISessionHandler?, p1: SessionHandlerOptions?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerSessionHandler(p0: ISessionHandler?, p1: SessionHandlerOptions?, p2: ExecutorService?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}