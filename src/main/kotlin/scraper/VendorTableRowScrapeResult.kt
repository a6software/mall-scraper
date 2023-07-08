package scraper

data class VendorTableRowScrapeResult(
    val itemName: String,
    val totalPrice: Int,
    val quantity: Int,
    val unitPrice: Int
)
