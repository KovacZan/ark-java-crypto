package examples.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.arkecosystem.client.Connection;
import org.arkecosystem.crypto.configuration.Network;
import org.arkecosystem.crypto.identities.Address;
import org.arkecosystem.crypto.networks.Testnet;
import org.arkecosystem.crypto.transactions.builder.TransferBuilder;
import org.arkecosystem.crypto.transactions.types.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transfer {

    public static long getNonce(Connection connection, String senderWallet) throws IOException {
        Map<String, Object> json =  connection.api().wallets.show(senderWallet);
        Map<String, Object> data = (Map<String, Object>) json.get("data");
        String nonce = data.get("nonce").toString();
        return Long.parseLong(nonce);
    }

    public static void main(String[] args) throws IOException {
        Network.set(new Testnet());

        Map<String, Object> map = new HashMap<>();
        map.put("host", "http://localhost:4003/api/");
        map.put("content-type","application/json");

        Connection connection = new Connection(map);

        String passphrase = "clay harbor enemy utility margin pretty hub comic piece aerobic umbrella acquire";
        String address = Address.fromPassphrase(passphrase);

        long nonce = getNonce(connection, address) + 1;

        List<Map<String, ?>> payload = new ArrayList<>();

        Transaction transaction = new TransferBuilder()
            .nonce(nonce)
            .recipient(address)
            .amount(100)
            .sign(passphrase)
            .transaction;


        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("Formatted Transaction JSON");
        System.out.println(gsonPretty.toJson(JsonParser.parseString(transaction.toJson())));
        System.out.println();

        payload.add(transaction.toHashMap());

        System.out.println("Payload response");

        Map<String, Object> postResponse = connection.api().transactions.create(payload);
        Gson mapToJson = new Gson();
        String json = mapToJson.toJson(postResponse);

        System.out.println(gsonPretty.toJson(JsonParser.parseString(json)));
    }

}
