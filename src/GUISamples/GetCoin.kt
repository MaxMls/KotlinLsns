import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException
import java.util.*


object MyCoinTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val a = GetCoin()
        // a.state(GetCoin.EIntervalType.DAY, "bitcoin")
        a.coinNames()
    }
}

class Crypto(val id: String, val name: String, val symbol: String){
    override fun toString(): String {
        return id
    }
}


class GetCoin {

    private val baseURL = "http://api.coincap.io/v2/assets"

    public enum class EIntervalType(val value: String, val desc: String) {
        MINUTE_1("m1", "1 минута"),
        MINUTE_5("m5", "5 минут"),
        MINUTE_15("m15", "15 минут"),
        MINUTE_30("m30", "30 минут"),
        HOUR_1("h1", "1 час"),
        HOUR_2("h2", "2 часа"),
        HOUR_6("h6", "6 часов"),
        HOUR_12("h12", "12 часов"),
        DAY("d1", "1 день")
    }

    fun state(interval: EIntervalType, crypto: Crypto) {
        val paratmers = ArrayList<NameValuePair>()
        paratmers.add(BasicNameValuePair("interval", interval.value))

        try {
            val result = makeAPICall("$baseURL/$crypto/history", paratmers)
            println(result)
        } catch (e: IOException) {
            println("Error: cannot access content - $e")
        } catch (e: URISyntaxException) {
            println("Error: Invalid URL $e")
        }
    }


    fun coinNames(): List<Crypto> {
        val list = mutableListOf<Crypto>()
        try {
            val json = makeAPICall(baseURL, ArrayList<NameValuePair>())
            println(json)
            val data = JSONObject(json).getJSONArray("data");
            for (i in 0 until data.length()) {
                val info: JSONObject = data.getJSONObject(i)
                list.add(Crypto(info.getString("id"), info.getString("name"), info.getString("symbol")))
            }

        } catch (e: IOException) {
            println("Error: cannot access content - $e")
        } catch (e: URISyntaxException) {
            println("Error: Invalid URL $e")
        }
        println(list)
        return list
    }


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