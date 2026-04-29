package com.adrec.db

import com.adrec.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init() {
        val dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = AppConfig.dbUrl
            username = AppConfig.dbUser
            password = AppConfig.dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()

        Database.connect(dataSource)
    }
}
