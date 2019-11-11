package com.hims.personal_node

import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object EncryptionRSA{
    const val CRYPTO_METHOD = "RSA"
    const val CRYPTO_TRANSFORM = "RSA/ECB/PKCS1Padding"

    fun PublicKey.key() = Base64.getEncoder().encodeToString(this.encoded)
    fun PrivateKey.key() = Base64.getEncoder().encodeToString(this.encoded)

    fun PublicKeytoString(publicKey: PublicKey):String{
        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }
    fun PrivateKeytoString(privateKey: PrivateKey):String{
        return Base64.getEncoder().encodeToString(privateKey.encoded)
    }

    private fun String.toPublicKey(): PublicKey {
        val keyBytes: ByteArray = Base64.getDecoder().decode(this)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)

        return keyFactory.generatePublic(spec)
    }

    private fun String.toPrivateKey(): PrivateKey {
        val keyBytes: ByteArray = Base64.getDecoder().decode(this)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)

        return keyFactory.generatePrivate(spec)
    }

    private fun StringtoPublicKey(publicKey: String): PublicKey {
        val keyBytes: ByteArray = Base64.getDecoder().decode(publicKey)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)

        return keyFactory.generatePublic(spec)
    }

    private fun StringtoPrivateKey(privateKey: String): PrivateKey {
        val keyBytes: ByteArray = Base64.getDecoder().decode(privateKey)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)

        return keyFactory.generatePrivate(spec)
    }

    fun encrypt(message: String, key: String): String {
        val encryptedBytes: ByteArray
        val pubKey: PublicKey? = key.toPublicKey()
        val cipher: Cipher = Cipher.getInstance(CRYPTO_TRANSFORM)

        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        encryptedBytes = cipher.doFinal(message.toByteArray(StandardCharsets.UTF_8))

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(message: String, key:String): String {
        val decryptedBytes: ByteArray
        val key: PrivateKey? = key.toPrivateKey()
        val cipher: Cipher = Cipher.getInstance(CRYPTO_TRANSFORM)

        cipher.init(Cipher.DECRYPT_MODE, key)
        decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message))

        return String(decryptedBytes)
    }

    fun SignKey(content:String, privateKey: PrivateKey):String{
        var sha = EncryptionSHA.encryptSha(content)
        var sig = Signature.getInstance("SHA1WithRSA")
        sig.initSign(privateKey)
        sig.update(Base64.getDecoder().decode(sha))
        var sign = sig.sign()
        return Base64.getEncoder().encodeToString(sign)
    }

    fun VerifyKey(content:String, publicKey: String, sign:String):Boolean{
        var publicKey = StringtoPublicKey(publicKey)
        var sha = EncryptionSHA.encryptSha(content)
        var sig = Signature.getInstance("SHA1WithRSA")
        sig.initVerify(publicKey)
        sig.update(Base64.getDecoder().decode(sha))
        var sign = Base64.getDecoder().decode(sign)
        return sig.verify(sign)
    }
}