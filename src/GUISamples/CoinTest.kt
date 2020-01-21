
//org.apache.httpcomponents:httpclient:jar:4.5.10

import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import java.io.IOException
import java.net.URISyntaxException
import java.util.ArrayList


object CoinTest {
    private val baseURL = "http://api.coincap.io/v2/assets"

    @JvmStatic
    fun main(args: Array<String>) {
        val parameters = ArrayList<NameValuePair>()
        parameters.add(BasicNameValuePair("interval", "d1"))

        try {
            val result = makeAPICall("$baseURL/bitcoin/history", parameters)
            println(result)
        } catch (e: IOException) {
            println("Error: cannont access content - $e")
        } catch (e: URISyntaxException) {
            println("Error: Invalid URL $e")
        }

    }

    @Throws(URISyntaxException::class, IOException::class)
    fun makeAPICall(uri: String, parameters: List<NameValuePair>): String {
        var responseContent: String

        val query = URIBuilder(uri)
        query.addParameters(parameters)

        val client = HttpClients.createDefault()
        val request = HttpGet(query.build())
        val response = client.execute(request)

        try {
            System.out.println(response.getStatusLine())
            val entity = response.getEntity()
            responseContent = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
        } finally {

        }
        return responseContent
    }
}