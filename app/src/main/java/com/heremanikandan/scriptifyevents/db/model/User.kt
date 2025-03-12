package com.heremanikandan.scriptifyevents.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uid: String,
    val name: String,
    val email:String,
   // val photoUri: Uri?,
    val disabled:Boolean,

    )
