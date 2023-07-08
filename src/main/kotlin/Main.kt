import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.selectAllBatched
import org.jetbrains.exposed.sql.transactions.transaction
import repository.Vendors
import repository.dbConnection
import scraper.vendorsScraper
import scraper.vendorScraper


suspend fun main() {
    vendorsScraper()
//    vendorScraper()

    dbConnection(withDrop = false)

    val urls = mutableListOf<String>()

    transaction {

        Vendors
            .selectAll()
            .forEach{
                urls.add( "https://outlandmalls.com/vendors/${it[Vendors.vendorId]}" )
            }



    }
     println(urls)


    val batchSize = 10
    val concurrentRequests = 10

    val batches = urls.chunked(batchSize) // Divide URLs into batches

    batches.chunked(concurrentRequests).forEach { batchGroup ->
        batchGroup.map { batch ->
            println("batch: $batch")
                batch.map { url -> vendorScraper(url) }
        }

    }
}
