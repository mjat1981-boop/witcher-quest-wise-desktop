package com.example.data

data class IngredientRequirement(
    val name: String,
    val quantity: Int,
    val icon: String,
    val foundAt: String
)

private data class AlchemyIngredient(
    val name: String,
    val icon: String,
    val foundAt: String
)

private val ingredientCatalog: Map<String, AlchemyIngredient> = listOf(
    AlchemyIngredient("Celandine Petals", "🌿", "Meadows in White Orchard and Velen"),
    AlchemyIngredient("Alcohest Spirit", "🍺", "Sold by innkeepers and herbalists, not gathered as a plant"),
    AlchemyIngredient("Dwarven Spirit", "🍺", "Sold by innkeepers and herbalists, not gathered as a plant"),
    AlchemyIngredient("Drowner Brain", "🧠", "Drowners along rivers in White Orchard and Velen"),
    AlchemyIngredient("Fiend Mutagen", "🩸", "Fiends in the wilds of Velen"),
    AlchemyIngredient("Cortinarius", "🍄", "Dark woods in Velen"),
    AlchemyIngredient("Arenaria", "🌾", "Sandy coasts in Skellige"),
    AlchemyIngredient("Beggartick Blossoms", "🌼", "Roadsides in Velen and Novigrad"),
    AlchemyIngredient("Ergot Seeds", "🌾", "Grain fields near villages"),
    AlchemyIngredient("Pringrape", "🍇", "Skellige hillsides"),
    AlchemyIngredient("Honeysuckle", "🌸", "Novigrad outskirts and Toussaint hedges"),
    AlchemyIngredient("Fool's Parsley", "🌿", "Damp ground around Oxenfurt"),
    AlchemyIngredient("Wolfsbane", "💜", "Kaer Morhen and Skellige high ground"),
    AlchemyIngredient("Ghoul's Blood", "🩸", "Ghouls on Velen battlefields"),
    AlchemyIngredient("Blowball", "🌼", "Sunny fields in White Orchard"),
).associateBy { it.name }

private fun ingredient(name: String, quantity: Int): IngredientRequirement {
    val known = ingredientCatalog.getValue(name)
    return IngredientRequirement(known.name, quantity, known.icon, known.foundAt)
}

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
                        ingredient("Celandine Petals", 2),
                        ingredient("Drowner Brain", 1),
                        ingredient("Alcohest Spirit", 1)
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
                        ingredient("Cortinarius", 1),
                        ingredient("Fiend Mutagen", 1),
                        ingredient("Dwarven Spirit", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "cat",
                    name = "Cat Potion",
                    category = "POTION",
                    icon = "🐈",
                    description = "Opens the pupils so a witcher can see in pitch darkness.",
                    combatEffect = "Grants night vision for 60 seconds. Bright light becomes painful.",
                    formula = listOf(
                        ingredient("Fool's Parsley", 2),
                        ingredient("Blowball", 1),
                        ingredient("Dwarven Spirit", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "tawny_owl",
                    name = "Tawny Owl",
                    category = "POTION",
                    icon = "🦉",
                    description = "Keeps stamina from draining while Signs are cast.",
                    combatEffect = "Stamina regenerates in combat for 30 seconds.",
                    formula = listOf(
                        ingredient("Cortinarius", 1),
                        ingredient("Arenaria", 1),
                        ingredient("Dwarven Spirit", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "white_raffard",
                    name = "White Raffard's Decoction",
                    category = "POTION",
                    icon = "🤍",
                    description = "A violent surge of healing, brewed when Swallow is not enough.",
                    combatEffect = "Instantly restores a large share of Vitality.",
                    formula = listOf(
                        ingredient("Honeysuckle", 2),
                        ingredient("Beggartick Blossoms", 1),
                        ingredient("Dwarven Spirit", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "black_blood",
                    name = "Black Blood",
                    category = "POTION",
                    icon = "🖤",
                    description = "Turns the witcher's blood into poison for anything that bites.",
                    combatEffect = "Vampires and necrophages that wound Geralt are poisoned for 30 seconds.",
                    formula = listOf(
                        ingredient("Ghoul's Blood", 1),
                        ingredient("Ergot Seeds", 2),
                        ingredient("Dwarven Spirit", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "petris_philter",
                    name = "Petri's Philter",
                    category = "POTION",
                    icon = "✨",
                    description = "A sign-booster favored by witchers who fight with Igni and Quen.",
                    combatEffect = "Increases Sign intensity by 25% for 30 seconds.",
                    formula = listOf(
                        ingredient("Pringrape", 1),
                        ingredient("Cortinarius", 1),
                        ingredient("Dwarven Spirit", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "full_moon",
                    name = "Full Moon",
                    category = "POTION",
                    icon = "🌕",
                    description = "Swells the body's reserves so a blow that should kill only staggers.",
                    combatEffect = "Increases maximum Vitality for 60 seconds.",
                    formula = listOf(
                        ingredient("Wolfsbane", 1),
                        ingredient("Honeysuckle", 1),
                        ingredient("Drowner Brain", 1)
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
                        ingredient("Blowball", 2),
                        ingredient("Beggartick Blossoms", 1),
                        ingredient("Ergot Seeds", 1)
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
                        ingredient("Wolfsbane", 1),
                        ingredient("Pringrape", 1),
                        ingredient("Dwarven Spirit", 1)
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
                        ingredient("Celandine Petals", 3)
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
                        ingredient("Arenaria", 2),
                        ingredient("Fool's Parsley", 1)
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
                        ingredient("Wolfsbane", 1),
                        ingredient("Ghoul's Blood", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "beast_oil",
                    name = "Beast Oil",
                    category = "OIL",
                    icon = "🐺",
                    description = "A simple blade grease for wolves, bears, and other ordinary animals.",
                    combatEffect = "Deals +25% damage against beasts.",
                    formula = listOf(
                        ingredient("Blowball", 2),
                        ingredient("Celandine Petals", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "cursed_oil",
                    name = "Cursed Oil",
                    category = "OIL",
                    icon = "🌙",
                    description = "Silver-blade grease for creatures born of a curse.",
                    combatEffect = "Deals +25% damage against werewolves, archespores, and the Toad Prince.",
                    formula = listOf(
                        ingredient("Wolfsbane", 1),
                        ingredient("Beggartick Blossoms", 2)
                    )
                ),
                AlchemyRecipe(
                    id = "draconid_oil",
                    name = "Draconid Oil",
                    category = "OIL",
                    icon = "🐉",
                    description = "A thick oil for winged reptiles: wyverns, forktails, basilisks, and cockatrices.",
                    combatEffect = "Deals +25% damage against draconids.",
                    formula = listOf(
                        ingredient("Arenaria", 1),
                        ingredient("Pringrape", 2)
                    )
                ),
                AlchemyRecipe(
                    id = "hybrid_oil",
                    name = "Hybrid Oil",
                    category = "OIL",
                    icon = "🦅",
                    description = "Blade grease for monsters that are half one thing and half another.",
                    combatEffect = "Deals +25% damage against griffins and succubi.",
                    formula = listOf(
                        ingredient("Fool's Parsley", 1),
                        ingredient("Cortinarius", 1)
                    )
                ),
                AlchemyRecipe(
                    id = "relict_oil",
                    name = "Relict Oil",
                    category = "OIL",
                    icon = "🪵",
                    description = "An old-forest oil for leshens, fiends, chorts, and shaelmaar.",
                    combatEffect = "Deals +25% damage against relicts.",
                    formula = listOf(
                        ingredient("Honeysuckle", 1),
                        ingredient("Ergot Seeds", 2)
                    )
                ),
                AlchemyRecipe(
                    id = "vampire_oil",
                    name = "Vampire Oil",
                    category = "OIL",
                    icon = "🦇",
                    description = "Silver-blade grease that bites into vampire flesh.",
                    combatEffect = "Deals +25% damage against katakan, bruxae, and higher vampires.",
                    formula = listOf(
                        ingredient("Ghoul's Blood", 1),
                        ingredient("Arenaria", 2)
                    )
                )
            )
        }
    }
}
