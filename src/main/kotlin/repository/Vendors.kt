package repository

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant
import java.util.Date

object Vendors : IntIdTable() {
    val vendorName:  Column<String> = varchar("vendor_name", 255)
    val vendorId:  Column<Int> = integer("vendor_id")
    val timestamp :  Column<Long> = long("timestamp")
    val link:  Column<String> = varchar("link", 255)
    val location:  Column<String> = varchar("location", 255)
    val totalValue:  Column<Int> = integer("total_value")
    val totalItems:  Column<Int> = integer("total_items")
    val createdAt: Column<Long> = long("created_at").default(Date.from(Instant.now()).time)
}
