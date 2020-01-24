package ProjectPlayer

import javafx.scene.control.Slider
import javafx.scene.media.EqualizerBand
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import java.lang.Exception
import java.nio.file.Paths
import kotlin.math.abs


class MusicPlayer(val sliders: Collection<Slider>) {

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
    private var isSongPlaying: Boolean = false

    private lateinit var p: MediaPlayer

    init {
        Thread {
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
                        playerRefresh = false
                        isSongPlaying = false
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
        }.start()
    }

    fun song(pathToSong: String) {
        playerRefresh = false
        totalDuration = Duration.UNKNOWN;
        changePos = false
        isFirstDurationGet = false
        newPos = null
        isSongPlaying = true

        try{p.dispose()}catch (e:Exception){}
        p = MediaPlayer(Media(Paths.get(pathToSong).toUri().toString()));
        p.volume = volume

        sliders.forEachIndexed { i, s ->
            p.audioEqualizer.bands[i].gain = s.value
            s.valueProperty().addListener { _, _, newValue ->
                p.audioEqualizer.bands[i].gain = newValue as Double
            }
        }


        p.audioEqualizer.bands.add(EqualizerBand())
    }

    fun pause() {
        if (!isSongPlaying) return
        playerRefresh = false
        p.pause()
        p.startTime = p.currentTime
    }

    fun play() {
        if (!isSongPlaying) return
        playerRefresh = true
        p.play()
    }

    fun setPos(pos: Double) {
        if (!isSongPlaying) return
        newPos = pos
    }

    fun stop() {
        if (!isSongPlaying) return
        playerRefresh = false
        p.stop()
        p.startTime = Duration(.0)
        onSongPositionChanged?.invoke(.0)
    }

    fun setVolume(v: Double) {
        volume = v;
        if (!isSongPlaying) return
        p.volume = v
    }


}