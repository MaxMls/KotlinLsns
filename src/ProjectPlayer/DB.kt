package ProjectPlayer

import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement


class DB {

    private lateinit var stat: Statement
    private lateinit var con: Connection
    private var path: String

    init {


    }

    constructor(path: String = "data.db") {
        this.path = Paths.get(path).toUri().toString()


    }

    fun getSongs(): List<Song> {
        con = DriverManager.getConnection("jdbc:sqlite:${path}")
        stat = con.createStatement()
        //stat.executeUpdate("DROP TABLE IF EXISTS MusicLib")
        stat.executeUpdate(
            """ 
            CREATE TABLE IF NOT EXISTS MusicLib (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title       TEXT,
                    file        TEXT UNIQUE,
                    duration    INTEGER,
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
                if (t as Int? == 0) return "Неизвестно"
            } catch (e: Exception) {
            }

            return t?.toString() ?: "Неизвестно"
        }

        val l = mutableListOf<Song>()
        while (res.next()) {
            l.add(
                Song(
                    n(res.getInt("id")),
                    n(res.getString("title")),
                    { x: Int -> if (x == 0) n(x) else "${x / 60}:${x % 60}" }(res.getInt("duration")),
                    n(res.getString("genre")),
                    n(res.getString("artist")),
                    n(res.getString("album")),
                    n(res.getInt("releaseYear")),
                    res.getString("file")
                )
            )
        }

        return l
    }

    fun addSongs(s: Collection<Song>) {
        val ins = con.prepareStatement(
            "INSERT INTO MusicLib(title, file, duration, genre, artist, album, releaseYear) values(?,?,?,?,?,?,?)"
        )
        ins.setString(1, "TOPHAMHAT-KYO Princess♂.mp3")
        ins.setString(2, "D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3")
        ins.setInt(3, 0)
        ins.setString(4, null)
        ins.setString(5, "Tophamhatkyo")
        ins.setString(6, "Tophamhatkyo")
        ins.setInt(7, 2019)

        ins.executeUpdate()
        try {
            ins.executeUpdate()
        } catch (e: Exception) {
            println(e)
        }
    }

}
