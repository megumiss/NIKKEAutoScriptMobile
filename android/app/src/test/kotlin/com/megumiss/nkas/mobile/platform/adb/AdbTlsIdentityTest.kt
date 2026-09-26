package com.megumiss.nkas.mobile.platform.adb

import java.security.KeyPairGenerator
import java.security.Provider
import java.security.Security
import org.junit.Assert.assertEquals
import org.junit.Test

class AdbTlsIdentityTest {
    @Test
    fun certificateDoesNotUseAndroidsStrippedBcProvider() {
        val previous = Security.getProvider("BC")
        Security.removeProvider("BC")
        val stripped = object : Provider("BC", 1.0, "Android BC without RSA signatures") {}
        Security.addProvider(stripped)
        try {
            val keys = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
            val certificate = AdbTlsIdentity.createCertificate(keys.private, keys.public)
            certificate.verify(keys.public)
            assertEquals(stripped, Security.getProvider("BC"))
        } finally {
            Security.removeProvider("BC")
            if (previous != null) Security.addProvider(previous)
        }
    }
}
