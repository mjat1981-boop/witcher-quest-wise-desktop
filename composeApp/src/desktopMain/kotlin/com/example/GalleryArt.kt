package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WitcherAmberGold
import com.example.ui.theme.WitcherBorderColor
import com.example.ui.theme.WitcherDarkSurface
import com.example.ui.theme.WitcherDarkSurfaceVariant
import com.example.ui.theme.WitcherMutedText
import com.example.ui.theme.WitcherRedPrimary
import com.example.ui.theme.WitcherWhiteText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import witcher_quest_wise_desktop.composeapp.generated.resources.Res
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_archespore
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_basilisk
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_bruxa
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_chort
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_cockatrice
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_drowner
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_fiend
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_foglet
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_forktail
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_ghoul
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_grave_hag
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_griffin
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_higher_vampire
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_katakan
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_leshen
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_nekker
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_nightwraith
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_noonwraith
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_shaelmaar
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_succubus
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_toad_prince
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_water_hag
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_werewolf
import witcher_quest_wise_desktop.composeapp.generated.resources.img_beast_wyvern
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_avallach
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_back
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_baron
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_cerys
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_ciri
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_crach
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_dijkstra
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_emhyr
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_eredin
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_eskel
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_gaunter
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_geralt
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_imlerith
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_jaskier
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_lambert
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_letho
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_olgierd
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_philippa
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_regis
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_roach
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_roche
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_triss
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_vesemir
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_yennefer
import witcher_quest_wise_desktop.composeapp.generated.resources.img_gwent_zoltan
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_kaer_morhen
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_novigrad
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_oxenfurt
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_skellige
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_toussaint
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_velen
import witcher_quest_wise_desktop.composeapp.generated.resources.img_alchemy_oil
import witcher_quest_wise_desktop.composeapp.generated.resources.img_alchemy_potion
import witcher_quest_wise_desktop.composeapp.generated.resources.img_loc_white_orchard
import witcher_quest_wise_desktop.composeapp.generated.resources.img_nav_beast
import witcher_quest_wise_desktop.composeapp.generated.resources.img_nav_gear
import witcher_quest_wise_desktop.composeapp.generated.resources.img_nav_journal
import witcher_quest_wise_desktop.composeapp.generated.resources.img_nav_potion

data class GwentCard(
    val name: String,
    val faction: String,
    val detail: String,
    val strength: Int,
    val image: DrawableResource,
)

fun gwentDeck(): List<GwentCard> = listOf(
    GwentCard("Card Back", "All", "Wolf Medallion", 0, Res.drawable.img_gwent_back),
    GwentCard("Geralt of Rivia", "Neutral", "Hero Card (15)", 15, Res.drawable.img_gwent_geralt),
    GwentCard("Ciri of Cintra", "Neutral", "Hero Card (15)", 15, Res.drawable.img_gwent_ciri),
    GwentCard("Dandelion / Jaskier", "Neutral", "Minstrel Card (2)", 2, Res.drawable.img_gwent_jaskier),
    GwentCard("Vesemir", "Neutral", "Hero Card (6)", 6, Res.drawable.img_gwent_vesemir),
    GwentCard("Yennefer", "Neutral", "Hero Card (7)", 7, Res.drawable.img_gwent_yennefer),
    GwentCard("Triss Merigold", "Neutral", "Hero Card (7)", 7, Res.drawable.img_gwent_triss),
    GwentCard("Emiel Regis", "Neutral", "Hero Card (10)", 10, Res.drawable.img_gwent_regis),
    GwentCard("Zoltan Chivay", "Northern Realms", "Scout Card (5)", 5, Res.drawable.img_gwent_zoltan),
    GwentCard("Roach", "Neutral", "Unit Card (3)", 3, Res.drawable.img_gwent_roach),
    GwentCard("Letho of Gulet", "Nilfgaard", "Hero Card (10)", 10, Res.drawable.img_gwent_letho),
    GwentCard("Eredin", "Monsters", "Leader Card (10)", 10, Res.drawable.img_gwent_eredin),
    GwentCard("Emhyr var Emreis", "Nilfgaard", "Leader Card (10)", 10, Res.drawable.img_gwent_emhyr),
    GwentCard("Lambert", "Neutral", "Hero Card (15)", 15, Res.drawable.img_gwent_lambert),
    GwentCard("Eskel", "Neutral", "Hero Card (11)", 11, Res.drawable.img_gwent_eskel),
    GwentCard("Gaunter O'Dimm", "Neutral", "Leader Card (12)", 12, Res.drawable.img_gwent_gaunter),
    GwentCard("Bloody Baron", "Northern Realms", "Unit Card (6)", 6, Res.drawable.img_gwent_baron),
    GwentCard("Philippa Eilhart", "Northern Realms", "Hero Card (10)", 10, Res.drawable.img_gwent_philippa),
    GwentCard("Vernon Roche", "Northern Realms", "Hero Card (10)", 10, Res.drawable.img_gwent_roche),
    GwentCard("Sigismund Dijkstra", "Northern Realms", "Hero Card (4)", 4, Res.drawable.img_gwent_dijkstra),
    GwentCard("Cerys an Craite", "Skellige", "Hero Card (10)", 10, Res.drawable.img_gwent_cerys),
    GwentCard("Crach an Craite", "Skellige", "Leader Card (15)", 15, Res.drawable.img_gwent_crach),
    GwentCard("Olgierd von Everec", "Neutral", "Hero Card (6)", 6, Res.drawable.img_gwent_olgierd),
    GwentCard("Imlerith", "Monsters", "Hero Card (10)", 10, Res.drawable.img_gwent_imlerith),
    GwentCard("Avallac'h", "Scoia'tael", "Hero Card (8)", 8, Res.drawable.img_gwent_avallach),
)

private fun GwentCard.isHeroOrLeader(): Boolean =
    detail.startsWith("Hero") || detail.startsWith("Leader")

fun beastDrawable(name: String): DrawableResource? = when (name) {
    "Leshen" -> Res.drawable.img_beast_leshen
    "Werewolf" -> Res.drawable.img_beast_werewolf
    "Noonwraith" -> Res.drawable.img_beast_noonwraith
    "Nightwraith" -> Res.drawable.img_beast_nightwraith
    "Royal Griffin" -> Res.drawable.img_beast_griffin
    "Fiend" -> Res.drawable.img_beast_fiend
    "Katakan" -> Res.drawable.img_beast_katakan
    "Ghoul" -> Res.drawable.img_beast_ghoul
    "Drowner" -> Res.drawable.img_beast_drowner
    "Nekker" -> Res.drawable.img_beast_nekker
    "Noonshade Forktail" -> Res.drawable.img_beast_forktail
    "Foglet" -> Res.drawable.img_beast_foglet
    "Grave Hag" -> Res.drawable.img_beast_grave_hag
    "Wyvern" -> Res.drawable.img_beast_wyvern
    "Chort" -> Res.drawable.img_beast_chort
    "Basilisk" -> Res.drawable.img_beast_basilisk
    "Cockatrice" -> Res.drawable.img_beast_cockatrice
    "Water Hag" -> Res.drawable.img_beast_water_hag
    "Higher Vampire" -> Res.drawable.img_beast_higher_vampire
    "Toad Prince" -> Res.drawable.img_beast_toad_prince
    "Bruxa" -> Res.drawable.img_beast_bruxa
    "Archespore" -> Res.drawable.img_beast_archespore
    "Shaelmaar" -> Res.drawable.img_beast_shaelmaar
    "Succubus" -> Res.drawable.img_beast_succubus
    else -> null
}

data class RegionPainting(
    val regionId: String,
    val label: String,
    val image: DrawableResource,
)

fun regionPaintings(): List<RegionPainting> = listOf(
    RegionPainting("WHITE_ORCHARD", "White Orchard", Res.drawable.img_loc_white_orchard),
    RegionPainting("VELEN", "Velen", Res.drawable.img_loc_velen),
    RegionPainting("NOVIGRAD", "Novigrad", Res.drawable.img_loc_novigrad),
    RegionPainting("SKELLIGE", "Skellige", Res.drawable.img_loc_skellige),
    RegionPainting("KAER_MORHEN", "Kaer Morhen", Res.drawable.img_loc_kaer_morhen),
    RegionPainting("TOUSSAINT", "Toussaint", Res.drawable.img_loc_toussaint),
    RegionPainting("HEART_OF_STONE", "Oxenfurt", Res.drawable.img_loc_oxenfurt),
)

@Composable
fun RegionPaintingStrip(selectedRegion: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Text(
            text = "Places on the Path",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WitcherAmberGold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            regionPaintings().forEach { place ->
                val selected = selectedRegion == place.regionId
                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .clickable {
                            onSelect(if (selected) "ALL" else place.regionId)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(place.image),
                        contentDescription = place.label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) WitcherAmberGold else WitcherBorderColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = place.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) WitcherAmberGold else WitcherWhiteText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun GwentGalleryCard() {
    val deck = remember { gwentDeck() }
    val factions = remember(deck) {
        listOf("All") + deck.map { it.faction }.filter { it != "All" }.distinct()
    }
    var factionIndex by remember { mutableIntStateOf(0) }
    val selectedFaction = factions[factionIndex]
    val shown = if (selectedFaction == "All") deck else deck.filter { it.faction == selectedFaction }
    val playable = deck.filter { it.name != "Card Back" }
    val heroes = playable.count { it.isHeroOrLeader() }
    val power = shown.filter { it.name != "Card Back" }.sumOf { it.strength }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gwent Deck",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold
                )
                Surface(
                    modifier = Modifier.clickable {
                        factionIndex = (factionIndex + 1) % factions.size
                    },
                    color = WitcherRedPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, WitcherRedPrimary)
                ) {
                    Text(
                        text = selectedFaction,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = "Tap the faction chip to walk the deck. Hero and leader counts follow the cards in view.",
                fontSize = 11.sp,
                color = WitcherMutedText,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                shown.forEach { card ->
                    val featured = card.isHeroOrLeader()
                    Column(
                        modifier = Modifier.width(340.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(card.image),
                            contentDescription = "${card.name} Gwent card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (featured) 2.dp else 1.dp,
                                    color = if (featured) WitcherAmberGold else WitcherBorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = card.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (featured) WitcherAmberGold else WitcherWhiteText,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = card.detail,
                            fontSize = 10.sp,
                            color = WitcherMutedText,
                            textAlign = TextAlign.Center
                        )
                        if (card.name != "Card Back") {
                            Text(
                                text = card.faction,
                                fontSize = 9.sp,
                                color = WitcherAmberGold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Deck Size", fontSize = 10.sp, color = WitcherMutedText)
                    Text("${playable.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hero & Leaders", fontSize = 10.sp, color = WitcherMutedText)
                    Text("$heroes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherAmberGold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Shown Power", fontSize = 10.sp, color = WitcherMutedText)
                    Text("$power PTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                }
            }
        }
    }
}

data class GlossaryEntry(
    val id: String,
    val label: String,
    val image: DrawableResource,
)

fun glossaryEntries(): List<GlossaryEntry> = listOf(
    GlossaryEntry("JOURNAL", "Journal", Res.drawable.img_nav_journal),
    GlossaryEntry("BEASTS", "Beasts", Res.drawable.img_nav_beast),
    GlossaryEntry("ALCHEMY", "Alchemy", Res.drawable.img_nav_potion),
    GlossaryEntry("GWENT", "Gwent", Res.drawable.img_gwent_back),
    GlossaryEntry("GEAR", "Gear", Res.drawable.img_nav_gear),
    GlossaryEntry("COUNSEL", "Counsel", Res.drawable.img_gwent_geralt),
)

fun alchemyCategoryImage(category: String): DrawableResource =
    if (category == "OIL") Res.drawable.img_alchemy_oil else Res.drawable.img_alchemy_potion

fun counselPortrait(name: String): DrawableResource = when (name) {
    "Vesemir" -> Res.drawable.img_gwent_vesemir
    "Yennefer" -> Res.drawable.img_gwent_yennefer
    "Jaskier" -> Res.drawable.img_gwent_jaskier
    else -> Res.drawable.img_gwent_geralt
}

@Composable
fun PlaceTitle(text: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = WitcherAmberGold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(6.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(WitcherAmberGold)
        )
    }
}

@Composable
fun GlossaryRail(currentTab: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(WitcherDarkSurface)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        glossaryEntries().forEach { entry ->
            val selected = currentTab == entry.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelect(entry.id) }
                    .background(if (selected) WitcherRedPrimary.copy(alpha = 0.18f) else androidx.compose.ui.graphics.Color.Transparent)
                    .border(
                        width = if (selected) 1.dp else 0.dp,
                        color = if (selected) WitcherAmberGold else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(entry.image),
                    contentDescription = entry.label,
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = entry.label,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) WitcherAmberGold else WitcherMutedText,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                )
            }
        }
    }
}

@Composable
fun RemasteredNotesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WitcherAmberGold, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "The Witcher 3: Wild Hunt — Remastered",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherAmberGold
            )
            Text(
                text = "Version 1.1.0 · what changed in this companion",
                fontSize = 10.sp,
                color = WitcherMutedText
            )
            Text("• New Gwent cards, region paintings, and bestiary art", fontSize = 11.sp, color = WitcherWhiteText)
            Text("• Three-rank skill tree. Later skills need their prerequisite.", fontSize = 11.sp, color = WitcherWhiteText)
            Text("• Reforge looks at Yoana or Hattori. Stats and dyes stay.", fontSize = 11.sp, color = WitcherWhiteText)
            Text("• Track a quest from the journal. Tracked quests sort first.", fontSize = 11.sp, color = WitcherWhiteText)
        }
    }
}
