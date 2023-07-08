package repository

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant
import java.util.*

object Vendor : IntIdTable() {
    val vendorId: Column<Int> = integer("vendor_id")
    val itemName: Column<String> = varchar("item_name", 255)
    val totalPrice: Column<Int> = integer("total_price")
    val unitPrice: Column<Int> = integer("unit_price")
    val quantity: Column<Int> = integer("quantity")
    val createdAt: Column<Long> = long("created_at").default(Date.from(Instant.now()).time)
}
