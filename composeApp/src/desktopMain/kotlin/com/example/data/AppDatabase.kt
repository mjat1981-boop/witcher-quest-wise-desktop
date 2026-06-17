package com.example.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.data.utils.Converters
import kotlinx.coroutines.Dispatchers
import java.io.File

@Database(entities = [Quest::class, SaddlebagItem::class, Monster::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun saddlebagItemDao(): SaddlebagItemDao
    abstract fun monsterDao(): MonsterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbFile = File(System.getProperty("user.home"), ".witcher-quest-wise/witcher_quest_wise_db")
                dbFile.parentFile?.mkdirs()
                val instance = Room.databaseBuilder<AppDatabase>(dbFile.absolutePath)
                    .setDriver(BundledSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getPreloadedQuests(): List<Quest> {
            return listOf(
                // WHITE ORCHARD
                Quest(
                    title = "Lilac and Gooseberries",
                    type = "MAIN",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 1,
                    description = "Geralt, guided by a letter scented with lilac and gooseberries, tracks down Yennefer of Vengerberg through war-torn Temeria.",
                    questgiver = "Geralt (Journal)",
                    rewards = "150 XP, 50 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Missing in Action",
                    type = "SIDE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 1,
                    description = "Geralt helps Duny find his brother Bastien, who went missing during a battle near White Orchard.",
                    questgiver = "Duny (White Orchard Board)",
                    rewards = "25 XP, Herb stash",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Twisted Firestarter",
                    type = "SIDE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 1,
                    description = "Geralt tracks down an arsonist who burned the dwarf blacksmith Willis' forge in White Orchard.",
                    questgiver = "Willis (Dwarven Blacksmith)",
                    rewards = "25 XP, 20 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Precious Cargo",
                    type = "SIDE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 1,
                    description = "A merchant asks Geralt to recover a box of precious medicine from a swamp, but things aren't as they seem.",
                    questgiver = "Merchant near Garrison",
                    rewards = "50 XP, 50 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Devil by the Well",
                    type = "CONTRACT",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 2,
                    description = "A noonwraith is haunting the well in an abandoned village. Find out who she was and put her tortured spirit to rest.",
                    questgiver = "Odolan (White Orchard Board)",
                    rewards = "240 XP, 20 Crowns, Noonwraith Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Yrden, Specter Oil, Moon Dust"
                ),
                Quest(
                    title = "The Beast of White Orchard",
                    type = "MAIN",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 3,
                    description = "To get information from the Nilfgaardian commander, Geralt must hunt down a royal griffin plaguing the area.",
                    questgiver = "Captain Peter Saar Gwynleve",
                    rewards = "300 XP, 150 Crowns, Griffin Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Aard, Hybrid Oil, Grapeshot Bomb"
                ),
                Quest(
                    title = "Imperial Audience",
                    type = "MAIN",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 2,
                    description = "Geralt is summoned to Castle Vizima to meet Emperor Emhyr, discuss his search for Ciri, and secure diplomatic clearance.",
                    questgiver = "Emperor Emhyr var Emreis",
                    rewards = "150 XP, Noble Attire",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "On Death's Bed",
                    type = "SIDE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 2,
                    description = "Geralt brews a Swallow potion for Lena, a dying peasant girl in White Orchard, reflecting on the harsh side effects of witcher mutagens.",
                    questgiver = "Tomira (Herbalist)",
                    rewards = "350 XP, 50 Crowns, Venom Extract",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Scavenger Hunt: Viper Gear",
                    type = "TREASURE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 2,
                    description = "Uncover the hidden diagrams for the Viper School's quick steel and silver swords inside a castle ruin and a ghoul-infested crypt.",
                    questgiver = "Kolgrim's Remains",
                    rewards = "Viper Steel & Silver Sword Diagrams",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Places of Power: Exploration",
                    type = "TREASURE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 1,
                    description = "Locate and draw magical energy from all six ancient stones known as Places of Power across White Orchard to earn early Ability Points.",
                    questgiver = "Geralt (Journal)",
                    rewards = "6 Ability Points, Sign intensity buffs",
                    status = "NOT_STARTED"
                ),

                // VELEN
                Quest(
                    title = "The Nilfgaardian Connection",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 5,
                    description = "Reach Crow’s Perch and check lead on Ciri by searching for Hendrik, a spy in the Nilfgaardian service.",
                    questgiver = "Geralt (Journal)",
                    rewards = "100 XP, Witcher Lead clues",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "The Bloody Baron",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Geralt reaches Crow's Perch to find the self-proclaimed baron, Phillip Strenger, who holds crucial information regarding Ciri.",
                    questgiver = "Nilfgaardian Intel",
                    rewards = "100 XP, Crow's Perch Safe Conduct",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Family Matters",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Search Crow's Perch for clues about the Baron's missing wife (Anna) and daughter (Tamara). Uncover deep domestic tragedies and a botchling.",
                    questgiver = "The Bloody Baron",
                    rewards = "350 XP, 100 Crowns, Strenger Letter",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Ladies of the Wood",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Follow the Trail of Treats into Crookback Bog to investigate the mysterious Crones and find out how they are linked to Ciri's disappearance.",
                    questgiver = "Keira Metz / Journal",
                    rewards = "250 XP, Crone's Ear dagger",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "The Whispering Hillock",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Geralt must deal with an ancient malevolent spirit of a whispering tree near Downwarren to settle his contract with the Crones, presenting a dark moral conundrum.",
                    questgiver = "The Crones of Crookback Bog",
                    rewards = "100 XP, 50 Crowns",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Relict Oil, Igni"
                ),
                Quest(
                    title = "Return to Crookback Bog",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 9,
                    description = "Join the Bloody Baron and his soldiers as they head to Crookback Bog to rescue his cursed wife, Anna, from the Crones.",
                    questgiver = "The Bloody Baron",
                    rewards = "300 XP, Anna's Fate / Family resolution",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "An Invitation from Keira Metz",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 5,
                    description = "Keira invites Geralt to her hut to discuss a personal matter, initiating her questline.",
                    questgiver = "Keira Metz",
                    rewards = "50 XP, Cave map",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "A Towerful of Mice",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Keira Metz asks Geralt to lift a tragic, mouse-ridden curse of love and betrayal on Fyke Isle's abandoned tower.",
                    questgiver = "Keira Metz",
                    rewards = "200 XP, Magic Lamp",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Yrden, Specter Oil, Magic Lamp"
                ),
                Quest(
                    title = "Return to Keira",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Deliver the notes from Fyke Isle to Keira, and see if you can change her mind about visiting King Radovid.",
                    questgiver = "Keira Metz",
                    rewards = "100 XP, Keira's Friendship / Fate choice",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Wandering in the Dark",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Search an ancient, underground elven ruin with Keira Metz to chase a lead on a mysterious elven mage.",
                    questgiver = "Keira Metz",
                    rewards = "350 XP, Potion recipes",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Magic Lamp",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Help Keira Metz search the elven ruins for a magical lamp that can project echoes of the deceased.",
                    questgiver = "Keira Metz",
                    rewards = "100 XP, Magic Lamp item",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "For the Advancement of Learning",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 8,
                    description = "Confront Keira Metz on Fyke Isle about her secret notes and persuade her to seek refuge at Kaer Morhen.",
                    questgiver = "Keira Metz",
                    rewards = "100 XP, Keira Metz as Kaer Morhen ally",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Contract: The Merry Widow",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 7,
                    description = "Slay Mourntart, a deadly grave hag graveyard scavenger terrorizing the villagers of Lindenvale.",
                    questgiver = "Gravedigger of Lindenvale",
                    rewards = "240 XP, 180 Crowns, Grave Hag Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Necrophage Oil, Black Blood, Yrden"
                ),
                Quest(
                    title = "Contract: Shrieker",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 8,
                    description = "Track and defeat the Shrieker, a territorial cockatrice nesting near Crow's Perch.",
                    questgiver = "Symon (Crow's Perch Board)",
                    rewards = "250 XP, 120 Crowns, Cockatrice Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Draconid Oil, Aard, Grapeshot"
                ),
                Quest(
                    title = "Jenny o' the Woods",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 10,
                    description = "A violent nightwraith known as Jenny o' the Woods is attacking villagers near Midcopse. Find her beloved's letter and purge her.",
                    questgiver = "Ealdorman of Midcopse",
                    rewards = "240 XP, 185 Crowns, Nightwraith Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Yrden, Specter Oil, Moon Dust"
                ),
                Quest(
                    title = "Swamp Thing",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 12,
                    description = "Investigate a thick, toxic fog in the Velen swampland to slay the ancient foglet, Ignis Fatuus.",
                    questgiver = "Leslav (Downwarren Board)",
                    rewards = "240 XP, 136 Crowns, Foglet Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Necrophage Oil, Moon Dust, Quen"
                ),
                Quest(
                    title = "Phantom of the Trade Route",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 23,
                    description = "Investigate missing merchant carts in Benek; slay the royal wyvern nesting on the nearby cliffs.",
                    questgiver = "Refugee (Benek)",
                    rewards = "300 XP, 276 Crowns, Wyvern Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Draconid Oil, Aard, Grapeshot"
                ),
                Quest(
                    title = "Scavenger Hunt: Griffin Gear",
                    type = "TREASURE",
                    region = "VELEN",
                    recommendedLevel = 11,
                    description = "Find the legendary diagrams of Grandmaster George's Griffin School Witcher gear scattered around Velen's ruins.",
                    questgiver = "Edwin Metras' Notes",
                    rewards = "Griffin Steel & Silver Sword, Griffin Armor diagrams",
                    status = "NOT_STARTED"
                ),

                // NOVIGRAD
                Quest(
                    title = "Pyres of Novigrad",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 10,
                    description = "Geralt arrives in Novigrad to find Triss Merigold, but deep religious cleansings by the Witch Hunters force him into the criminal underworld.",
                    questgiver = "Geralt (Journal)",
                    rewards = "150 XP, Corrupt Guard contacts",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Novigrad Dreaming",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 7,
                    description = "Seek out Corinne Tilly, a legendary oneiromancer, to help interpret Geralt's dreams of Ciri.",
                    questgiver = "Geralt (Journal)",
                    rewards = "100 XP, Dream Interpretation clue",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Get Junior",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "Investigate the disappearance of Dandelion by searching the casino, arena, and secret hideout of criminal underworld boss Whoreson Junior.",
                    questgiver = "Sigismund Dijkstra / Cleaver",
                    rewards = "150 XP, Underworld contacts",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Count Reuven's Treasure",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "Solve the mysterious heist of Dijkstra's underground bathhouse vault to locate leads on Dandelion and the stolen fortune.",
                    questgiver = "Sigismund Dijkstra",
                    rewards = "150 XP, Dijkstra spy contacts",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "A Poet Under Pressure",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 13,
                    description = "Execute a daring ambush with Zoltan and Priscilla to rescue Dandelion from temple guards transporting him near Novigrad.",
                    questgiver = "Zoltan Chivay",
                    rewards = "200 XP, Dandelion's rescue",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Carnal Sins",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 16,
                    description = "Geralt tracks down a brutal, ritualistic serial killer haunting Novigrad's dark alleys after Priscilla is severely attacked.",
                    questgiver = "Dandelion / Doctor Shani",
                    rewards = "300 XP, Deargdeith Steel Sword",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "An Eye for an Eye",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "Vernon Roche needs Geralt's help rescuing Ves, who has defied orders to launch a desperate raid against Nilfgaardian executioners.",
                    questgiver = "Vernon Roche",
                    rewards = "150 XP, Roche's loyalty",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "A Matter of Life and Death",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "Triss asks Geralt to help her rescue a young mage, Albert Vegelbud, by escorting him securely from a masquerade ball at the Vegelbud Mansion.",
                    questgiver = "Triss Merigold",
                    rewards = "150 XP, Vegelbud Commemorative Masks, Fox Mask",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Now or Never",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 14,
                    description = "Help Triss Merigold relocate the persecuted mages of Novigrad through the sewers to a safe ship.",
                    questgiver = "Triss Merigold",
                    rewards = "200 XP, Triss romance confirmation option",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Cabaret",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 14,
                    description = "Dandelion has inherited a brothel called Rosemary and Thyme and wants to turn it into a high-end cabaret. Help him secure loan and props.",
                    questgiver = "Dandelion",
                    rewards = "200 XP, Crimson Avenger nameplate",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Scavenger Hunt: Feline Gear",
                    type = "TREASURE",
                    region = "NOVIGRAD",
                    recommendedLevel = 17,
                    description = "Hunt down the sleek, attack-multiplier diagrams of the Cat School Gear hidden within Novigrad's temple ruins and caverns.",
                    questgiver = "Adalbert Kermith's Map",
                    rewards = "Feline Armor & Weapon diagrams",
                    status = "NOT_STARTED"
                ),

                // SKELLIGE
                Quest(
                    title = "Destination: Skellige",
                    type = "MAIN",
                    region = "SKELLIGE",
                    recommendedLevel = 16,
                    description = "Hire a captain courageous enough to sail Geralt to Skellige. Surviving shipwreck and sea raiders on the way is mandatory.",
                    questgiver = "Geralt (Journal)",
                    rewards = "250 XP, Pirate Gold",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "The King is Dead - Long Live the King",
                    type = "MAIN",
                    region = "SKELLIGE",
                    recommendedLevel = 16,
                    description = "Geralt meets Yennefer in Kaer Trolde to attend King Bran's wake, leading to a secretive heist of Ermion's laboratory mask.",
                    questgiver = "Yennefer of Vengerberg",
                    rewards = "200 XP, Ouroboros Mask",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "The Last Wish",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 15,
                    description = "Yennefer wants to capture a Djinn to lift the magical wish that bound her destiny to Geralt, testing if their love is indeed genuine.",
                    questgiver = "Yennefer",
                    rewards = "150 XP, Emotional clarity / Romance outcome",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Possession",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 17,
                    description = "Help Cerys an Craite solve the possession of Udalryk, the Jarl of Spikeroog, by a dark shadow demon (Hym).",
                    questgiver = "Crach an Craite",
                    rewards = "150 XP, Allies support",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Specter Oil, Igni"
                ),
                Quest(
                    title = "The Lord of Undvik",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 17,
                    description = "Help Hjalmar an Craite slay the Ice Giant of Undvik and rescue his remaining crew.",
                    questgiver = "Crach an Craite",
                    rewards = "150 XP, Undvik allies support",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Ogroid Oil, Quen"
                ),
                Quest(
                    title = "King's Gambit",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 18,
                    description = "Help Crach choose the next ruler of Skellige during a bloody feast at Kaer Trolde disrupted by mysterious berserker bears.",
                    questgiver = "Crach an Craite",
                    rewards = "300 XP, Coronated Sovereign ally",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "The Phantom of Eldberg",
                    type = "CONTRACT",
                    region = "SKELLIGE",
                    recommendedLevel = 17,
                    description = "A massive lighthouse on Eldberg island is enveloped in thick fog haunted by specters. Find the lighthouse keeper and solve the light's source.",
                    questgiver = "Jorund of Arinbjorn",
                    rewards = "260 XP, 200 Crowns, Penitent Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Yrden, Specter Oil, Moon Dust"
                ),
                Quest(
                    title = "Scavenger Hunt: Ursine Gear",
                    type = "TREASURE",
                    region = "SKELLIGE",
                    recommendedLevel = 20,
                    description = "Retrieve the heavy defense Bear School (Ursine) Diagrams from ruins of Clan Tuirseach castle and coastal caves.",
                    questgiver = "Ibrahim Savi's Map",
                    rewards = "Ursine Heavy Armor diagrams",
                    status = "NOT_STARTED"
                ),

                // KAER MORHEN
                Quest(
                    title = "Ugly Baby",
                    type = "MAIN",
                    region = "KAER_MORHEN",
                    recommendedLevel = 19,
                    description = "Geralt takes the hideous creature Uma to the mountain stronghold Kaer Morhen to run a dangerous Trial of Grasses decrypters on it.",
                    questgiver = "Nilfgaardian Emperor / Journal",
                    rewards = "400 XP, Ciri's whereabouts",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Brothers in Arms",
                    type = "MAIN",
                    region = "KAER_MORHEN",
                    recommendedLevel = 22,
                    description = "Travel across Temeria, Skellige, and Redania to recruit powerful allies to defend Kaer Morhen against the impending Wild Hunt invasion.",
                    questgiver = "Geralt (Journal)",
                    rewards = "Xp varies, Recruited Allies",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "The Isle of Mists",
                    type = "MAIN",
                    region = "KAER_MORHEN",
                    recommendedLevel = 22,
                    description = "Geralt sets sail to a magical, hidden island to find Ciri, navigating perilous waters guarded by foglets.",
                    questgiver = "Geralt (Journal)",
                    rewards = "1000 XP, Reuniting with Ciri",
                    status = "NOT_STARTED",
                    geraltAdvice = "This is a point of no return for many regional conflicts. Make sure all local side contracts are completely wrapped up before stepping foot on the island.",
                    advisorAdvice = "Keep your eyes on the magic firefly. It's the only thing guiding your boat through the deadly coastal shoals safely."
                ),
                Quest(
                    title = "The Battle of Kaer Morhen",
                    type = "MAIN",
                    region = "KAER_MORHEN",
                    recommendedLevel = 24,
                    description = "Stand with your brothers-in-arms at Kaer Morhen to defend Ciri from the Wild Hunt's onslaught. Prepare defenses, set traps, and mix potions.",
                    questgiver = "Geralt (Journal)",
                    rewards = "1000 XP, Legendary Wolf steel sword",
                    status = "NOT_STARTED"
                ),

                // TOUSSAINT (Blood & Wine Expansion)
                Quest(
                    title = "The Beast of Toussaint",
                    type = "MAIN",
                    region = "TOUSSAINT",
                    recommendedLevel = 35,
                    description = "Geralt travels to the pristine duchy of Toussaint to investigate a series of bizarre, ritualistic murders targeting knights.",
                    questgiver = "Duchess Anna Henrietta",
                    rewards = "500 XP, 1000 Crowns, Beauclair Medallion",
                    status = "NOT_STARTED",
                    geraltAdvice = "This isn't a mindless monster. The strikes are calculated. Check the arena corpse for specialized wounds; we're dealing with a higher vampire.",
                    advisorAdvice = "Follow the trail to the greenhouse gardens. The local nobility is hiding secrets behind their knightly virtues."
                ),
                Quest(
                    title = "La Cage au Fou",
                    type = "MAIN",
                    region = "TOUSSAINT",
                    recommendedLevel = 39,
                    description = "Regis guides Geralt to a haunted, spoon-filled mansion infested with a ravenous Spotted Wight to obtain its rare saliva and lift a tragic hunger curse.",
                    questgiver = "Emiel Regis / Regis",
                    rewards = "400 XP, Spotted Wight Spoon Trophy, Gold Chest",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Yrden, Necrophage Oil"
                ),
                Quest(
                    title = "The Night of Long Fangs",
                    type = "MAIN",
                    region = "TOUSSAINT",
                    recommendedLevel = 42,
                    description = "The Beast of Beauclair sets loose a horde of vampires upon the city of Beauclair. Geralt must make a critical choice to locate Syanna or seek out the Unseen Elder vampire.",
                    questgiver = "Emiel Regis / Regis",
                    rewards = "500 XP, Toussaint Steel Sword",
                    status = "NOT_STARTED",
                    geraltAdvice = "Vampires are tearing the city apart. Use Black Blood potion and apply Vampire Oil on your silver sword before joining Regis in the city streets.",
                    advisorAdvice = "Syanna is the key to resolving this tragedy. Think carefully before taking her to the Land of a Thousand Fables."
                ),
                Quest(
                    title = "Scavenger Hunt: Grandmaster Manticore Gear",
                    type = "TREASURE",
                    region = "TOUSSAINT",
                    recommendedLevel = 40,
                    description = "Investigate ancient prison ruins and temple notes in Toussaint to recover grandmaster diagrams of the legendary high-toxicity Manticore School gear.",
                    questgiver = "Grandmaster Lazare Lafargue",
                    rewards = "Manticore Steel & Silver Sword, Manticore Chest/Boots/Glove diagrams",
                    status = "NOT_STARTED"
                ),

                // HEARTS OF STONE EXPANSION (Novigrad/Velen Regions)
                Quest(
                    title = "Evil's Soft First Touches",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 32,
                    description = "Geralt takes a contract from Olgierd von Everec to slay a giant monster toad lurking in the Oxenfurt sewers.",
                    questgiver = "Olgierd von Everec",
                    rewards = "300 XP, 400 Crowns, Gaunter's Mark of Tribute",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Golden Oriole, Yrden, Cursed Oil",
                    geraltAdvice = "The sewers are flooded with toxic venom. Drink a Golden Oriole potion before dropping down, or the fumes will take you out before the beast does.",
                    advisorAdvice = "Confront Olgierd afterward. He knows more about the toad's true identity than he lets on."
                ),
                Quest(
                    title = "Scenes From a Marriage",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 35,
                    description = "Rebuild and explore Iris's painted nightmare world inside the ruined, specter-ridden von Everec estate to find and retrieve her black violet rose.",
                    questgiver = "Olgierd von Everec",
                    rewards = "250 XP, Iris's Rose, Violet Saber blade",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Specter Oil, Blizzard Potion"
                ),
                // NEW CLIMACTIC ENDGAME QUESTS
                Quest(
                    title = "Blood on the Battlefield",
                    type = "MAIN",
                    region = "KAER_MORHEN",
                    recommendedLevel = 20,
                    description = "Support Ciri in her grief following the grueling Battle of Kaer Morhen, and make choices that will shape the fate of the Northern Realms and her imperial legacy.",
                    questgiver = "Ciri",
                    rewards = "1000 XP",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Bald Mountain",
                    type = "MAIN",
                    region = "VELEN",
                    recommendedLevel = 26,
                    description = "Ascend the misty, blood-soaked Bald Mountain to eliminate General Imlerith of the Wild Hunt and put an end to the ancient cruelty of the Crones of Crookback Bog.",
                    questgiver = "Geralt (Path)",
                    rewards = "1050 XP, Imlerith's Acorn",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Quen, Relict Oil"
                ),
                Quest(
                    title = "On Thin Ice",
                    type = "MAIN",
                    region = "SKELLIGE",
                    recommendedLevel = 30,
                    description = "Unleash the Sunstone to summon the Naglfar. Engage Eredin Breacc Glas in the final steel and silver duel on the frozen shores of Undvik.",
                    questgiver = "Avallac'h",
                    rewards = "1500 XP, Legendary Silver Sword Hav'caaren",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Igni, Specter Oil"
                ),
                Quest(
                    title = "Tedd Deireadh, The Final Age",
                    type = "MAIN",
                    region = "SKELLIGE",
                    recommendedLevel = 30,
                    description = "Accompany Ciri to Tor Gvalch'ca as she steps into the vortex to confront the ancient threat of the White Frost once and for all.",
                    questgiver = "Ciri",
                    rewards = "2000 XP, Game Ending Finale",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Dead Man's Party",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 33,
                    description = "Agree to show Olgierd's brother, the late Vlodimir von Everec, a night to remember. Allow his cheerful ghost to possess Geralt's body and crash a wedding.",
                    questgiver = "Olgierd von Everec",
                    rewards = "400 XP, Alchemist's boots",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "In Wolf's Clothing",
                    type = "CONTRACT",
                    region = "SKELLIGE",
                    recommendedLevel = 15,
                    description = "Lifting the curse of the immortal werewolf Morkvarg, who haunts the sacred Garden of Freya. Discover if he can be fed his own cursed flesh.",
                    questgiver = "Josta (Freya's Prophet)",
                    rewards = "300 XP, Werewolf Mutagen, 200 Crowns",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Cursed Oil, Igni"
                ),
                Quest(
                    title = "No Place Like Home",
                    type = "MAIN",
                    region = "KAER_MORHEN",
                    recommendedLevel = 19,
                    description = "Take the evening to catch up with Lambert and Eskel at Kaer Morhen. Under the guidance of alcohol, share stories of the Path and play with Yennefer's megascope.",
                    questgiver = "Yennefer / Lambert",
                    rewards = "500 XP",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "There Can Be Only One",
                    type = "SIDE",
                    region = "TOUSSAINT",
                    recommendedLevel = 36,
                    description = "Prove your adherence to the five knightly virtues (Valor, Honor, Compassion, Generosity, Wisdom) across Toussaint to claim the mythical silver sword Aerondight directly from the Lady of the Lake.",
                    questgiver = "Hermit of Lac Céleste",
                    rewards = "500 XP, Aerondight (Legendary Silver Sword)",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Water Hag Oil, Quen"
                ),
                Quest(
                    title = "A Faithful Friend",
                    type = "SIDE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 2,
                    description = "Geralt tracks down an escaped horse belonging to a Nilfgaardian deserter's friend Elahal in the outskirts of White Orchard.",
                    questgiver = "Elahal (Peasant)",
                    rewards = "50 XP, Horse Feed pouch",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Dirty Funds",
                    type = "TREASURE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 2,
                    description = "Locate and raid a bandit camp in northern White Orchard that possesses a stash of stolen Temerian military funds.",
                    questgiver = "Scrawled Note",
                    rewards = "50 XP, Temerian Steel Ingot, 120 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Temerian Valuables",
                    type = "TREASURE",
                    region = "WHITE_ORCHARD",
                    recommendedLevel = 4,
                    description = "Recover a chest filled with Temerian coins and jewelry, sunk underneath the river bridge near the military campsite.",
                    questgiver = "Blood-soaked Military Order",
                    rewards = "20 XP, Temerian Gold chest items",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Wild at Heart",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 7,
                    description = "Investigate the mysterious disappearance of Niellen's wife, Hanna, which leads into the deep wolf-ridden woods of Blackbough and a cursed werewolf tragedy.",
                    questgiver = "Niellen (Blackbough Board)",
                    rewards = "150 XP, Werewolf Mutagen, 150 Crowns",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Cursed Oil, Igni, Devil's Puffball"
                ),
                Quest(
                    title = "The Fall of the House of Reardon",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Dolores Reardon requests a Witcher to clear her ancestral mansion tomb of Wraiths, skeletons, and hidden traps so she can return home.",
                    questgiver = "Dolores Reardon (Lindenvale)",
                    rewards = "75 XP, 100 Crowns, Dolores Cellar Key",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Specter Oil, Yrden"
                ),
                Quest(
                    title = "Ghosts of the Past",
                    type = "SIDE",
                    region = "VELEN",
                    recommendedLevel = 6,
                    description = "Accidentally encounter Letho of Gulet, the Kingslayer, who is hiding from bounty hunters inside the booby-trapped barn of the Reardon Manor.",
                    questgiver = "Letho of Gulet",
                    rewards = "150 XP, Letho's aid in Kaer Morhen",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Contract: The Mystery of the Byways Murders",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 22,
                    description = "Find out what slaughtered a whole contingent of Nilfgaardian soldiers and citizens inside the remote village of Byways. Fight the ancient Ekimmara, Sarasti.",
                    questgiver = "Milan Noran (Oreferry Board)",
                    rewards = "300 XP, 250 Crowns, Ekimmara Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Vampire Oil, Devil's Puffball, Black Blood"
                ),
                Quest(
                    title = "Contract: Patrol Gone Missing",
                    type = "CONTRACT",
                    region = "VELEN",
                    recommendedLevel = 7,
                    description = "Investigate the disappearance of a Nilfgaardian southern patrol troop, who entered the deep swamps and were consumed by a deadly Wyvern.",
                    questgiver = "Quartermaster Eggebracht",
                    rewards = "200 XP, 150 Crowns, Wyvern Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Draconid Oil, Aard, Grapeshot"
                ),
                Quest(
                    title = "The Play's the Thing",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 11,
                    description = "Stage a theatrical play titled 'The Savior’s Queen!' with Irina's troupe in Novigrad to safely lure out the Doppler Dudu, who holds crucial info on Jaskier.",
                    questgiver = "Irina Renarde",
                    rewards = "150 XP, 100 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "High Stakes",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 26,
                    description = "Pay the hefty entry fee of 1,000 Crowns to enter the elite, ultra-competitive high-stakes Gwent tournament hosted at the Passiflora and solve a heist.",
                    questgiver = "Sasha / Tournament Board",
                    rewards = "Leader cards, 4500 Crowns, Sasha's companionship",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "A Dangerous Game",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "Zoltan Chivay needs Geralt's aid in tracing down and recovering three unbelievably rare Gwent cards (John Natalis, Fringilla, Isengrim) from collector snobs.",
                    questgiver = "Zoltan Chivay",
                    rewards = "150 XP, Choice of three legendary cards or Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Redania's Most Wanted",
                    type = "SIDE",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "King Radovid commissions Geralt to search the hidden elven underground laboratory of the fugitive sorceress Philippa Eilhart.",
                    questgiver = "King Radovid V",
                    rewards = "200 XP, Sorceress megascope crystal clues",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Contract: The Oxenfurt Drunk",
                    type = "CONTRACT",
                    region = "NOVIGRAD",
                    recommendedLevel = 26,
                    description = "Drink heavily to draw out a notorious Katakan (Gael) that lurks around Oxenfurt taverns, preying solely on inebriated citizens.",
                    questgiver = "Nikolas Friedman (Oxenfurt Guard)",
                    rewards = "300 XP, 200 Crowns, Katakan Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Vampire Oil, Moon Dust, Black Blood"
                ),
                Quest(
                    title = "Contract: An Elusive Thief",
                    type = "CONTRACT",
                    region = "NOVIGRAD",
                    recommendedLevel = 13,
                    description = "Track down a trickster Doppler who is stealing food and wares in Novigrad market by shape-shifting into various local townspeople.",
                    questgiver = "Sylvester Amello (Marketplace)",
                    rewards = "250 XP, 180 Crowns, Doppler Trophy / spares",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Relict Oil, Dimeritium Bomb"
                ),
                Quest(
                    title = "Coast of Wrecks",
                    type = "TREASURE",
                    region = "NOVIGRAD",
                    recommendedLevel = 12,
                    description = "Search the vast collection of sunken ship skeletons off the southern coast of Novigrad for chests containing hidden smuggler cargoes.",
                    questgiver = "Unsent Letter on beach",
                    rewards = "100 XP, Rare materials, 250 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Nameless",
                    type = "MAIN",
                    region = "SKELLIGE",
                    recommendedLevel = 14,
                    description = "Chasing Ciri's footsteps with Yennefer, navigate the sacred, desecrated Garden of Freya to locate Skjall, the coward who helped her escape.",
                    questgiver = "Yennefer / Priestesses",
                    rewards = "200 XP, Clues on Arthur's boat",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Cave of Dreams",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 14,
                    description = "Meet Blueboy Lugos near a mystical, cavernous cove where inhaling psychotropic herbs forces Geralt to duel manifestations of his worst fears.",
                    questgiver = "Blueboy Lugos (Morgur)",
                    rewards = "150 XP, Lugos Family respect",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Specter Oil, Igni"
                ),
                Quest(
                    title = "Path of Warriors",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 16,
                    description = "Prove your superhuman athletic power to the elders of Clan An Craite by undertaking a dual trial of scaling high peaks and swimming underwater cave systems.",
                    questgiver = "Gunnar (Urialla's Harbor)",
                    rewards = "200 XP, Mountaineer Boots, 50 Crowns",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Coronation",
                    type = "SIDE",
                    region = "SKELLIGE",
                    recommendedLevel = 18,
                    description = "Secure your allies' future by attending the grand Coronation feast of the next sovereign ruler of Skellige (Cerys or Hjalmar) on Gedyneith's holy oak.",
                    questgiver = "Crach an Craite",
                    rewards = "300 XP, Clan An Craite Support",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Contract: Dragon",
                    type = "CONTRACT",
                    region = "SKELLIGE",
                    recommendedLevel = 28,
                    description = "Determine what is hunting cattle in Fyresdal. Prove to the skeptical villagers that the mythical beast is not a true dragon, but an aggressive Royal Forktail.",
                    questgiver = "Vagn (Fyresdal Elder)",
                    rewards = "300 XP, 250 Crowns, Forktail Trophy",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Draconid Oil, Aard, Golden Oriole"
                ),
                Quest(
                    title = "Scavenger Hunt: Cat School Gear",
                    type = "TREASURE",
                    region = "NOVIGRAD",
                    recommendedLevel = 17,
                    description = "Retrieve the diagrams for the Cat School's light armor and swords hidden under Temple Isle.",
                    questgiver = "Mage's Journal",
                    rewards = "Feline Steel & Silver Sword, Feline Armor diagrams",
                    status = "NOT_STARTED"
                ),
                Quest(
                    title = "Whatsoever a Man Soweth...",
                    type = "MAIN",
                    region = "NOVIGRAD",
                    recommendedLevel = 35,
                    description = "Confront Gaunter O'Dimm at the Temple of Lilvani to settle Olgierd von Everec's contract. Solve the Mirror Master's final riddle in his twisted shadow realm before time runs out.",
                    questgiver = "Olgierd von Everec",
                    rewards = "Olgierd's Silver Sword (Iris), 1000 XP",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Specter Oil, Quen"
                ),
                Quest(
                    title = "The Beast of Beauclair",
                    type = "MAIN",
                    region = "TOUSSAINT",
                    recommendedLevel = 34,
                    description = "Embark on a grand investigation with Duchess Anna Henrietta to track down the mysterious Beast of Beauclair, responsible for elite knight executions.",
                    questgiver = "Duchess Anna Henrietta",
                    rewards = "500 XP, 500 Crowns",
                    status = "NOT_STARTED",
                    monsterWeaknesses = "Vampire Oil, Black Blood, Quen"
                )
            )
        }
    }
}
