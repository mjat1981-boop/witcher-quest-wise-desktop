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
                ),
                Monster(
                    name = "Grave Hag",
                    category = "NECROPHAGE",
                    description = "A hunched corpse-eater that haunts village cemeteries and battlefields, vomiting bile when cornered.",
                    weaknesses = listOf("Necrophage Oil", "Igni (Sign)", "Grapeshot Bomb"),
                    combatGuide = "Stay out of the bile pool. Burn her with Igni, then close with fast silver strikes before she can spit again."
                ),
                Monster(
                    name = "Cockatrice",
                    category = "DRACONID",
                    description = "A winged reptile with a rooster's crest and a venomous beak. It swoops from cliffs and spits acid.",
                    weaknesses = listOf("Golden Oriole Potion", "Draconid Oil", "Aard (Sign)", "Crossbow"),
                    combatGuide = "Drink Golden Oriole so the spit does not poison you. Knock it out of the air with Aard or a bolt, then strike the grounded body."
                ),
                Monster(
                    name = "Water Hag",
                    category = "NECROPHAGE",
                    description = "A bloated swamp hag that drags prey under the water and heals while standing in her pool.",
                    weaknesses = listOf("Necrophage Oil", "Igni (Sign)", "Grapeshot Bomb"),
                    combatGuide = "Lure her out of the water. Igni stops her regeneration. Do not let her drag you into the pool."
                ),
                Monster(
                    name = "Noonshade Forktail",
                    category = "DRACONID",
                    description = "A cliff-nesting forktail whose tail spines and diving attacks make open ground lethal.",
                    weaknesses = listOf("Golden Oriole Potion", "Draconid Oil", "Aard (Sign)", "Crossbow"),
                    combatGuide = "Golden Oriole for the venom. Pull it down with a crossbow bolt, roll past the tail, and cut the wing joints."
                ),
                Monster(
                    name = "Higher Vampire",
                    category = "VAMPIRE",
                    description = "An intelligent vampire who can pass for a man, turn to mist, and heal from wounds that would kill a lesser breed.",
                    weaknesses = listOf("Black Blood Potion", "Vampire Oil", "Yrden (Sign)", "Moon Dust Bomb"),
                    combatGuide = "Drink Black Blood before the conversation turns. Moon Dust stops the mist form. Yrden holds them still for silver strikes."
                ),
                Monster(
                    name = "Toad Prince",
                    category = "CURSED",
                    description = "A cursed prince swollen into a giant toad in the Oxenfurt sewers. His tongue and poison spit fill the chamber.",
                    weaknesses = listOf("Cursed Oil", "Golden Oriole Potion", "Igni (Sign)"),
                    combatGuide = "Golden Oriole against the spit. Burn the tongue when it lashes, then hit the open mouth. Do not stand in the poison pools."
                ),
                Monster(
                    name = "Bruxa",
                    category = "VAMPIRE",
                    description = "A screaming vampire who blinds her prey and closes the distance in a blur of claws.",
                    weaknesses = listOf("Black Blood Potion", "Vampire Oil", "Moon Dust Bomb", "Yrden (Sign)"),
                    combatGuide = "Black Blood punishes her bite. Moon Dust keeps her from vanishing. Yrden slows the rush so a silver riposte can land."
                ),
                Monster(
                    name = "Archespore",
                    category = "CURSED",
                    description = "A walking blossom that roots itself and spits acid pods. Cut one down and the bulb may bloom again.",
                    weaknesses = listOf("Cursed Oil", "Igni (Sign)", "Northern Wind Bomb"),
                    combatGuide = "Burn the pods before they burst. Igni on the blossom, then destroy the bulb so it cannot regrow."
                ),
                Monster(
                    name = "Shaelmaar",
                    category = "RELICT",
                    description = "A cave-dwelling beast plated in stone. It curls into a ball and rolls until something stops it.",
                    weaknesses = listOf("Relict Oil", "Aard (Sign)", "Samum Bomb"),
                    combatGuide = "Let it roll into a wall, or blast it with Aard so it flips. The soft belly is only open for a moment. Strike then."
                ),
                Monster(
                    name = "Succubus",
                    category = "HYBRID",
                    description = "A winged seductress who would rather talk than fight. Steel and silver both find her if the talk fails.",
                    weaknesses = listOf("Hybrid Oil", "Black Blood Potion", "Dimeritium Bomb"),
                    combatGuide = "A dimeritium bomb breaks her glamour. If you must fight, Hybrid Oil and short dodges beat chasing her across the chapel."
                )
            )
        }
    }
}
