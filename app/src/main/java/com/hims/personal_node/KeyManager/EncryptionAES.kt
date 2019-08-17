package com.hims.personal_node

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object EncryptionAES {
    internal fun init():String {
        var generator = KeyGenerator.getInstance("AES")
        var random = SecureRandom.getInstance("SHA1PRNG")
        generator.init(256, random);
        var secureKey = generator.generateKey()
        return Base64.encodeToString(secureKey.encoded, Base64.NO_WRAP)
    }

    internal fun encryptAES(plainText: String, key:String): String {
        var decodedkey = Base64.decode(key, Base64.NO_WRAP)
        var secretKey = SecretKeySpec(decodedkey, 0, decodedkey.size, "AES")

        var cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        var encryptedData = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
    }

    internal fun decryptAES(value:String, key:String):String{
        var decodedkey = Base64.decode(key, Base64.NO_WRAP)
        var secretKey = SecretKeySpec(decodedkey, 0, decodedkey.size, "AES")

        var cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        var encryptedData = cipher.doFinal(Base64.decode(value, Base64.NO_WRAP))

        return String(encryptedData)
    }
}