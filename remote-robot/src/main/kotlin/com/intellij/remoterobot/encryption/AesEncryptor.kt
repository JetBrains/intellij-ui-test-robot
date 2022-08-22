// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.encryption

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import java.security.Key
import java.security.Security
import java.security.spec.KeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AesEncryptor(password: String) : Encryptor {
    private val key: Key = getAESKeyFromPassword(password.toCharArray(), password.toByteArray())

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    override fun encrypt(text: String): String {
        val input = text.toByteArray(charset("UTF8"))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val cipherText = ByteArray(cipher.getOutputSize(input.size))
            var ctLength = cipher.update(
                input, 0, input.size,
                cipherText, 0
            )
            ctLength += cipher.doFinal(cipherText, ctLength)
            return String(
                Base64.encode(cipherText)
            )
        }
    }

    override fun decrypt(text: String): String {
        val input = try {
            Base64.decode(text.trim { it <= ' ' }.toByteArray(charset("UTF8")))
        } catch (e: DecoderException) {
            throw IllegalStateException("Check properties specified: '$ENCRYPTION_ENABLED_KEY' = 'true', '$ENCRYPTION_PASSWORD_KEY' = 'YOUR SECRET PASSWORD'")
        }
        try {
            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
                cipher.init(Cipher.DECRYPT_MODE, key)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (e: BadPaddingException) {
            throw IllegalStateException("Wrong password specified in '$ENCRYPTION_PASSWORD_KEY'")
        }
    }

    private fun getAESKeyFromPassword(password: CharArray?, salt: ByteArray?): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password, salt, 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}