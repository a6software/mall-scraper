import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun BatchingExample() = runBlocking<Unit> {
    val urls = generateUrls() // Replace this with your list of 1000 URLs

    val client = HttpClient(CIO)

    val batchSize = 2
    val concurrentRequests = 2

    val batches = urls.chunked(batchSize) // Divide URLs into batches

    batches.chunked(concurrentRequests).forEach { batchGroup ->
        val jobs = batchGroup.map { batch ->
            println("batch: $batch")
            async(Dispatchers.IO) {
                batch.map { url ->
                    val result = makeHttpGetRequest(client, url)
                    // Process the result as needed
                    println("url: ${result.responseTime}")

                }
            }
        }

        jobs.awaitAll()
    }
}

suspend fun makeHttpGetRequest(client: HttpClient, url: String): HttpResponse {
    println(url)
    return client.get(url)
}

fun generateUrls(): List<String> {
    // Replace this with your logic to generate the list of URLs
    // Example:
    return (1..20).map { "https://example.com/$it.html" }
}
