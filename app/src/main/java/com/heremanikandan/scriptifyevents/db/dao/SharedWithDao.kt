package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.heremanikandan.scriptifyevents.db.model.SharedWith

@Dao
interface SharedWithDao {

    // Insert a new shared entry
    @Insert
    suspend fun insertSharedWith(sharedWith: SharedWith)

    // Get total count of users permitted to a specific event
    @Query("SELECT COUNT(*) FROM shared_with WHERE eventId = :eventId")
    suspend fun getTotalPermittedUsers(eventId: Long): Int

    // Get count of users with status = true (permitted)
    @Query("SELECT COUNT(*) FROM shared_with WHERE eventId = :eventId AND status = 1")
    suspend fun getPermittedUsersCount(eventId: Long): Int

    // Get count of users with status = false (not permitted)
    @Query("SELECT COUNT(*) FROM shared_with WHERE eventId = :eventId AND status = 0")
    suspend fun getNotPermittedUsersCount(eventId: Long): Int

    // Check if a specific user is permitted to an event
    @Query("SELECT COUNT(*) FROM shared_with WHERE eventId = :eventId AND permittedTo = :userId AND status = 1")
    suspend fun isUserPermitted(eventId: Long, userId: Long): Int
}
