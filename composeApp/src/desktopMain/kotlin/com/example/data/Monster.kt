package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.utils.Converters

@Entity(tableName = "monsters")
data class Monster(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "SPECTER", "HYBRID", "CURSED", "RELICT", "VAMPIRE", "NECROPHAGE", "DRACONID"
    val description: String,
    val weaknesses: List<String>,
    val combatGuide: String
) {
    companion object {
        fun getStaticBestiary(): List<Monster> {
            return listOf(
                Monster(
                    name = "Noonwraith",
                    category = "SPECTER",
                    description = "Noonwraiths are born of tragedy, representing a young woman who died violently right before her wedding. They haunt crops and wells under intense daylight.",
                    weaknesses = listOf("Yrden (Sign)", "Specter Oil", "Moon Dust Bomb"),
                    combatGuide = "A Noonwraith is completely ethereal and takes almost no damage unless trapped within a Yrden sign or dusted with Moon Dust. Trap them first, then apply silver sword quick attacks."
                ),
                Monster(
                    name = "Nightwraith",
                    category = "SPECTER",
                    description = "Similar to noonwraiths, nightwraiths are created by tragic deaths. They haunt dark fields and forests lit only by moonlight, draining the souls of travelers.",
                    weaknesses = listOf("Yrden (Sign)", "Specter Oil", "Moon Dust Bomb"),
                    combatGuide = "Nightwraiths can split into mirror clones that drain Geralt's health. Cleanse these mirror images immediately with simple sword fast attacks while using Yrden to slow down the prime spectre."
                ),
                Monster(
                    name = "Royal Griffin",
                    category = "HYBRID",
                    description = "Fierce hybrids combining the body of a lion and the wings/head of an eagle. Griffins are highly territorial and will viciously attack anything entering their airspace.",
                    weaknesses = listOf("Aard (Sign)", "Hybrid Oil", "Grapeshot Bomb", "Crossbow"),
                    combatGuide = "Use the Crossbow or Aard to force a flying Griffin to crash into the ground. Once grounded, utilize side-dodges to get behind its wings and land devastating heavy attacks."
                ),
                Monster(
                    name = "Leshen",
                    category = "RELICT",
                    description = "An ancient forest spirit that commands wood creatures, summons crows, and manipulates roots to crush trespassers in primeval woodlands.",
                    weaknesses = listOf("Igni (Sign)", "Relict Oil", "Dimeritium Bomb"),
                    combatGuide = "Vulnerable to fire. Use Igni to ignite its wooden frame, stopping its root-summoning animation. Clear any summoned wolves using wide, sweeping silver sword strikes."
                ),
                Monster(
                    name = "Werewolf",
                    category = "CURSED",
                    description = "A tragic shapeshifter cursed with uncontrollable bloodlust. They heal their wounds at an astronomical rate, out-healing standard sword cuts.",
                    weaknesses = listOf("Igni (Sign)", "Cursed Oil", "Moon Dust Bomb", "Devil's Puffball"),
                    combatGuide = "They have passive regeneration that activates when low. Toss a Moon Dust bomb or apply Devil's Puffball poison to permanently halt their healing while launching rapid frontal attacks."
                ),
                Monster(
                    name = "Fiend",
                    category = "RELICT",
                    description = "A massive, muscular beast carrying massive elk horns and a third hypnotic eye that plunges opponents into blinding pitch darkness.",
                    weaknesses = listOf("Samum Bomb", "Devil's Puffball", "Relict Oil"),
                    combatGuide = "When the Fiend lifts its head to hypnotize, throw a Samum bomb or ignite with Igni to disrupt its focus. Dodge sideways from its charges to slash its vulnerable flank."
                ),
                Monster(
                    name = "Katakan",
                    category = "VAMPIRE",
                    description = "A large, terrifying cave-dwelling vampire that turns invisible and heals rapidly from the blood of its victims.",
                    weaknesses = listOf("Vampire Oil", "Moon Dust Bomb", "Black Blood Potion", "Yrden (Sign)"),
                    combatGuide = "Drink Black Blood potion prior to the fight. When the Katakan bites, the acidic blood will stun it. Toss Moon Dust to reveal its invisible form, and strike with silver attacks."
                ),
                Monster(
                    name = "Drowner",
                    category = "NECROPHAGE",
                    description = "Predatory aquatic scavengers that ambush lone travelers along rivers, swamps, and shorelines, attacking in deadly, nimble groups.",
                    weaknesses = listOf("Necrophage Oil", "Igni (Sign)", "Grapeshot Bomb"),
                    combatGuide = "Drowners strike swiftly in packs. Keep your distance, use Igni to burn and panic multiple drowners simultaneously, and focus down the burning ones one by one."
                ),
                Monster(
                    name = "Ghoul",
                    category = "NECROPHAGE",
                    description = "Unthinking scavengers of war-torn battlefields that feed on cold flesh. They become ferocious and gain hyper-armor when they feast.",
                    weaknesses = listOf("Necrophage Oil", "Igni (Sign)"),
                    combatGuide = "Prevent ghouls from feasting on cadavers or each other by hitting them with fast sword strikes. Knock them down with Aard to execute instantly if isolated."
                ),
                Monster(
                    name = "Wyvern",
                    category = "DRACONID",
                    description = "A winged reptile that nests on high cliffs and drops upon prey from above, injecting lethal toxins through its spiked tail.",
                    weaknesses = listOf("Golden Oriole Potion", "Draconid Oil", "Aard (Sign)", "Crossbow"),
                    combatGuide = "Drink Golden Oriole to nullify its poison. When it swoops, fire a crossbow bolt to drag it out of the skies. Flank its tail to strike its chest safely."
                ),
                Monster(
                    name = "Foglet",
                    category = "SPECTER",
                    description = "Aggressive swamp-dwelling predators that manipulate thick fog and create shimmering illusions to disorient and ambush travelers.",
                    weaknesses = listOf("Moon Dust Bomb", "Necrophage Oil", "Quen (Sign)"),
                    combatGuide = "A Foglet cloaks itself in fog to summon mirror clones. Use Moon Dust or Aard to disperse the mist and force it to materialize, then land heavy silver sword attacks."
                ),
                Monster(
                    name = "Chort",
                    category = "RELICT",
                    description = "A compact but incredibly muscular relation to the Fiend, characterized by fierce charge attacks and a resilient hide.",
                    weaknesses = listOf("Chort Decoction", "Relict Oil", "Samum Bomb"),
                    combatGuide = "Sidestep their rapid forward-charging strikes. Once they collide with an obstacle or miss, they are momentarily dazed, allowing you to deal heavy damage to their flank."
                ),
                Monster(
                    name = "Basilisk",
                    category = "DRACONID",
                    description = "A reptilian beast combining wings and a sharp bird beak. Its acidic spit and high-altitude swoops make it a lethal flying terror.",
                    weaknesses = listOf("Golden Oriole Potion", "Draconid Oil", "Aard (Sign)", "Crossbow"),
                    combatGuide = "Always keep Golden Oriole active to nullify toxic lung spray. Use Aard or a crossbow bolt to knock them out of flight, then execute rapid silver slashes."
                ),
                Monster(
                    name = "Nekker",
                    category = "NECROPHAGE",
                    description = "Vicious subterranean pack hunters that overwhelm targets with sheer numbers, communicating through chilling cackles.",
                    weaknesses = listOf("Northern Wind Bomb", "Necrophage Oil", "Igni (Sign)"),
                    combatGuide = "Nekkers always attack in large numbers and can surround you in seconds. Cast Igni or throw a Northern Wind bomb to freeze or panic them, then destroy them individually."
                )
            )
        }
    }
}
