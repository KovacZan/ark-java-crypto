package org.arkecosystem.crypto.identities

import org.arkecosystem.crypto.encoding.Hex
import org.arkecosystem.crypto.encoding.Base58 as B58
import org.arkecosystem.crypto.configuration.Network
import org.bitcoinj.core.*
import org.spongycastle.crypto.digests.RIPEMD160Digest

class Address {
    static String fromPassphrase(String passphrase) {
        fromPrivateKey(PrivateKey.fromPassphrase(passphrase))
    }

    static String fromPublicKey(String publicKey) {
        byte[] publicKeyBytes = Hex.decode(publicKey)

        RIPEMD160Digest digest = new RIPEMD160Digest()
        digest.update(publicKeyBytes, 0, publicKeyBytes.length)
        byte[] out = new byte[20]
        digest.doFinal(out, 0)

        new VersionedChecksummedBytes(Network.get().version(), out).toBase58()
    }

    static String fromPrivateKey(ECKey privateKey) {
        fromPublicKey(privateKey.getPublicKeyAsHex())
    }

    static Boolean validate(String address) {
        B58.decodeChecked(address)[0] == Network.get().version()
    }
}
