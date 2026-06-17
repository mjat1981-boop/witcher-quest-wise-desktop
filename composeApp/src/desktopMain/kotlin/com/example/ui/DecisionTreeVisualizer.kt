package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun DecisionTreeVisualizer(
    quest: Quest,
    onNotesChange: (String) -> Unit
) {
    val tree = remember(quest.title) { QuestDecisionTree.getTreeForQuest(quest.title) }

    if (tree != null) {
        BuiltInDecisionTree(tree = tree, currentNotes = quest.notes, onNotesChange = onNotesChange)
    } else {
        CustomDecisionPlanner(quest = quest, currentNotes = quest.notes, onNotesChange = onNotesChange)
    }
}

@Composable
fun BuiltInDecisionTree(
    tree: QuestDecisionTree,
    currentNotes: String,
    onNotesChange: (String) -> Unit
) {
    // Default to the first path, or if one of the paths has been logged previously, select it
    var selectedPathId by remember(tree.questTitle) {
        val initiallySelected = tree.paths.find { path ->
            currentNotes.contains(path.summary.take(20), ignoreCase = true) ||
            currentNotes.contains(path.choiceName.replace("🟢", "").replace("🔴", "").trim().take(15), ignoreCase = true)
        }?.id ?: tree.paths.firstOrNull()?.id ?: ""
        mutableStateOf(initiallySelected)
    }

    val selectedPath = remember(selectedPathId) {
        tree.paths.find { it.id == selectedPathId } ?: tree.paths.firstOrNull()
    }

    var isLoggedSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPathId) {
        // Reset success indicator on switching paths
        isLoggedSuccess = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(WitcherDarkSurface, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, WitcherBorderColor), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = WitcherAmberGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "⚔️ CONFLICTING DESTINIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = WitcherAmberGold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tree.dilemmaTitle,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = WitcherWhiteText
        )
        Text(
            text = tree.description,
            fontSize = 11.sp,
            color = WitcherMutedText,
            modifier = Modifier.padding(vertical = 4.dp),
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large choice switcher tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tree.paths.forEach { path ->
                val isSelected = path.id == selectedPathId
                val accentColor = if (path.choiceName.contains("🟢")) WitcherSuccess else WitcherFailed

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPathId = path.id }
                        .border(
                            BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) WitcherAmberGold else WitcherBorderColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    color = if (isSelected) WitcherDarkSurfaceVariant else WitcherDarkBackground,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = path.choiceName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
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

        // Flow transition for the details of the selected path
        selectedPath?.let { path ->
            Spacer(modifier = Modifier.height(12.dp))

            // Connector Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    color = WitcherBorderColor,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }

            // Node 1: Immediate Outcome
            ConsequenceNodeCard(
                node = path.immediateNode,
                badgeLabel = "⚡ Immediate Outcome",
                icon = Icons.Default.PlayArrow
            )

            // Connector Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    color = WitcherBorderColor,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }

            // Node 2: Long Term Fate
            ConsequenceNodeCard(
                node = path.longTermNode,
                badgeLabel = "🔮 Chronicle & Long-Term Fate",
                icon = Icons.Default.Info
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Log Path Action Button
            val isAlreadyLogged = currentNotes.contains(path.logMessage.take(30))
            Button(
                onClick = {
                    onNotesChange(path.logMessage)
                    isLoggedSuccess = true
                },
                modifier = Modifier.fillMaxWidth(),
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
                            contentDescription = "Logged to Journal",
                            tint = WitcherSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DESTINY RECORDED IN JOURNAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherSuccess
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Log Path",
                            tint = WitcherWhiteText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COMMITTED: LOG THIS DESTINY",
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

@Composable
fun ConsequenceNodeCard(
    node: DecisionNode,
    badgeLabel: String,
    icon: ImageVector
) {
    val nodeBg = when (node.severity) {
        ConsequenceSeverity.POSITIVE -> WitcherSuccess.copy(alpha = 0.08f)
        ConsequenceSeverity.NEGATIVE -> WitcherFailed.copy(alpha = 0.08f)
        ConsequenceSeverity.NEUTRAL -> WitcherDarkBackground
    }

    val nodeBorder = when (node.severity) {
        ConsequenceSeverity.POSITIVE -> WitcherSuccess.copy(alpha = 0.6f)
        ConsequenceSeverity.NEGATIVE -> WitcherFailed.copy(alpha = 0.6f)
        ConsequenceSeverity.NEUTRAL -> WitcherBorderColor
    }

    val badgeColor = when (node.severity) {
        ConsequenceSeverity.POSITIVE -> WitcherSuccess
        ConsequenceSeverity.NEGATIVE -> WitcherFailed
        ConsequenceSeverity.NEUTRAL -> WitcherAmberGold
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, nodeBorder), RoundedCornerShape(10.dp)),
        color = nodeBg,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Node Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badgeLabel.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor,
                        letterSpacing = 0.5.sp
                    )
                }

                if (node.flavorNote != null) {
                    Text(
                        text = "Tactical note",
                        fontSize = 9.sp,
                        color = WitcherMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = node.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherWhiteText
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = node.description,
                fontSize = 11.sp,
                color = WitcherMutedText,
                lineHeight = 14.sp
            )

            node.flavorNote?.let { flavor ->
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = WitcherDarkSurfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📖 $flavor",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = WitcherAmberGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CustomDecisionPlanner(
    quest: Quest,
    currentNotes: String,
    onNotesChange: (String) -> Unit
) {
    var dilemmaInput by remember { mutableStateOf("") }
    var choiceInput by remember { mutableStateOf("") }
    var consequenceInput by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("NEUTRAL") } // "POSITIVE", "NEUTRAL", "NEGATIVE"
    var isLoggedSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(WitcherDarkSurface, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, WitcherBorderColor), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = WitcherAmberGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "🛡️ CONTRACT DESTINY SIMULATOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = WitcherAmberGold,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "No recorded branch on the Path exists. Simulate this custom quest's moral choice and log its final chronicle.",
            fontSize = 11.sp,
            color = WitcherMutedText,
            modifier = Modifier.padding(vertical = 4.dp),
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input 1: The Dilemma
        Text("Dilemma / Core Choice", fontSize = 10.sp, color = WitcherMutedText)
        OutlinedTextField(
            value = dilemmaInput,
            onValueChange = { dilemmaInput = it; isLoggedSuccess = false },
            placeholder = { Text("e.g. Slay the dragon or take its gold bribes?", fontSize = 11.sp, color = WitcherMutedText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WitcherDarkBackground,
                unfocusedContainerColor = WitcherDarkBackground,
                focusedBorderColor = WitcherRedPrimary,
                unfocusedBorderColor = WitcherDarkBackground,
                focusedTextColor = WitcherWhiteText,
                unfocusedTextColor = WitcherWhiteText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            shape = RoundedCornerShape(6.dp),
            textStyle = TextStyle(fontSize = 11.sp)
        )

        // Input 2: Your Path Chosen
        Text("Choice Taken", fontSize = 10.sp, color = WitcherMutedText)
        OutlinedTextField(
            value = choiceInput,
            onValueChange = { choiceInput = it; isLoggedSuccess = false },
            placeholder = { Text("e.g. Cleansed the beast, rejecting the coin.", fontSize = 11.sp, color = WitcherMutedText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WitcherDarkBackground,
                unfocusedContainerColor = WitcherDarkBackground,
                focusedBorderColor = WitcherRedPrimary,
                unfocusedBorderColor = WitcherDarkBackground,
                focusedTextColor = WitcherWhiteText,
                unfocusedTextColor = WitcherWhiteText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            shape = RoundedCornerShape(6.dp),
            textStyle = TextStyle(fontSize = 11.sp)
        )

        // Input 3: Final Chronicles Impact
        Text("Chronicled Consequence", fontSize = 10.sp, color = WitcherMutedText)
        OutlinedTextField(
            value = consequenceInput,
            onValueChange = { consequenceInput = it; isLoggedSuccess = false },
            placeholder = { Text("e.g. The beast spared the village but left starving. Locals blame Geralt.", fontSize = 11.sp, color = WitcherMutedText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WitcherDarkBackground,
                unfocusedContainerColor = WitcherDarkBackground,
                focusedBorderColor = WitcherRedPrimary,
                unfocusedBorderColor = WitcherDarkBackground,
                focusedTextColor = WitcherWhiteText,
                unfocusedTextColor = WitcherWhiteText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(6.dp),
            textStyle = TextStyle(fontSize = 11.sp)
        )

        // Consequence Tone Selector
        Text("Path Moral Tone", fontSize = 10.sp, color = WitcherMutedText, modifier = Modifier.padding(bottom = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tones = listOf(
                Triple("POSITIVE", "Green Path 🌿", WitcherSuccess),
                Triple("NEUTRAL", "Lesser Evil ⚖️", WitcherAmberGold),
                Triple("NEGATIVE", "Dark Fate 🪓", WitcherFailed)
            )

            tones.forEach { (code, label, color) ->
                val isSelected = selectedTone == code
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTone = code }
                        .border(
                            BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) color else WitcherBorderColor
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    color = if (isSelected) color.copy(alpha = 0.15f) else WitcherDarkBackground,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) color else WitcherWhiteText,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save Custom Path Button
        val isValid = dilemmaInput.isNotBlank() && choiceInput.isNotBlank()
        Button(
            onClick = {
                val emoji = when (selectedTone) {
                    "POSITIVE" -> "🌿 [NOBLE PATH]"
                    "NEGATIVE" -> "🪓 [DARK FATE]"
                    else -> "⚖️ [LESSER EVIL]"
                }
                val formattedLog = """
                    $emoji Dilemma: $dilemmaInput
                    Decision: Tried to solve via: $choiceInput
                    Consequences: $consequenceInput
                """.trimIndent()
                onNotesChange(formattedLog)
                isLoggedSuccess = true
            },
            enabled = isValid && !isLoggedSuccess,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoggedSuccess) WitcherDarkSurfaceVariant else WitcherRedPrimary
            ),
            border = BorderStroke(1.dp, if (isLoggedSuccess) WitcherSuccess else WitcherAmberGold),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoggedSuccess) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Logged",
                        tint = WitcherSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CUSTOM CHRONICLE LOGGED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherSuccess
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Apply Planner",
                        tint = if (isValid) WitcherWhiteText else WitcherMutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECORD CUSTOM DESTINY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isValid) WitcherWhiteText else WitcherMutedText
                    )
                }
            }
        }
    }
}
