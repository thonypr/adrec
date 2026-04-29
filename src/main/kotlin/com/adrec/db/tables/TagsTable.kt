package com.adrec.db.tables

import org.jetbrains.exposed.sql.Table

object TagsTable : Table("tags") {
    val id = integer("id").autoIncrement()
    val name = text("name")

    override val primaryKey = PrimaryKey(id)
}
