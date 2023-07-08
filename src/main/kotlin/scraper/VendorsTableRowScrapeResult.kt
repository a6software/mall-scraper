package scraper

import java.util.*

data class VendorsTableRowScrapeResult(
    val vendorName: String,
    val vendorId: Int,
    val timestamp: Date,
    val link: String,
    val location: String,
    val totalValue: Int,
    val totalItems: Int
)
