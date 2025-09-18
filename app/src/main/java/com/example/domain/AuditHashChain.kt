package com.example.domain

import java.security.MessageDigest

object AuditHashChain {
    fun computeHash(
        previousHash: String,
        eventType: String,
        userId: String,
        action: String,
        timestamp: Long
    ): String = sha256("$previousHash|$eventType|$userId|$action|$timestamp")

    private fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    fun verifyChain(events: List<String>, hashes: List<String>): Boolean {
        if (events.size != hashes.size) return false
        return events.zip(hashes).all { (input, hash) -> sha256(input) == hash }
    }
}
