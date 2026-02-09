package com.example.ehefin_mobile.core.security

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Utility class to detect if device is rooted.
 * Performs multiple checks to ensure comprehensive detection.
 */
object RootDetector {

    private val rootPaths = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/system/app/SuperSU.apk",
        "/system/app/SuperSU/SuperSU.apk",
        "/system/etc/.installed_su_daemon",
        "/system/etc/.has_su_daemon",
        "/dev/com.koushikdutta.superuser.daemon/",
        "/system/xbin/daemonsu"
    )

    private val dangerousApps = listOf(
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "eu.chainfire.supersu",
        "com.noshufou.android.su",
        "com.topjohnwu.magisk",
        "com.kingroot.kinguser",
        "com.kingo.root",
        "com.smedialink.oneclickroot",
        "com.zhiqupk.root.global",
        "com.alephzain.framaroot"
    )

    private val dangerousProps = listOf(
        "ro.debuggable",
        "ro.secure"
    )

    /**
     * Main detection method - returns true if device is rooted
     */
    fun isDeviceRooted(context: Context): Boolean {
        return checkRootBinaries() ||
                checkSuCommand() ||
                checkRootApps(context) ||
                checkBuildTags() ||
                checkDangerousProps() ||
                checkRWPaths()
    }

    /**
     * Get detailed root detection results for logging
     */
    fun getDetectionDetails(context: Context): RootDetectionResult {
        return RootDetectionResult(
            rootBinariesFound = checkRootBinaries(),
            suCommandAvailable = checkSuCommand(),
            rootAppsInstalled = checkRootApps(context),
            testKeysBuild = checkBuildTags(),
            dangerousPropsSet = checkDangerousProps(),
            rwSystemPaths = checkRWPaths()
        )
    }

    /**
     * Check 1: Look for common root binary paths
     */
    private fun checkRootBinaries(): Boolean {
        return rootPaths.any { path ->
            File(path).exists()
        }
    }

    /**
     * Check 2: Try to execute 'su' command
     */
    private fun checkSuCommand(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()
            reader.close()
            result != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check 3: Look for installed root management apps
     */
    private fun checkRootApps(context: Context): Boolean {
        val pm = context.packageManager
        return dangerousApps.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Check 4: Check for test-keys in build tags (indicates custom ROM)
     */
    private fun checkBuildTags(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check 5: Check dangerous system properties
     */
    private fun checkDangerousProps(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("getprop", "ro.debuggable"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val debuggable = reader.readLine()
            reader.close()
            debuggable == "1"
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check 6: Check if system partition is mounted as read-write
     */
    private fun checkRWPaths(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("mount")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains("/system") && line!!.contains("rw")) {
                    reader.close()
                    return true
                }
            }
            reader.close()
            false
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Data class to hold detailed root detection results
 */
data class RootDetectionResult(
    val rootBinariesFound: Boolean,
    val suCommandAvailable: Boolean,
    val rootAppsInstalled: Boolean,
    val testKeysBuild: Boolean,
    val dangerousPropsSet: Boolean,
    val rwSystemPaths: Boolean
) {
    val isRooted: Boolean
        get() = rootBinariesFound || suCommandAvailable || rootAppsInstalled ||
                testKeysBuild || dangerousPropsSet || rwSystemPaths

    override fun toString(): String {
        return buildString {
            appendLine("Root Detection Results:")
            appendLine("  - Root Binaries Found: $rootBinariesFound")
            appendLine("  - Su Command Available: $suCommandAvailable")
            appendLine("  - Root Apps Installed: $rootAppsInstalled")
            appendLine("  - Test-Keys Build: $testKeysBuild")
            appendLine("  - Dangerous Props Set: $dangerousPropsSet")
            appendLine("  - RW System Paths: $rwSystemPaths")
            appendLine("  - IS ROOTED: $isRooted")
        }
    }
}
