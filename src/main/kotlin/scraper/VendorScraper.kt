package scraper

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import repository.Vendor
import repository.dbConnection
import util.stringNumberToInt

suspend fun vendorScraper(url: String) {
    val client = HttpClient(CIO)
//    val response: HttpResponse = client.get("https://outlandmalls.com/vendors/1716419")
    val response: HttpResponse = client.get(url)
    client.close()

    val html = response.bodyAsText()

    // Parse the HTML document using Jsoup
    val document = Jsoup.parse(html)

    val itemRows = document.select("table tr:has(td.item-name)")

    val items = itemRows.map { row ->
        val columns = row.select("td")

        val itemName = columns[1].select("a").text()
        val totalPrice = columns[2].text().stringNumberToInt()
        val quantity = columns[3].text().toInt()
        val unitPrice = columns[4].text().stringNumberToInt()

        VendorTableRowScrapeResult(itemName, totalPrice, quantity, unitPrice)
    }

    dbConnection(withDrop = false)

    transaction {
        // Print the extracted table rows
        items.forEach { item ->
            Vendor.insert {
                it[vendorId] = url.replace("https://outlandmalls.com/vendors/", "").toInt()
                it[itemName] = item.itemName
                it[totalPrice] = item.totalPrice
                it[unitPrice] = item.unitPrice
                it[quantity] = item.quantity
            }
//            println(item)

            commit()
        }
    }

//    println(items)
}
