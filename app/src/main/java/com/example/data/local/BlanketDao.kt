package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BlanketDao {
    @Query("SELECT * FROM horses ORDER BY name ASC")
    fun getAllHorses(): Flow<List<HorseEntity>>

    @Query("SELECT * FROM horses WHERE id = :id LIMIT 1")
    suspend fun getHorseById(id: Long): HorseEntity?

    @Query("SELECT COUNT(*) FROM horses")
    suspend fun getHorseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHorse(horse: HorseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHorses(horses: List<HorseEntity>)

    @Update
    suspend fun updateHorse(horse: HorseEntity)

    @Delete
    suspend fun deleteHorse(horse: HorseEntity)

    @Query("SELECT * FROM blanket_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<BlanketLogEntity>>

    @Query("SELECT * FROM blanket_logs WHERE horseId = :horseId ORDER BY timestamp DESC")
    fun getLogsForHorse(horseId: Long): Flow<List<BlanketLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BlanketLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<BlanketLogEntity>)

    @Delete
    suspend fun deleteLog(log: BlanketLogEntity)

    @Query("DELETE FROM blanket_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("SELECT * FROM custom_blankets ORDER BY maxTempF DESC")
    fun getAllCustomBlankets(): Flow<List<CustomBlanketEntity>>

    @Query("SELECT COUNT(*) FROM custom_blankets")
    suspend fun getCustomBlanketCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomBlanket(blanket: CustomBlanketEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomBlankets(blankets: List<CustomBlanketEntity>)

    @Update
    suspend fun updateCustomBlanket(blanket: CustomBlanketEntity)

    @Delete
    suspend fun deleteCustomBlanket(blanket: CustomBlanketEntity)

    @Query("DELETE FROM custom_blankets")
    suspend fun deleteAllCustomBlankets()
}
