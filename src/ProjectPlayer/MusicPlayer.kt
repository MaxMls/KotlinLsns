package ProjectPlayer

import javafx.scene.media.EqualizerBand
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import java.lang.Exception
import java.nio.file.Paths
import kotlin.math.abs


class MusicPlayer {

    private var onSongEnd: (() -> Unit)? = null
    fun setOnSongEnd(listener: () -> Unit) {
        onSongEnd = listener
    }

    // 0.0 - 1.0
    private var onSongPositionChanged: ((newPos: Double) -> Unit)? = null

    fun setOnSongPositionChanged(listener: (newPos: Double) -> Unit) {
        onSongPositionChanged = listener
    }


    private var onTotalTimeChanged: ((newPos: Duration) -> Unit)? = null
    fun setOnTotalTimeChanged(listener: (newPos: Duration) -> Unit) {
        onTotalTimeChanged = listener
    }


    private var playerRefresh = false
    private var totalDuration = Duration.UNKNOWN
    private var changePos = false
    private var isFirstDurationGet = false
    private var newPos: Double? = null
    private var volume: Double = 1.0

    private lateinit var p: MediaPlayer

    fun song(pathToSong: String) {
        playerRefresh = false
        totalDuration = Duration.UNKNOWN;
        changePos = false
        isFirstDurationGet = false
        newPos = null

        p = MediaPlayer(Media(Paths.get(pathToSong).toUri().toString()));
        p.volume = volume

        p.audioEqualizer.bands.add(EqualizerBand())
        val t = Thread {
            while (true) {
                Thread.sleep(200)
                try {
                    if (!playerRefresh || p.status != MediaPlayer.Status.PLAYING) continue

                    if (!isFirstDurationGet) {
                        if (p.totalDuration != Duration.UNKNOWN) {
                            totalDuration = p.totalDuration;
                            onTotalTimeChanged?.invoke(totalDuration)
                            isFirstDurationGet = true
                        } else {
                            continue
                        }
                    }

                    if (abs(p.currentTime.toMillis() - totalDuration.toMillis()) < 0.001) {
                        p.dispose()
                        playerRefresh = false;
                        onSongEnd?.invoke()
                        continue
                    }

                    if (newPos != null) {
                        val g = totalDuration.toMillis() * newPos!!
                        p.stop()
                        p.startTime = Duration(g)
                        p.play()
                        newPos = null
                    } else {
                        onSongPositionChanged?.invoke(p.currentTime.toMillis() / totalDuration.toMillis())
                    }
                } catch (e: Exception) {
                    break
                }
            }
        }
        t.start()
    }

    fun pause() {
        playerRefresh = false
        p.pause()
        p.startTime = p.currentTime
    }

    fun play() {
        playerRefresh = true
        p.play()
    }

    // pos: 0.0 - 1.0
    fun setPos(pos: Double) {
        newPos = pos
    }

    fun stop() {
        playerRefresh = false
        p.stop()
        p.startTime = Duration(.0)
        onSongPositionChanged?.invoke(.0)
    }

    fun setVolume(v: Double) {
        volume = v;
        p.volume = v
    }


}