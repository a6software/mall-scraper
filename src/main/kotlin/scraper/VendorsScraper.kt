package scraper

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import repository.Vendors
import repository.dbConnection
import util.stringNumberToInt
import java.text.SimpleDateFormat
import java.util.*

suspend fun vendorsScraper() {
    val response: HttpResponse = HttpClient(CIO).use {client ->
        client.get("https://outlandmalls.com/vendors/")
    }

    val html = response.bodyAsText()

    // Parse the HTML document using Jsoup
    val document = Jsoup.parse(html)

    // Find the HTML table element using its CSS selector
    val table = document.select("table tbody").first()

    // Create a list to hold the extracted table rows
    val rows = mutableListOf<VendorsTableRowScrapeResult>()

    // Define the date format to match the timestamp format
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())

    // Iterate over the table rows
    table?.select("tr")?.forEach { row ->
        // Extract the content of each cell within the row
        val cells = row.select("td")

        val vendorLink = cells[1].select("a")

        // Extract the data and create a TableRow object
        val rowData = VendorsTableRowScrapeResult(
//            id = cells[0].text(),
            vendorName = cells[1].select("a").text(),
            vendorId = vendorLink.attr("href").substringAfterLast("/").toInt(),
            timestamp = dateFormat.parse(cells[2].attr("title")),
            link = cells[3].select("a").attr("href"),
            location = cells[3].select("a").text(),
            totalValue = cells[4].text().stringNumberToInt(),
            totalItems = cells[5].text().stringNumberToInt()
        )

        // Add the TableRow object to the list
        rows.add(rowData)
    }

    dbConnection(withDrop = true)

    transaction {
        // Print the extracted table rows
        rows.forEach { row ->
            Vendors.insert {
                it[vendorName] = row.vendorName
                it[vendorId] = row.vendorId
                it[timestamp] = row.timestamp.time
                it[link] = row.link
                it[location] = row.location
                it[totalValue] = row.totalValue
                it[totalItems] = row.totalItems
            }
            println(row)
        }
    }
}
