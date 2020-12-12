package examples.kotlin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.arkecosystem.client.Connection
import org.arkecosystem.crypto.configuration.Network
import org.arkecosystem.crypto.identities.Address
import org.arkecosystem.crypto.networks.Testnet
import org.arkecosystem.crypto.transactions.builder.TransferBuilder
import java.io.IOException


object Transfer {

    @Throws(IOException::class)
    fun getNonce(connection: Connection, senderWallet: String?): Long {
        val json = connection.api().wallets.show(senderWallet)
        val data = json["data"] as Map<String, Any>?
        val nonce = data!!["nonce"].toString()
        return nonce.toLong()
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        Network.set(Testnet())

        val map: MutableMap<String, Any> = HashMap()
        map["host"] = "http://localhost:4003/api/"
        map["content-type"] = "application/json"

        val connection = Connection(map)

        val passphrase = "clay harbor enemy utility margin pretty hub comic piece aerobic umbrella acquire"
        val address = Address.fromPassphrase(passphrase)

        val nonce = getNonce(connection, address) + 1

        val transaction = TransferBuilder()
            .nonce(nonce)
            .recipient(address)
            .amount(100)
            .sign(passphrase).transaction

        val payload = listOf(transaction.toHashMap())

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

        println("Formatted Transaction JSON")
        println(gsonPretty.toJson(JsonParser.parseString(transaction.toJson())))
        println()

        println("Payload response")

        val postResponse = connection.api().transactions.create(payload as List<Map<String, *>>)
        val mapToJson = Gson()
        val json = mapToJson.toJson(postResponse)

        println(gsonPretty.toJson(JsonParser.parseString(json)))
    }
}
