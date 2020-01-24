package ProjectPlayer

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.prefs.Preferences


class DB {

    private lateinit var stat: Statement
    private lateinit var con: Connection

    private var onSongsChanged: (() -> Unit)? = null
    fun setOnSongsChanged(listener: () -> Unit) {
        onSongsChanged = listener
    }

    private val prefs = Preferences.userRoot().node("/musicPlayerTask");

    private fun connect() {
        val path = File(prefs.get("settingsPath", ""), ("data.db")).toPath()

        con = DriverManager.getConnection("jdbc:sqlite:${path}")
        stat = con.createStatement()
    }

    private fun close() {
        stat.closeOnCompletion()
        con.close()
    }


    fun getSongs(): List<Song> {
        connect()

        //stat.executeUpdate("DROP TABLE IF EXISTS MusicLib")
        stat.executeUpdate(
            """ 
            CREATE TABLE IF NOT EXISTS MusicLib (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title       TEXT,
                    file        TEXT UNIQUE,
                    duration    TEXT,
                    genre       TEXT,
                    artist      TEXT,
                    album       TEXT,
                    releaseYear INTEGER
                    );
        """
        )

        val res = stat.executeQuery("SELECT * FROM  MusicLib")

        fun n(t: Any?): String {
            try {
                if (t as Int? == 0) return ""
            } catch (e: Exception) {
            }

            return t?.toString() ?: ""
        }

        val l = mutableListOf<Song>()
        while (res.next()) {

            val ry = res.getInt("releaseYear")
            l.add(
                Song(
                    res.getInt("id"),
                    n(res.getString("title")),
                    n(res.getString("duration")),
                    n(res.getString("genre")),
                    n(res.getString("artist")),
                    n(res.getString("album")),
                    if (ry == 0) null else ry,
                    res.getString("file")
                )
            )
        }

        close()
        return l
    }

    fun delSong(s: Song) {
        connect()

        val ins = con.prepareStatement("DELETE FROM MusicLib WHERE id = ${s.id};")
        ins.executeUpdate()
        close()

        //onSongsChanged?.invoke()
    }

    fun addSongs(ss: Collection<Song>) {
        connect()
        for (s in ss) {
            val ins = con.prepareStatement(
                "INSERT INTO MusicLib(title, file, duration, genre, artist, album, releaseYear) values(?,?,?,?,?,?,?)"
            )

            ins.setString(1, s.title)
            ins.setString(2, s.file)
            ins.setString(3, s.duration)
            ins.setString(4, s.genre)
            ins.setString(5, s.artist)
            ins.setString(6, s.album)

            if (s.releaseYear == null) {
                ins.setInt(7, 0)
            } else {
                ins.setInt(7, s.releaseYear)
            }

            try {
                ins.executeUpdate()
            } catch (e: Exception) {
                println(e)
            }

        }
        close()


        onSongsChanged?.invoke()
    }

    fun addTestSong() {
        connect()
        val ins = con.prepareStatement(
            "INSERT INTO MusicLib(title, file, duration, genre, artist, album, releaseYear) values(?,?,?,?,?,?,?)"
        )
        ins.setString(1, "TOPHAMHAT-KYO Princess♂.mp3")
        ins.setString(2, "D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3")
        ins.setString(3, null)
        ins.setString(4, null)
        ins.setString(5, "Tophamhatkyo")
        ins.setString(6, "Tophamhatkyo")
        ins.setString(7, "2019")

        ins.executeUpdate()
        try {
            ins.executeUpdate()
        } catch (e: Exception) {
            println(e)
        }

        close()
    }
}
