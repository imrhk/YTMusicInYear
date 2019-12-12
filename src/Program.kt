import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.math.min

//TODO: Change the filepath to your My Activity.json
const val FILE_PATH = "/Users/rahul/Downloads/Takeout/My Activity/YouTube/My Activity.json"
fun main() {
    val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val sdf2: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSXXX")

    sdf.timeZone = Calendar.getInstance().timeZone
    val lines = Files.readAllLines(File(FILE_PATH).toPath()).joinToString(separator = " ")
    val json = JSONArray(lines)

    val frequencyMap = LinkedHashMap<Pair<String, String>, Int>()
    json.filter {
        if(it !is JSONObject) return@filter false
            val date = try {
                sdf.parse(it["time"] as String?)
            } catch (e : Exception) {
                try {
                    sdf2.parse(it["time"] as String?)
                }
                catch (e2 : Exception) {
                    return@filter false
                }
            }

            it.has("titleUrl")
            && it.has("time")
            && date.year == 119
                    && it.has("title")
                    && (it["title"]!! as String).startsWith("Watched")

        }
        .map {
            Pair((it as JSONObject)["titleUrl"] as String, (it["title"]!! as String).removePrefix("Watched "))
        }
        .forEach {
            if(frequencyMap.putIfAbsent(it, 1) != null) {
                frequencyMap[it] = frequencyMap[it]!!.inc()
            }
        }

    frequencyMap.toList().sortedByDescending { (_, value) -> value }.subList(0, min(50, frequencyMap.size)).forEachIndexed { index, pair ->
        println("${index+1}. ${pair.first.second} -> ${pair.second}")
    }

}