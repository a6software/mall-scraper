package repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DbSettings {
  val db by lazy {
    Database.connect(
        "jdbc:postgresql://localhost:5502/app_db",
        driver = "org.postgresql.Driver",
        user = "app_user",
        password = "app_password")
  }
}

fun dbConnection(withDrop: Boolean = true) {
  DbSettings.db

  transaction {
    addLogger(StdOutSqlLogger)

    if (withDrop) {
      Vendors.dropStatement().forEach { statement -> exec(statement) }

      Vendor.dropStatement().forEach { statement -> exec(statement) }
    }

    SchemaUtils.create(Vendors, Vendor)

    commit()
  }
}
