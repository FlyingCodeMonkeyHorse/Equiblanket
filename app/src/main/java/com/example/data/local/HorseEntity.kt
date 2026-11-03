package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CoatType
import com.example.data.model.Horse
import com.example.data.model.KeeperType
import com.example.data.model.ShelterType

@Entity(tableName = "horses")
data class HorseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val breed: String = "Warmblood",
    val coatType: String = "UNCLIPPED",
    val shelterType: String = "STALLED",
    val keeperType: String = "NORMAL",
    val isSenior: Boolean = false,
    val customTempOffsetF: Int = 0,
    val notes: String = ""
) {
    fun toDomain(): Horse {
        return Horse(
            id = id,
            name = name,
            breed = breed,
            coatType = try { CoatType.valueOf(coatType) } catch (e: Exception) { CoatType.UNCLIPPED },
            shelterType = try { ShelterType.valueOf(shelterType) } catch (e: Exception) { ShelterType.STALLED },
            keeperType = try { KeeperType.valueOf(keeperType) } catch (e: Exception) { KeeperType.NORMAL },
            isSenior = isSenior,
            customTempOffsetF = customTempOffsetF,
            notes = notes
        )
    }

    companion object {
        fun fromDomain(horse: Horse): HorseEntity {
            return HorseEntity(
                id = horse.id,
                name = horse.name,
                breed = horse.breed,
                coatType = horse.coatType.name,
                shelterType = horse.shelterType.name,
                keeperType = horse.keeperType.name,
                isSenior = horse.isSenior,
                customTempOffsetF = horse.customTempOffsetF,
                notes = horse.notes
            )
        }
    }
}
