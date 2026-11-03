package com.example.data.model

data class Horse(
    val id: Long = 0,
    val name: String,
    val breed: String = "Warmblood",
    val coatType: CoatType = CoatType.UNCLIPPED,
    val shelterType: ShelterType = ShelterType.STALLED,
    val keeperType: KeeperType = KeeperType.NORMAL,
    val isSenior: Boolean = false,
    val customTempOffsetF: Int = 0,
    val notes: String = ""
) {
    val totalTempOffset: Int
        get() = coatType.tempAdjustmentF +
                shelterType.tempAdjustmentF +
                keeperType.tempAdjustmentF +
                (if (isSenior) 5 else 0) +
                customTempOffsetF
}
