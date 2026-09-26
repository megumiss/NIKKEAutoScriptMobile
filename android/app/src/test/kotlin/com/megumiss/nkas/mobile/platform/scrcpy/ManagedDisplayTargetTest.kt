package com.megumiss.nkas.mobile.platform.scrcpy

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagedDisplayTargetTest {
    private val socket = "nkas-vd-" + "a".repeat(32)
    private val target = ManagedDisplayTarget(19, "abc123def456", socket, "phone", "boot")

    @Test fun existingDisplayDoesNotCreateAnotherDisplay() {
        val command = ScrcpyServerCommand.build("/tmp/server.jar", "4.1", 7, ScrcpyServerOptions(displayId = 19))
        assertTrue(command.contains("display_id=19"))
        assertFalse(command.contains("new_display="))
        target.checkInfo("OK id=19 identity=abc123def456 socket=$socket frames=4")
        target.checkDevice("phone\n", "boot\n")
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsReusedNumericIdFromDifferentSession() {
        target.checkInfo("OK id=19 identity=abc123def456 socket=nkas-vd-${"b".repeat(32)}")
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsDifferentDevice() { target.checkDevice("other-phone", "boot") }

    @Test(expected = IllegalStateException::class)
    fun rejectsRebootedDevice() { target.checkDevice("phone", "next-boot") }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsPrimaryDisplay() { ScrcpyServerOptions(displayId = 0) }
}
