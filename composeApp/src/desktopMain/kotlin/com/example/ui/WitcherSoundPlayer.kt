package com.example.ui

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.math.sin

object WitcherSoundPlayer {
    @Volatile
    private var isAmbientPlaying = false
    private var ambientThread: Thread? = null

    // Plays a finished mono 16-bit PCM short buffer through a SourceDataLine (blocking on caller's thread).
    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val format = AudioFormat(sampleRate.toFloat(), 16, 1, true, false) // signed, little-endian
            val line = AudioSystem.getSourceDataLine(format)
            line.open(format, buffer.size * 2)
            line.start()
            val bytes = shortsToLittleEndianBytes(buffer)
            line.write(bytes, 0, bytes.size)
            line.drain()
            line.stop()
            line.close()
        } catch (e: Exception) {
            // Ignore if audio output is unavailable
        }
    }

    private fun shortsToLittleEndianBytes(samples: ShortArray): ByteArray {
        val bytes = ByteArray(samples.size * 2)
        for (i in samples.indices) {
            val s = samples[i].toInt()
            bytes[i * 2] = (s and 0xFF).toByte()
            bytes[i * 2 + 1] = ((s shr 8) and 0xFF).toByte()
        }
        return bytes
    }

    // Synthesize a majestic Witcher medal chime: two notes in a rising fifth chord (A4 to E5)
    fun playQuestUpdateChime() {
        Thread {
            try {
                val sampleRate = 44100
                val durationS = 0.5
                val numSamples = (durationS * sampleRate).toInt()
                val sample = DoubleArray(numSamples)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Envelopes: attack-decay
                    val env = if (t < 0.08) {
                        t / 0.08 // sharp attack
                    } else {
                        1.0 - (t - 0.08) / 0.42 // decay fading out
                    }

                    // Dual frequencies for rich medieval harmonic progression (rising perfect fifth chord)
                    val freq1 = 440.0 // A4
                    val freq2 = 659.25 // E5 (Perfect fifth)

                    val waveValue = 0.6 * sin(2 * Math.PI * freq1 * t) +
                            0.4 * sin(2 * Math.PI * freq2 * t) +
                            0.15 * sin(2 * Math.PI * (freq2 * 1.5) * t) // adding a rich third harmonic

                    sample[i] = waveValue * env * 0.35
                    buffer[i] = (sample[i] * 32767).toInt().coerceIn(-32768, 32767).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Ignore if device output is unavailable or locked
            }
        }.start()
    }

    // Synthesize a continuous deep rumbling/rustling fantasy forest ambient wind sound
    fun startAmbientWind() {
        if (isAmbientPlaying) return
        isAmbientPlaying = true

        ambientThread = Thread {
            var line: SourceDataLine? = null
            try {
                val sampleRate = 22050
                val bufferSize = sampleRate / 4 // 0.25 second chunks
                val buffer = ShortArray(bufferSize)

                val format = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
                line = AudioSystem.getSourceDataLine(format)
                line.open(format, bufferSize * 2)
                line.start()

                var phase = 0.0
                var filterState = 0.0

                while (isAmbientPlaying) {
                    for (i in 0 until bufferSize) {
                        // Generate soft brown-ish noise (filtered random white noise)
                        val white = (Math.random() * 2.0 - 1.0)
                        filterState = 0.94 * filterState + 0.06 * white

                        phase += 0.0005
                        // Slowly modulate frequency/amplitude of wind gusts to create rich rise and fall
                        val windMod = sin(phase) * 0.5 + 0.5
                        val windGust = filterState * windMod * 0.18

                        // Deep atmospheric drone simulating high mountain winds or cavern echo (approx 48 Hz to 72 Hz)
                        val droneFreq = 55.0 + sin(phase * 0.3) * 15.0
                        val drone = sin(2 * Math.PI * droneFreq * (i.toDouble() / sampleRate)) * 0.05 * (sin(phase * 1.5) * 0.25 + 0.75)

                        val mixed = (windGust + drone).coerceIn(-1.0, 1.0)
                        buffer[i] = (mixed * 32767).toInt().toShort()
                    }
                    val bytes = shortsToLittleEndianBytes(buffer)
                    line.write(bytes, 0, bytes.size)
                }

                line.drain()
                line.stop()
                line.close()
            } catch (e: Exception) {
                isAmbientPlaying = false
                try { line?.close() } catch (_: Exception) {}
            }
        }
        ambientThread?.start()
    }

    fun stopAmbientWind() {
        isAmbientPlaying = false
        ambientThread = null
    }

    // Synthesize a triumphant, golden success chime for completing quests (rising major triad arpeggio chord)
    fun playQuestCompleteChime() {
        Thread {
            try {
                val sampleRate = 44100
                val durationS = 0.8
                val numSamples = (durationS * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate

                    // Envelope with soft attack and nice ringing decay
                    val env = if (t < 0.12) {
                        t / 0.12
                    } else {
                        (1.0 - (t - 0.12) / 0.68).coerceIn(0.0, 1.0)
                    }

                    // Frequencies of a proud major triad: A4 (440Hz), C#5 (554.37Hz), E5 (659.25Hz), A5 (880Hz)
                    val freq1 = 440.0
                    val freq2 = 554.37
                    val freq3 = 659.25
                    val freq4 = 880.0

                    val waveValue = 0.3 * sin(2 * Math.PI * freq1 * t) +
                            0.25 * sin(2 * Math.PI * freq2 * t) +
                            0.25 * sin(2 * Math.PI * freq3 * t) +
                            0.2 * sin(2 * Math.PI * freq4 * t)

                    // Add a heavy chest-hum representing Geralt's wolf amulet vibrating
                    val amuletHum = 0.15 * sin(2 * Math.PI * 65.0 * t)

                    val mixed = (waveValue + amuletHum) * env * 0.4
                    buffer[i] = (mixed * 32767).toInt().coerceIn(-32768, 32767).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Return gracefully if audio device is unavailable
            }
        }.start()
    }

    // Synthesize a custom metallic blade clash/sword-sweep when a vulnerability is inspected
    fun playVulnerabilityClickSound() {
        Thread {
            try {
                val sampleRate = 44100
                val durationS = 0.25
                val numSamples = (durationS * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate

                    // Decaying envelope
                    val env = (1.0 - t / durationS).coerceIn(0.0, 1.0)

                    // Downward fast pitch frequency sweep (blade slice)
                    val sweepFreq = 3000.0 - (t * 5000.0)
                    val slice = sin(2 * Math.PI * maxOf(200.0, sweepFreq) * t) * 0.35

                    // High metallic resonance ring (silver coin clink)
                    val resonance = sin(2 * Math.PI * 1950.0 * t) * 0.2

                    // Whispering air noise
                    val windNoise = (Math.random() * 2.0 - 1.0) * 0.12

                    val mixed = (slice + resonance + windNoise) * env * 0.3
                    buffer[i] = (mixed * 32767).toInt().coerceIn(-32768, 32767).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Return gracefully
            }
        }.start()
    }

    // Synthesize a bubbling potion brewing/simmering sound
    fun playBrewingSound() {
        Thread {
            try {
                val sampleRate = 44100
                val durationS = 0.9
                val numSamples = (durationS * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate

                    // Sizzling liquid backdrop (high-pass filtered random noise)
                    val env = (1.0 - t / durationS).coerceIn(0.0, 1.0)
                    val sizzleNoise = (Math.random() * 2.0 - 1.0) * 0.08

                    // Periodic bubble pops: high frequency pops at specific sub-intervals
                    val popTimer = (t * 10.0).toInt() % 2
                    val popFreq = 700.0 + sin(t * 180.0) * 400.0
                    val pop = if (popTimer == 0 && (i % 5500 < 600)) {
                        sin(2 * Math.PI * popFreq * t) * 0.4
                    } else {
                        0.0
                    }

                    val mixed = (sizzleNoise + pop) * env * 0.35
                    buffer[i] = (mixed * 32767).toInt().coerceIn(-32768, 32767).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Return gracefully
            }
        }.start()
    }
}
