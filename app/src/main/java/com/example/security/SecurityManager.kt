package com.example.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.roundToLong

object SecurityManager {

    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val MASTER_SECRET = "ShivaMandirTempleSecurityKey2026!#1008"
    private const val DEFAULT_PIN = "1008"
    
    private var currentPin: String = DEFAULT_PIN
    private var isSessionUnlocked: Boolean = false

    private val secretKeySpec: SecretKeySpec by lazy {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(MASTER_SECRET.toByteArray(StandardCharsets.UTF_8))
        SecretKeySpec(keyBytes, "AES")
    }

    private val ivSpec: IvParameterSpec by lazy {
        // Fixed 16-byte IV for consistent deterministic payload encryption in local Room
        val ivBytes = "ShivMandir2026IV".toByteArray(StandardCharsets.UTF_8)
        IvParameterSpec(ivBytes)
    }

    /**
     * Encrypts plaintext string using AES-256
     */
    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return ""
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            "ENC_ERR:${e.localizedMessage}"
        }
    }

    /**
     * Decrypts AES-256 ciphertext
     */
    fun decrypt(cipherText: String): String {
        if (cipherText.isEmpty()) return ""
        if (cipherText.startsWith("ENC_ERR:")) return "Decryption Error"
        return try {
            val decodedBytes = Base64.decode(cipherText, Base64.NO_WRAP)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            // Fallback if plaintext was stored or decryption fails
            cipherText
        }
    }

    fun verifyPin(pin: String): Boolean {
        return if (pin == currentPin) {
            isSessionUnlocked = true
            true
        } else {
            false
        }
    }

    fun lockSession() {
        isSessionUnlocked = false
    }

    fun isUnlocked(): Boolean = isSessionUnlocked

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (verifyPin(oldPin) && newPin.length == 4 && newPin.all { it.isDigit() }) {
            currentPin = newPin
            return true
        }
        return false
    }

    fun getMaskedPhone(phoneText: String): String {
        val decrypted = decrypt(phoneText)
        if (decrypted.length < 10) return decrypted
        val first3 = decrypted.take(3)
        val last3 = decrypted.takeLast(3)
        return "$first3****$last3"
    }

    fun formatReceiptNo(id: Int): String {
        val padded = id.toString().padStart(4, '0')
        return "SM-2026-$padded"
    }

    fun convertAmountToWords(amount: Double): String {
        val longAmount = amount.roundToLong()
        if (longAmount == 0L) return "Zero Rupees Only"
        
        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        fun numToWords(n: Long): String {
            return when {
                n < 20 -> units[n.toInt()]
                n < 100 -> tens[(n / 10).toInt()] + (if (n % 10 != 0L) " " + units[(n % 10).toInt()] else "")
                n < 1000 -> units[(n / 100).toInt()] + " Hundred" + (if (n % 100 != 0L) " " + numToWords(n % 100) else "")
                n < 100000 -> numToWords(n / 1000) + " Thousand" + (if (n % 1000 != 0L) " " + numToWords(n % 1000) else "")
                n < 10000000 -> numToWords(n / 100000) + " Lakh" + (if (n % 100000 != 0L) " " + numToWords(n % 100000) else "")
                else -> numToWords(n / 10000000) + " Crore" + (if (n % 10000000 != 0L) " " + numToWords(n % 10000000) else "")
            }
        }

        return "Rupees ${numToWords(longAmount)} Only"
    }
}
