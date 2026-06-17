package com.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalWindowInfo

/**
 * Desktop replacements for Android-only APIs used throughout the original MainActivity.
 *
 * Text-to-speech and speech-recognition are intentionally no-ops on desktop (the task allows
 * TTS / speech to be stubbed). All public shapes mirror the Android APIs closely so the large
 * UI composables compile with only mechanical edits.
 */

// ---- Text To Speech (no-op) -------------------------------------------------

abstract class UtteranceProgressListener {
    open fun onStart(utteranceId: String?) {}
    open fun onDone(utteranceId: String?) {}
    open fun onError(utteranceId: String?) {}
}

/**
 * Free, dependency-free text-to-speech on Windows: drives the built-in
 * `System.Speech.Synthesis.SpeechSynthesizer` voices via a short-lived
 * PowerShell subprocess. No downloads, no extra Gradle dependencies.
 *
 * The text to speak is written to a UTF-8 temp file and read by PowerShell
 * with `[IO.File]::ReadAllText(...)`, so apostrophes, quotes, asterisks and
 * emoji in Witcher prose can never break the command line or inject code.
 */
class DesktopTts(@Suppress("UNUSED_PARAMETER") context: Any?, onInit: (Int) -> Unit) {

    @Volatile private var currentProcess: Process? = null
    @Volatile private var listener: UtteranceProgressListener? = null

    /** SAPI Rate: -10..10, 0 = normal. Derived from the Android-style rate. */
    @Volatile private var sapiRate: Int = 0

    init {
        onInit(SUCCESS)
    }

    /**
     * Resolve a working PowerShell executable by absolute path, with fallbacks.
     * A jpackaged JVM may not have the bare `powershell` on its PATH, so we prefer
     * the well-known System32 location before falling back to PATH lookups.
     */
    private fun resolvePowerShell(): String {
        val candidates = listOfNotNull(
            System.getenv("SystemRoot")?.let { "$it\\System32\\WindowsPowerShell\\v1.0\\powershell.exe" },
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe"
        )
        for (path in candidates) {
            try {
                if (java.io.File(path).exists()) return path
            } catch (_: Exception) {
            }
        }
        // pwsh.exe (PATH), then powershell.exe (PATH) as a last resort.
        return "pwsh.exe".takeIf { commandOnPath(it) } ?: "powershell.exe"
    }

    private fun commandOnPath(exe: String): Boolean {
        return try {
            val pathEnv = System.getenv("PATH") ?: return false
            pathEnv.split(java.io.File.pathSeparatorChar).any { dir ->
                dir.isNotBlank() && java.io.File(dir.trim(), exe).exists()
            }
        } catch (_: Exception) {
            false
        }
    }

    /** Append a timestamped line to the debug log; never throws. */
    private fun log(message: String) {
        try {
            val dir = java.io.File(System.getProperty("user.home"), ".witcher-quest-wise")
            dir.mkdirs()
            val logFile = java.io.File(dir, "tts.log")
            val ts = java.time.LocalDateTime.now().toString()
            logFile.appendText("$ts  $message${System.lineSeparator()}")
        } catch (_: Exception) {
            // Logging must never break TTS.
        }
    }

    fun setPitch(@Suppress("UNUSED_PARAMETER") pitch: Float) {
        // SpeechSynthesizer has no simple pitch control; intentionally a no-op.
    }

    fun setSpeechRate(rate: Float) {
        // Android: 1.0 = normal, ~0.5 slow, ~2.0 fast. SAPI: -10..10, 0 = normal.
        val mapped = when {
            rate <= 0f -> 0
            rate < 1f -> Math.round((rate - 1f) * 20f) // 0.5 -> -10
            else -> Math.round((rate - 1f) * 10f)       // 2.0 -> +10
        }
        sapiRate = mapped.coerceIn(-10, 10)
    }

    fun setLanguage(@Suppress("UNUSED_PARAMETER") locale: java.util.Locale) {
        // Uses the system default voice; explicit voice selection is not supported here.
    }

    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        this.listener = listener
    }

    fun speak(
        text: String,
        queueMode: Int,
        @Suppress("UNUSED_PARAMETER") params: TtsParams?,
        utteranceId: String?
    ) {
        try {
            // QUEUE_FLUSH: stop anything currently speaking so playback doesn't overlap.
            if (queueMode == QUEUE_FLUSH) {
                killCurrent()
            }

            val cleaned = stripMarkdown(text)
            val ps = resolvePowerShell()
            log("speak() called, len=${cleaned.length}, rate=$sapiRate, ps=$ps")
            if (cleaned.isBlank()) {
                log("blank text, skipping")
                listener?.onStart(utteranceId)
                listener?.onDone(utteranceId)
                return
            }

            val tempFile = java.io.File.createTempFile("wqw_tts_", ".txt")
            tempFile.writeText(cleaned, Charsets.UTF_8)

            val script = buildString {
                append("Add-Type -AssemblyName System.Speech; ")
                append("\$s = New-Object System.Speech.Synthesis.SpeechSynthesizer; ")
                append("\$s.Rate = ").append(sapiRate).append("; ")
                append("\$t = [IO.File]::ReadAllText('")
                append(tempFile.absolutePath.replace("'", "''"))
                append("'); ")
                append("\$s.Speak(\$t)")
            }

            val process = ProcessBuilder(
                ps, "-NoProfile", "-NonInteractive", "-Command", script
            ).redirectErrorStream(true).start()

            currentProcess = process
            try {
                log("process started pid=${process.pid()}")
            } catch (e: Exception) {
                log("process started pid=unknown (${e.message})")
            }
            listener?.onStart(utteranceId)

            // Wait for the process on a background thread so the UI never blocks.
            Thread {
                var exitCode = -1
                var interrupted = false
                try {
                    exitCode = process.waitFor()
                } catch (_: InterruptedException) {
                    // killed by stop()/shutdown()/flush; treated as completion below.
                    interrupted = true
                } finally {
                    try { tempFile.delete() } catch (_: Exception) {}
                    if (currentProcess === process) {
                        currentProcess = null
                    }
                    if (!interrupted) {
                        log("process exited code=$exitCode")
                    }
                    listener?.onDone(utteranceId)
                    if (!interrupted && exitCode != 0) {
                        listener?.onError(utteranceId)
                    }
                }
            }.apply { isDaemon = true; name = "wqw-tts" }.start()
        } catch (e: Exception) {
            // Non-Windows, missing voice, PowerShell unavailable, etc. Never crash.
            log("ERROR: ${e.message}")
            listener?.onError(utteranceId)
        }
    }

    fun stop() {
        killCurrent()
    }

    fun shutdown() {
        killCurrent()
    }

    private fun killCurrent() {
        val p = currentProcess
        currentProcess = null
        if (p != null) {
            try {
                p.destroyForcibly()
            } catch (_: Exception) {
            }
        }
    }

    /** Remove common markdown so symbols aren't read aloud. */
    private fun stripMarkdown(text: String): String {
        return text
            .replace("`", "")
            .replace("*", "")
            .replace("#", "")
            .replace(Regex("(?m)^\\s*[-+]\\s+"), "")
            .trim()
    }

    object Engine {
        const val KEY_PARAM_UTTERANCE_ID = "utteranceId"
    }

    companion object {
        const val SUCCESS = 0
        const val QUEUE_FLUSH = 0
    }
}

/** Mirrors the `Bundle().apply { putString(...) }` shape used for TTS params. */
class TtsParams {
    fun putString(@Suppress("UNUSED_PARAMETER") key: String, @Suppress("UNUSED_PARAMETER") value: String?) {}
}

fun ttsParams(): TtsParams = TtsParams()

// ---- Speech recognition launcher (no-op) ------------------------------------

/** Stand-in for an ActivityResultLauncher; launching does nothing on desktop. */
object DesktopSpeechLauncher {
    fun launch(@Suppress("UNUSED_PARAMETER") intent: Any?) {
        // Voice dictation is unavailable on desktop.
    }
}

// ---- Configuration (window size) --------------------------------------------

class DesktopConfiguration(val screenWidthDp: Int)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun rememberDesktopConfiguration(): DesktopConfiguration {
    val containerSize = LocalWindowInfo.current.containerSize
    // containerSize is in px; approximate dp 1:1 (desktop default density ~1.0).
    return remember(containerSize.width) { DesktopConfiguration(containerSize.width.coerceAtLeast(360)) }
}

// ---- Clipboard --------------------------------------------------------------

fun copyToClipboard(text: String) {
    try {
        val selection = java.awt.datatransfer.StringSelection(text)
        java.awt.Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    } catch (e: Exception) {
        // Headless or no clipboard available; ignore.
    }
}
