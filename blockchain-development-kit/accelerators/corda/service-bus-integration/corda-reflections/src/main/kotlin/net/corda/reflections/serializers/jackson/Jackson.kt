package net.corda.reflections.serializers.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.Module
import java.io.IOException
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import net.corda.core.contracts.Amount
import net.corda.core.contracts.TokenizableAssetInfo
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.reflections.reflections.ParamMetaData
import java.util.*

object Jackson {
    fun defaultModule(): Module {
        val module = SimpleModule()
        module.addSerializer(Party::class.java, PartySerializer())
        module.addSerializer(UniqueIdentifier::class.java, UniqueIdentifierSerializer())
        module.addSerializer(SignedTransaction::class.java, SignedTransactionSerializer())
        module.addSerializer(ParamMetaData::class.java, ParamMetaDataSerializer())
        module.addSerializer(Amount::class.java, AmountSerializer())
        module.addSerializer(CordaX500Name::class.java, CordaX500NameSerializer())
        return module
    }
}


class PartySerializer @JvmOverloads constructor(t: Class<Party>? = null) : StdSerializer<Party>(t) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
            value: Party, jgen: JsonGenerator, provider: SerializerProvider) {

        jgen.writeString(value.name.x500Principal.toString())

    }
}

//class WireTransactionSerializer @JvmOverloads constructor(t: Class<WireTransaction>? = null) : StdSerializer<WireTransaction>(t) {
//
//    @Throws(IOException::class, JsonProcessingException::class)
//    override fun serialize(
//            value: WireTransaction, jgen: JsonGenerator, provider: SerializerProvider) {
//
//        jgen.writeStartObject()
//        jgen.writeStringField("command", value.commands.first().javaClass.canonicalName)
//        //jgen.writeStringField("itemName", value.itemName)
//        //jgen.writeNumberField("owner", value.owner.id)
//        jgen.writeEndObject()
//
//        //jgen.writeString(value.toString())
//
//    }
//}
//
//class NotaryChangeWireTransactionSerializer @JvmOverloads constructor(t: Class<NotaryChangeWireTransaction>? = null) : StdSerializer<NotaryChangeWireTransaction>(t) {
//
//    @Throws(IOException::class, JsonProcessingException::class)
//    override fun serialize(
//            value: NotaryChangeWireTransaction, jgen: JsonGenerator, provider: SerializerProvider) {
//
//
//        jgen.writeStartObject()
//        jgen.writeStringField("newNotary", value.newNotary.name.toString())
//        //jgen.writeStringField("itemName", value.itemName)
//        //jgen.writeNumberField("owner", value.owner.id)
//        jgen.writeEndObject()
//
//        //jgen.writeString(value.toString())
//
//    }
//}


class SignedTransactionSerializer @JvmOverloads constructor(t: Class<SignedTransaction>? = null) : StdSerializer<SignedTransaction>(t) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
            value: SignedTransaction, jgen: JsonGenerator, provider: SerializerProvider) {


        jgen.writeStartObject()
        if (value.notary != null) {
            jgen.writeStringField("notary", value.notary?.name?.toString())
        }

        jgen.writeStringField("id", value.id.toString())

//        value.
//        jgen.writeStringField("newNotary", value.newNotary.name.toString())
//        //jgen.writeStringField("itemName", value.itemName)
//        //jgen.writeNumberField("owner", value.owner.id)
        jgen.writeEndObject()

        //jgen.writeString(value.toString())

    }
}

class ParamMetaDataSerializer @JvmOverloads constructor(t: Class<ParamMetaData>? = null) : StdSerializer<ParamMetaData>(t) {
    @Throws(IOException::class, JsonProcessingException::class)

    override fun serialize(value: ParamMetaData, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("type", value.kclazz.simpleName)
        gen.writeBooleanField("optional", value.optional)
        gen.writeBooleanField("nullable", value.nullable)
        gen.writeEndObject()
    }
}

class UniqueIdentifierSerializer @JvmOverloads constructor(t: Class<UniqueIdentifier>? = null) : StdSerializer<UniqueIdentifier>(t) {
    @Throws(IOException::class, JsonProcessingException::class)

    override fun serialize(value: UniqueIdentifier, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.toString())
    }
}


class AmountSerializer @JvmOverloads constructor(t: Class<Amount<*>>? = null) : StdSerializer<Amount<*>>(t) {
    @Throws(IOException::class, JsonProcessingException::class)

    override fun serialize(value: Amount<*>, gen: JsonGenerator, provider: SerializerProvider) {
        val token = value.token
        if (token is Currency) {
            gen.writeString("${token.currencyCode}:${value.quantity}")
            return
        }
        if (token is TokenizableAssetInfo) {
            gen.writeString("TOKEN<${token.javaClass.simpleName}>:${value.quantity}")
            return
        }
        gen.writeString("???<${token.javaClass.simpleName}>:${value.quantity}")

    }
}

class CordaX500NameSerializer @JvmOverloads constructor(t: Class<CordaX500Name>? = null) : StdSerializer<CordaX500Name>(t) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
            value: CordaX500Name, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.x500Principal.toString())
    }
}


