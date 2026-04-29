package com.adrec.db.tables

import org.jetbrains.exposed.sql.Table

object AdTagsTable : Table("ad_tags") {
    val adId = integer("ad_id").references(AdsTable.id)
    val tagId = integer("tag_id").references(TagsTable.id)

    override val primaryKey = PrimaryKey(adId, tagId)
}
