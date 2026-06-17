package com.example.data

data class IngredientRequirement(
    val name: String,
    val quantity: Int,
    val icon: String
)

data class AlchemyRecipe(
    val id: String,
    val name: String,
    val category: String, // "POTION", "OIL"
    val icon: String,
    val description: String,
    val combatEffect: String,
    val formula: List<IngredientRequirement>
) {
    companion object {
        fun getStaticRecipes(): List<AlchemyRecipe> {
            return listOf(
                AlchemyRecipe(
                    id = "swallow",
                    name = "Swallow Potion",
                    category = "POTION",
                    icon = "🧪",
                    description = "Accelerates vitality regeneration. Standard healing elixir for all witchers.",
                    combatEffect = "Regenerates 15 Vitality per second for 20 seconds.",
                    formula = listOf(
                        IngredientRequirement("Celandine Petals", 2, "🌿"),
                        IngredientRequirement("Alcohest Spirit", 1, "🍺"),
                        IngredientRequirement("Drowner Brain", 1, "🧠")
                    )
                ),
                AlchemyRecipe(
                    id = "thunderbolt",
                    name = "Thunderbolt Potion",
                    category = "POTION",
                    icon = "⚡",
                    description = "Increases physical strength, boosting raw attack impact for a short duration.",
                    combatEffect = "Increases Attack Power by +35% for 30 seconds.",
                    formula = listOf(
                        IngredientRequirement("Alcohest Spirit", 1, "🍺"),
                        IngredientRequirement("Celandine Petals", 2, "🌿"),
                        IngredientRequirement("Fiend Mutagen", 1, "🩸")
                    )
                ),
                AlchemyRecipe(
                    id = "necrophage_oil",
                    name = "Necrophage Oil",
                    category = "OIL",
                    icon = "🛢️",
                    description = "Lethal grease applied to Silver Swords to exploit monster flesh.",
                    combatEffect = "Deals +25% damage against Drowners, Ghouls, and Grave Hags.",
                    formula = listOf(
                        IngredientRequirement("Celandine Petals", 3, "🌿")
                    )
                ),
                AlchemyRecipe(
                    id = "specter_oil",
                    name = "Specter Oil",
                    category = "OIL",
                    icon = "👻",
                    description = "A specialty grease applied to silver blades to disrupt spectral form bonds.",
                    combatEffect = "Deals +25% damage against Noonwraiths and Nightwraiths.",
                    formula = listOf(
                        IngredientRequirement("Alcohest Spirit", 1, "🍺"),
                        IngredientRequirement("Celandine Petals", 2, "🌿")
                    )
                ),
                AlchemyRecipe(
                    id = "golden_oriole",
                    name = "Golden Oriole",
                    category = "POTION",
                    icon = "🟡",
                    description = "Immunizes the user against all toxic poisons and neutralizes existing toxins.",
                    combatEffect = "Instantly cures poison and heals the user when exposed to poison clouds for 60 seconds.",
                    formula = listOf(
                        IngredientRequirement("Alcohest Spirit", 1, "🍺"),
                        IngredientRequirement("Celandine Petals", 2, "🌿")
                    )
                ),
                AlchemyRecipe(
                    id = "blizzard",
                    name = "Blizzard Potion",
                    category = "POTION",
                    icon = "❄️",
                    description = "Sharpens reflexes, making local time slow down after killing an enemy.",
                    combatEffect = "Slows local time by 40% for 5 seconds upon slaying a foe. Greatly increases dodge chance.",
                    formula = listOf(
                        IngredientRequirement("Alcohest Spirit", 1, "🍺"),
                        IngredientRequirement("Drowner Brain", 2, "🧠")
                    )
                ),
                AlchemyRecipe(
                    id = "hanged_mans_venom",
                    name = "Hanged Man's Venom",
                    category = "OIL",
                    icon = "🗡️",
                    description = "Lethal paste specialized for steel blades against non-monster combatants.",
                    combatEffect = "Deals +25% bonus attack damage to humans and non-humans (bandits, soldiers).",
                    formula = listOf(
                        IngredientRequirement("Alcohest Spirit", 1, "🍺"),
                        IngredientRequirement("Celandine Petals", 1, "🌿")
                    )
                )
            )
        }
    }
}
