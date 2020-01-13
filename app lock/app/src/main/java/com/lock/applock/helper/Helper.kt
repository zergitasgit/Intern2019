package com.lock.applock.helper

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class Helper {
    companion object{
         var keyStore: KeyStore? = null
        // Variable used for storing the key in the Android Keystore container
         val KEY_NAME = "androidHive"
         var cipher: Cipher? = null
        @TargetApi(Build.VERSION_CODES.M)
         fun generateKey() {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val keyGenerator: KeyGenerator
            keyGenerator = try {
                KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
                )
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to get KeyGenerator instance", e)
            } catch (e: NoSuchProviderException) {
                throw RuntimeException("Failed to get KeyGenerator instance", e)
            }
            try {
                keyStore!!.load(null)
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT or
                                KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7
                        )
                        .build()
                )
                keyGenerator.generateKey()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            } catch (e: InvalidAlgorithmParameterException) {
                throw RuntimeException(e)
            } catch (e: CertificateException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @TargetApi(Build.VERSION_CODES.M)
          fun cipherInit(): Boolean {
            cipher = try {
                Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to get Cipher", e)
            } catch (e: NoSuchPaddingException) {
                throw RuntimeException("Failed to get Cipher", e)
            }
            return try {
                keyStore!!.load(null)
                val key = keyStore!!.getKey(
                    KEY_NAME,
                    null
                ) as SecretKey
                cipher!!.init(Cipher.ENCRYPT_MODE, key)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}