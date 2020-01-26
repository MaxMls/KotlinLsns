package ProjectPlayer

import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
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

companion object{

    @Throws(URISyntaxException::class, IOException::class)
    fun makeAPICall(uri: String, parameters: List<NameValuePair>): String {
        var responseContent = ""

        val query = URIBuilder(uri)
        query.addParameters(parameters)

        val client = HttpClients.createDefault()
        val request = HttpGet(query.build())
        val response = client.execute(request)

        try {
            println(response.statusLine)
            val entity = response.entity
            responseContent = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
        } finally {
        }
        return responseContent
    }

}
}
