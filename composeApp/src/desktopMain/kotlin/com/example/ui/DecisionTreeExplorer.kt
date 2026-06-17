package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun DecisionTreeExplorer(
    viewModel: QuestViewModel,
    quests: List<Quest>
) {
    var searchQuery by remember { mutableStateOf("") }
    val allTrees = remember { QuestDecisionTree.getAllTrees() }
    
    val filteredTrees = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allTrees
        } else {
            allTrees.filter {
                it.questTitle.contains(searchQuery, ignoreCase = true) ||
                it.dilemmaTitle.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    var selectedTreeQuestTitle by remember { 
        mutableStateOf(allTrees.firstOrNull()?.questTitle ?: "") 
    }
    
    val selectedTree = remember(selectedTreeQuestTitle) {
        allTrees.find { it.questTitle == selectedTreeQuestTitle } ?: allTrees.firstOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        // Search & Filter bar for Decision Trees
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("decision_tree_search"),
            placeholder = { Text("Search critical decisions (e.g. Baron, Anabelle)...", color = WitcherMutedText, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Decisions", tint = WitcherMutedText) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WitcherDarkSurface,
                unfocusedContainerColor = WitcherDarkSurface,
                focusedBorderColor = WitcherRedPrimary,
                unfocusedBorderColor = WitcherDarkSurfaceVariant,
                focusedTextColor = WitcherWhiteText,
                unfocusedTextColor = WitcherWhiteText
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Main Adaptive Split Layout: Horizontal if enough space, else scroll list plus expand detail
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val isWideScreen = maxWidth > 600.dp
            
            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left list panel: Key quests
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "CRITICAL PATHWAYS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredTrees) { tree ->
                                val isSelected = tree.questTitle == selectedTreeQuestTitle
                                val matchedQuest = quests.find { it.title.equals(tree.questTitle, ignoreCase = true) }
                                val hasDecisionLogged = matchedQuest?.notes?.isNotBlank() == true
                                
                                KeyDilemmaSelectorCard(
                                    tree = tree,
                                    isSelected = isSelected,
                                    hasDecisionLogged = hasDecisionLogged,
                                    onClick = { selectedTreeQuestTitle = tree.questTitle }
                                )
                            }
                        }
                    }

                    // Right detail panel: Flow View
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        selectedTree?.let { tree ->
                            val matchedQuest = quests.find { it.title.equals(tree.questTitle, ignoreCase = true) }
                            DetailTreeVisualFlow(
                                tree = tree,
                                matchedQuest = matchedQuest,
                                onChoosePath = { loggedReason, choiceName ->
                                    matchedQuest?.let { q ->
                                        viewModel.updateQuestNotes(q.id, loggedReason)
                                        viewModel.updateQuestNarrativeChoices(q.id, choiceName)
                                    }
                                }
                            )
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No decision tree matches your query.", color = WitcherMutedText)
                        }
                    }
                }
            } else {
                // Compact Screen: Single scroll layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "CRITICAL PATHWAYS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Horizontal quick scroll selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredTrees.forEach { tree ->
                            val isSelected = tree.questTitle == selectedTreeQuestTitle
                            val matchedQuest = quests.find { it.title.equals(tree.questTitle, ignoreCase = true) }
                            val hasDecisionLogged = matchedQuest?.notes?.isNotBlank() == true
                            
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isSelected) WitcherAmberGold else WitcherBorderColor),
                                color = if (isSelected) WitcherRedPrimary.copy(alpha = 0.2f) else WitcherDarkSurface,
                                modifier = Modifier
                                    .widthIn(max = 160.dp)
                                    .clickable { selectedTreeQuestTitle = tree.questTitle }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = tree.questTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) WitcherAmberGold else WitcherWhiteText,
                                        maxLines = 1,
                                        modifier = Modifier.testTag("compact_tree_select_${tree.questTitle}")
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (hasDecisionLogged) "✔ Recorded" else "⏳ Unsolved",
                                        fontSize = 9.sp,
                                        color = if (hasDecisionLogged) WitcherSuccess else WitcherMutedText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    selectedTree?.let { tree ->
                        val matchedQuest = quests.find { it.title.equals(tree.questTitle, ignoreCase = true) }
                        DetailTreeVisualFlow(
                            tree = tree,
                            matchedQuest = matchedQuest,
                            onChoosePath = { loggedReason, choiceName ->
                                matchedQuest?.let { q ->
                                    viewModel.updateQuestNotes(q.id, loggedReason)
                                    viewModel.updateQuestNarrativeChoices(q.id, choiceName)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeyDilemmaSelectorCard(
    tree: QuestDecisionTree,
    isSelected: Boolean,
    hasDecisionLogged: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isSelected) WitcherAmberGold else WitcherBorderColor,
                RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("dilemma_selector_${tree.questTitle}"),
        color = if (isSelected) WitcherDarkSurfaceVariant else WitcherDarkSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tree.questTitle.uppercase(),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = WitcherWhiteText
                )
                
                if (hasDecisionLogged) {
                    Surface(
                        color = WitcherSuccess.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, WitcherSuccess),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "CHRONICLED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherSuccess,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = WitcherAmberGold.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, WitcherBorderColor),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "UNLOGGED PATH",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherMutedText,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tree.dilemmaTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherAmberGold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tree.description,
                fontSize = 10.sp,
                color = WitcherMutedText,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun DetailTreeVisualFlow(
    tree: QuestDecisionTree,
    matchedQuest: Quest?,
    onChoosePath: (String, String) -> Unit
) {
    val currentNotes = matchedQuest?.notes ?: ""
    val currentChoices = matchedQuest?.narrativeChoices ?: ""
    
    var selectedPathId by remember(tree.questTitle) {
        val initiallySelected = tree.paths.find { path ->
            currentChoices.contains(path.choiceName.replace("🟢", "").replace("🔴", "").trim().take(10)) ||
            currentNotes.contains(path.summary.take(20), ignoreCase = true)
        }?.id ?: tree.paths.firstOrNull()?.id ?: ""
        mutableStateOf(initiallySelected)
    }

    val selectedPath = remember(selectedPathId) {
        tree.paths.find { it.id == selectedPathId } ?: tree.paths.firstOrNull()
    }

    var isLoggedSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPathId) {
        isLoggedSuccess = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(WitcherDarkSurface, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, WitcherBorderColor), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Detailed Header
        Text(
            text = tree.questTitle.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WitcherAmberGold,
            letterSpacing = 1.5.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Dilemma: ${tree.dilemmaTitle}",
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = WitcherWhiteText
        )
        
        Text(
            text = tree.description,
            fontSize = 12.sp,
            color = WitcherMutedText,
            lineHeight = 16.sp,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        // Status check
        if (matchedQuest == null) {
            Surface(
                color = WitcherRedPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, WitcherRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WitcherRedPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "This quest is not in your current game save. Showing default lore branches.",
                        fontSize = 10.sp,
                        color = WitcherWhiteText
                    )
                }
            }
        } else {
            val badgeColor = when (matchedQuest.status) {
                "COMPLETED" -> WitcherSuccess
                "IN_PROGRESS" -> WitcherAmberGold
                "FAILED" -> WitcherFailed
                else -> WitcherMutedText
            }
            Surface(
                color = badgeColor.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STATUS ON THE PATH:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherMutedText
                    )
                    Text(
                        text = matchedQuest.status.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = WitcherBorderColor)
        Spacer(modifier = Modifier.height(14.dp))

        // Large choice selection tabs
        Text(
            text = "CHOOSE YOUR PATH",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = WitcherAmberGold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tree.paths.forEach { path ->
                val isSelected = path.id == selectedPathId
                val choiceColor = if (path.choiceName.contains("🟢")) WitcherSuccess else WitcherFailed
                
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPathId = path.id }
                        .border(
                            BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) WitcherAmberGold else WitcherBorderColor
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .testTag("branch_tab_${path.id}"),
                    color = if (isSelected) WitcherDarkSurfaceVariant else WitcherDarkBackground,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = path.choiceName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) WitcherAmberGold else WitcherWhiteText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = path.summary,
                            fontSize = 10.sp,
                            color = WitcherMutedText,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Selected Path details
        selectedPath?.let { path ->
            Spacer(modifier = Modifier.height(12.dp))

            // Spacer with connector dots
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(WitcherBorderColor)
                        )
                    }
                }
            }

            // Outcome & Fate flows
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Immediate Consequence Card
                ConsequenceNodeCard(
                    node = path.immediateNode,
                    badgeLabel = "⚡ Immediate Outcome",
                    icon = Icons.Default.PlayArrow
                )

                // Long Term Consequence Card
                ConsequenceNodeCard(
                    node = path.longTermNode,
                    badgeLabel = "🔮 Chronicle & Long-Term Fate",
                    icon = Icons.Default.Info
                )
                
                // DETAILED REWARDS AND NARRATIVE OUTCOME PANEL
                val pathRewards = getRewardsForPath(path.id)
                OutcomeAndRewardsCard(rewards = pathRewards)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (matchedQuest != null) {
                // Log/Commit Decision Button
                val isAlreadyLogged = currentNotes.contains(path.logMessage.take(30))
                Button(
                    onClick = {
                        onChoosePath(path.logMessage, path.choiceName.replace("🟢", "").replace("🔴", "").trim())
                        isLoggedSuccess = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_decision_btn_${path.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlreadyLogged || isLoggedSuccess) WitcherDarkSurfaceVariant else WitcherRedPrimary
                    ),
                    border = BorderStroke(1.dp, if (isAlreadyLogged || isLoggedSuccess) WitcherSuccess else WitcherAmberGold),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isAlreadyLogged && !isLoggedSuccess
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isAlreadyLogged || isLoggedSuccess) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Decision Logged",
                                tint = WitcherSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DESTINY PERMANENTLY RECORDED IN JOURNAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitcherSuccess
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Log Path",
                                tint = WitcherWhiteText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COMMIT TO THIS DESTINY AND LOG CHRONICLE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitcherWhiteText
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom model for decision rewards
data class PathRewardsAndOutcomes(
    val xp: String,
    val crowns: String,
    val keyItems: List<String>,
    val alignmentTone: String, // e.g. "LESSER EVIL", "TRAGIC FATE", "HONORABLE PATH"
    val storyConsequences: List<String>
)

fun getRewardsForPath(pathId: String): PathRewardsAndOutcomes {
    return when(pathId) {
        "hillock_free", "ladies_free" -> PathRewardsAndOutcomes(
            xp = "100 XP (for completing the Black Stallion ritual)",
            crowns = "0 Crowns (Crones deny any reward coin)",
            keyItems = listOf("Black Stallion stallion bones", "Velen swamp herbs"),
            alignmentTone = "TRAGIC FATALISM",
            storyConsequences = listOf(
                "The innocent orphans of Crookback Bog are rescued safely and transported to safety.",
                "Furiously betrayed, the Crones turn Anna Stenger into a cursed, rotting Water Hag.",
                "Geralt lifts the Hag curse, but Anna dies instantly in her weeping family's arms.",
                "Consumed by alcohol and deep grief, the Bloody Baron hangs himself from a tree.",
                "The razed Downwarren village is completely incinerated by the freed, vengeful spirit."
            )
        )
        "hillock_slay", "ladies_slay" -> PathRewardsAndOutcomes(
            xp = "250 XP (for heavy combat victory over the spirit heart)",
            crowns = "100 Crowns (The Ealdorman of Downwarren pays you gold)",
            keyItems = listOf("Ealdorman's ear (Crones tribute)", "Swamp Hag trophies"),
            alignmentTone = "LESSER EVIL PATH",
            storyConsequences = listOf(
                "The ancient spirit is locked out or destroyed, protecting Velen's local structures.",
                "The orphans of Crookback Bog are stolen and consumed by the sinister Crones.",
                "Spared of the curse, Anna loses her mind but survives the swamp terrors.",
                "The Bloody Baron survives, swears off drinking, and travels to secure a healer.",
                "Downwarren is spared, remaining fully standing under the rule of the local Ealdorman."
            )
        )
        "mice_trust" -> PathRewardsAndOutcomes(
            xp = "150 XP (for delivery of haunted bones)",
            crowns = "0 Crowns (Tragic client death)",
            keyItems = listOf("Anabelle's mortal bones"),
            alignmentTone = "TRAGIC BETRAYAL",
            storyConsequences = listOf(
                "Graham receives Anabelle's bones and grants them burial, thinking she is at peace.",
                "Anabelle turns into a monstrous, highly infectious Pesta (Plague Maiden).",
                "She brutally breaks Graham's spine, murders him on the spot, and escapes.",
                "The deadly plague spreads uncontrollably across Velen villages, causing massive casualties."
            )
        )
        "mice_confront" -> PathRewardsAndOutcomes(
            xp = "300 XP (for lifting ancient curse and combat)",
            crowns = "50 Crowns (Grateful Keira reward)",
            keyItems = listOf("Fyke island specter dust", "Keira's diagnostic notes"),
            alignmentTone = "HONORABLE SACRIFICE",
            storyConsequences = listOf(
                "Geralt sees through Anabelle's specter illusion and refuses to move the bones.",
                "Graham is guided to the tower and courageously kisses Anabelle's horrific corpse.",
                "The deep love bond breaks the plague curse, saving Fyke Island permanently.",
                "Graham dies peaceful in the action, letting their souls enter the afterlife together.",
                "Fyke is purified, unlocking Keira Metz's active assistance later at Kaer Morhen."
            )
        )
        "yen_love" -> PathRewardsAndOutcomes(
            xp = "200 XP (for lifting the Djinn connection)",
            crowns = "0 Crowns (Love cannot be bought)",
            keyItems = listOf("Djinn magical shard"),
            alignmentTone = "DESTINY SOLIDIFIED",
            storyConsequences = listOf(
                "Geralt and Yennefer confirm that their affection was completely real, not spell-driven.",
                "Locks romance with Yennefer of Vengerberg for the rest of the game.",
                "Unlocks the Kovir cozy retirement ending, spending warm sunlit days in a peaceful cabin.",
                "Changes key dialogue options during the Kaer Morhen final battle preparations."
            )
        )
        "yen_refuse" -> PathRewardsAndOutcomes(
            xp = "100 XP (for closing the romance pathway)",
            crowns = "0 Crowns",
            keyItems = listOf("Broken magical thread"),
            alignmentTone = "LONE PATHWAY",
            storyConsequences = listOf(
                "Geralt informs Yennefer that without the Djinn's magical bond, the romance has vanished.",
                "The romance line with Yennefer is instantly severed, though friendship can remain.",
                "Keeps the player free to pursue a cozy life with Triss Merigold in Kovir.",
                "Allows Geralt to remain a Lone Wolf wandering the Path till old age."
            )
        )
        "rose_take" -> PathRewardsAndOutcomes(
            xp = "500 XP (for complete master contract completion)",
            crowns = "300 Crowns (Olgierd's safe vault payload)",
            keyItems = listOf("The Physical Violet Rose (Iris' anchor)"),
            alignmentTone = "HONORABLE MERCY",
            storyConsequences = listOf(
                "Taking the rose dissolves the painted world sanctuary, freeing Iris' trapped soul.",
                "Iris fades peacefully into eternal sleep, grateful for her long-awaited liberation.",
                "Fulfills the exact final demand of Olgierd von Everec's contract.",
                "Unlocks the Gaunter O'Dimm showdown at the temple courtyard segment."
            )
        )
        "rose_leave" -> PathRewardsAndOutcomes(
            xp = "300 XP (for partial contract alternative)",
            crowns = "100 Crowns",
            keyItems = listOf("Oil portrait of Iris holding the Rose"),
            alignmentTone = "SOMBER SANCTUARY",
            storyConsequences = listOf(
                "Iris keeps her rose, preserving her quiet painted sanctuary, but remains trapped with grief.",
                "Geralt cuts out the portrait to prove to Olgierd that he found her and the rose.",
                "Olgierd is deeply struck by his wife's frozen state, breaking through his stony heart.",
                "Olgierd experiences agonizing, genuine emotional heartbreak over his past actions."
            )
        )
        else -> PathRewardsAndOutcomes(
            xp = "200 XP",
            crowns = "100 Crowns",
            keyItems = emptyList(),
            alignmentTone = "LESSER EVIL",
            storyConsequences = listOf(
                "Outcome is registered in the Velen journal logging pages.",
                "Slight changes to minor dialogue sections with village heads."
            )
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun OutcomeAndRewardsCard(rewards: PathRewardsAndOutcomes) {
    val highlightBorder = when (rewards.alignmentTone) {
        "HONORABLE PATH", "HONORABLE SACRIFICE", "DESTINY SOLIDIFIED", "HONORABLE MERCY" -> WitcherSuccess.copy(alpha = 0.8f)
        "TRAGIC FATALISM", "TRAGIC BETRAYAL" -> WitcherFailed.copy(alpha = 0.8f)
        else -> WitcherBorderColor
    }
    
    val highlightTitleColor = when (rewards.alignmentTone) {
        "HONORABLE PATH", "HONORABLE SACRIFICE", "DESTINY SOLIDIFIED", "HONORABLE MERCY" -> WitcherSuccess
        "TRAGIC FATALISM", "TRAGIC BETRAYAL" -> WitcherFailed
        else -> WitcherAmberGold
    }

    Surface(
        color = WitcherDarkBackground,
        border = BorderStroke(1.2.dp, highlightBorder),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .testTag("rewards_card_${rewards.alignmentTone.replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rewards details",
                        tint = WitcherAmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚖️ PATH ANALYSIS & REWARDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WitcherAmberGold,
                        letterSpacing = 0.5.sp
                    )
                }
                
                Surface(
                    color = highlightTitleColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, highlightBorder)
                ) {
                    Text(
                        text = rewards.alignmentTone,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = highlightTitleColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Loot detail boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // XP Box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = WitcherDarkSurfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, WitcherBorderColor)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("EXPERIENCE GAINED", fontSize = 8.sp, color = WitcherMutedText, fontWeight = FontWeight.Bold)
                        Text(rewards.xp, fontSize = 10.sp, color = WitcherWhiteText, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Crowns Box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = WitcherDarkSurfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, WitcherBorderColor)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("CONTRACT GOLD", fontSize = 8.sp, color = WitcherMutedText, fontWeight = FontWeight.Bold)
                        Text(rewards.crowns, fontSize = 10.sp, color = WitcherAmberGold, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Key Items
            if (rewards.keyItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("🎒 KEY ACQUISITIONS & LOOT:", fontSize = 9.sp, color = WitcherMutedText, fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rewards.keyItems.forEach { item ->
                        Surface(
                            color = WitcherDarkSurfaceVariant,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, WitcherBorderColor)
                        ) {
                            Text(
                                text = "⚔️ $item",
                                fontSize = 9.sp,
                                color = WitcherWhiteText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Detailed Story Consequences list
            Spacer(modifier = Modifier.height(12.dp))
            Text("🔮 WORLD STATE IMPACTS:", fontSize = 9.sp, color = WitcherMutedText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                rewards.storyConsequences.forEach { consequence ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "🐾 ",
                            fontSize = 11.sp,
                            color = highlightTitleColor
                        )
                        Text(
                            text = consequence,
                            fontSize = 11.sp,
                            color = WitcherWhiteText,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
