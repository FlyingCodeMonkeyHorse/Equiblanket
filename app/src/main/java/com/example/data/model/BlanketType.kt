package com.example.data.model

enum class BlanketType(
    val displayName: String,
    val shortName: String,
    val grams: Int,
    val description: String
) {
    NO_BLANKET(
        displayName = "No Blanket Needed",
        shortName = "Naked / None",
        grams = 0,
        description = "Weather is mild. The horse's natural coat and thermo-regulation are sufficient."
    ),
    RAIN_SHEET(
        displayName = "Waterproof Rain Sheet (0g)",
        shortName = "Rain Sheet",
        grams = 0,
        description = "Waterproof and windproof shell with no polyfill. Shields from chilling rain and wind without causing overheating."
    ),
    LIGHT_TURNOUT(
        displayName = "Light Turnout Rug (100g - 150g)",
        shortName = "Light Turnout",
        grams = 100,
        description = "Light insulation for brisk, chilly nights or clipped horses in cool weather."
    ),
    MEDIUM_TURNOUT(
        displayName = "Medium Turnout Rug (200g - 250g)",
        shortName = "Medium Turnout",
        grams = 200,
        description = "Moderate warmth for cold nights near or below freezing, or clipped horses in cold weather."
    ),
    HEAVY_TURNOUT(
        displayName = "Heavy Turnout Rug (300g - 350g)",
        shortName = "Heavy Turnout",
        grams = 300,
        description = "Maximum thermal fill for deep freezes, high wind chill, or exposed senior/clipped horses."
    ),
    HEAVY_WITH_HOOD(
        displayName = "Heavy Turnout + Detachable Hood (360g - 400g)",
        shortName = "Heavy + Neck Hood",
        grams = 400,
        description = "Full coverage protection including insulated neck cover for severe sub-freezing blizzards and extreme wind."
    )
}

enum class CoatType(val label: String, val tempAdjustmentF: Int, val description: String) {
    CLIPPED("Body Clipped", 15, "Hair shaved; loses natural insulation (+15°F warmer blanket needed)"),
    MODERATE_CLIP("Partial / Trace Clip", 8, "Neck/chest clipped (+8°F warmer blanket needed)"),
    UNCLIPPED("Natural Winter Coat", 0, "Full thick natural winter coat with air-trapping fluff")
}

enum class ShelterType(val label: String, val tempAdjustmentF: Int, val description: String) {
    STALLED("Stalled Indoors", -5, "Protected inside stable overnight; 5°F warmer than outside"),
    RUN_IN("Pasture with Run-in Shed", 0, "Has access to 3-sided shelter from wind and precipitation"),
    OPEN_PASTURE("Open Pasture (No Shelter)", 5, "Exposed to wind, rain, and cold; requires 5°F extra warmth")
}

enum class KeeperType(val label: String, val tempAdjustmentF: Int) {
    HARD_KEEPER("Hard Keeper / Senior / Underweight", 5),
    NORMAL("Normal Body Condition", 0),
    EASY_KEEPER("Easy Keeper / Heavy Draft Breed", -3)
}
