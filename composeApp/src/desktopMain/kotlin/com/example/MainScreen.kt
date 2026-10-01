package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalFocusManager
import java.util.Locale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Monster
import com.example.data.Quest
import com.example.ui.ChatMessage
import com.example.ui.QuestViewModel
import com.example.ui.WitcherSkill
import com.example.ui.DecisionTreeVisualizer
import com.example.ui.DecisionTreeExplorer
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import witcher_quest_wise_desktop.composeapp.generated.resources.Res
import witcher_quest_wise_desktop.composeapp.generated.resources.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(viewModel: QuestViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val filteredQuests by viewModel.filteredQuests.collectAsStateWithLifecycle()
    val selectedQuest by viewModel.selectedQuest.collectAsStateWithLifecycle()

    var showAddQuestDialog by remember { mutableStateOf(false) }
    var showSaddlebagsSidebar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WitcherDarkBackground,
        bottomBar = {
            WitcherBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        floatingActionButton = {
            if (currentTab == "JOURNAL") {
                FloatingActionButton(
                    onClick = { showAddQuestDialog = true },
                    containerColor = WitcherRedPrimary,
                    contentColor = WitcherWhiteText,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom quest/task",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            WitcherDarkBackground,
                            Color(0xFF16151A),
                            WitcherDarkBackground
                        )
                    )
                )
        ) {
            // Screen switching with animateContentSize or Crossfade
            Crossfade(targetState = currentTab, label = "TabTransition") { tab ->
                when (tab) {
                    "JOURNAL" -> JournalTab(viewModel, filteredQuests)
                    "BESTIARY" -> BestiaryTab(viewModel)
                    "CHAT" -> ChatTab(viewModel)
                    "ADVISOR" -> AdvisorTab(viewModel)
                    "PROFILE" -> ProfileTab(viewModel, quests)
                }
            }

            // Universal Floating Saddlebags Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .testTag("floating_saddlebags_button"),
                color = WitcherDarkSurface,
                border = BorderStroke(1.dp, WitcherAmberGold),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .clickable { showSaddlebagsSidebar = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("👜", fontSize = 16.sp)
                    Text("Saddlebags", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Slide out Saddlebags Sidebar Drawer Overlay
            AnimatedVisibility(
                visible = showSaddlebagsSidebar,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.fillMaxSize().zIndex(99f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { showSaddlebagsSidebar = false }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = 340.dp)
                            .fillMaxWidth(0.85f)
                            .align(Alignment.CenterEnd)
                            .border(2.dp, WitcherAmberGold, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                        color = WitcherDarkBackground
                    ) {
                        com.example.ui.SaddlebagSidebarContent(
                            viewModel = viewModel,
                            onClose = { showSaddlebagsSidebar = false }
                        )
                    }
                }
            }

            // Dialog for Quest Details
            selectedQuest?.let { quest ->
                QuestDetailsDialog(
                    quest = quest,
                    viewModel = viewModel,
                    onDismiss = { viewModel.selectQuest(null) },
                    onStatusChange = { newStatus -> viewModel.updateQuestStatus(quest.id, newStatus) },
                    onNotesChange = { newNotes -> viewModel.updateQuestNotes(quest.id, newNotes) },
                    onDelete = {
                        viewModel.deleteQuest(quest)
                        viewModel.selectQuest(null)
                    }
                )
            }

            // Dialog for Custom Quest Creation
            if (showAddQuestDialog) {
                AddQuestDialog(
                    onDismiss = { showAddQuestDialog = false },
                    onSave = { title, type, region, level, desc, giver, rewards, weaknesses ->
                        viewModel.addCustomQuest(title, type, region, level, desc, giver, rewards, weaknesses)
                        showAddQuestDialog = false
                    }
                )
            }
        }
    }
}

// Composite Witcher Bottom Nav Bar
@Composable
fun WitcherBottomNavBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = WitcherDarkSurfaceVariant, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = WitcherDarkSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Journal",
                iconText = "📜",
                isSelected = currentTab == "JOURNAL",
                onClick = { onTabSelected("JOURNAL") }
            )
            BottomNavItem(
                label = "Bestiary",
                iconText = "🐺",
                isSelected = currentTab == "BESTIARY",
                onClick = { onTabSelected("BESTIARY") }
            )
            BottomNavItem(
                label = "Counsel",
                iconText = "🕯️",
                isSelected = currentTab == "CHAT",
                onClick = { onTabSelected("CHAT") }
            )
            BottomNavItem(
                label = "Advisor",
                iconText = "🧪",
                isSelected = currentTab == "ADVISOR",
                onClick = { onTabSelected("ADVISOR") }
            )
            BottomNavItem(
                label = "Profile",
                iconText = "⚔️",
                isSelected = currentTab == "PROFILE",
                onClick = { onTabSelected("PROFILE") }
            )
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    label: String,
    iconText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = iconText,
            fontSize = 18.sp,
            color = if (isSelected) WitcherRedPrimary else WitcherMutedText
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) WitcherRedPrimary else WitcherMutedText
        )
    }
}

// JOURNAL TAB COMPONENTS
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun JournalTab(
    viewModel: QuestViewModel,
    quests: List<Quest>
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val typeFilter by viewModel.typeFilter.collectAsStateWithLifecycle()
    val regionFilter by viewModel.regionFilter.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val levelRangeFilter by viewModel.levelRangeFilter.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val allQuests by viewModel.quests.collectAsStateWithLifecycle()

    var currentJournalSubTab by remember { mutableStateOf("QUESTS") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 150.dp) // Constrain header space
                .padding(bottom = 8.dp)
        ) {
            // Witcher styled Journal Header
            Text(
                text = "Matt's Witcher guide".uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp
                ),
                color = WitcherRedPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Manage your contracts, side pathways, and main destiny highlights.",
                style = MaterialTheme.typography.bodySmall,
                color = WitcherMutedText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Sub Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WitcherDarkSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Pair("QUESTS", "📜 Quest Log"),
                    Pair("DECISIONS", "⚖️ Destiny Branches")
                )
                tabs.forEach { (tabId, label) ->
                    val isSelected = currentJournalSubTab == tabId
                    Surface(
                        color = if (isSelected) WitcherRedPrimary else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { currentJournalSubTab = tabId }
                            .testTag("journal_sub_tab_$tabId")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) WitcherDarkBackground else WitcherWhiteText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        if (currentJournalSubTab == "QUESTS") {
            // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            placeholder = { Text("Search quests...", color = WitcherMutedText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WitcherMutedText) },
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

        // Filters Header Clickable Row to toggle visibility
        var showFilters by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { showFilters = !showFilters }
                .background(WitcherDarkSurface)
                .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚔️ Filters & Navigation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold
                )
                
                val activeFiltersCount = listOf(
                    typeFilter != "ALL",
                    regionFilter != "ALL",
                    statusFilter != "ALL",
                    levelRangeFilter != "ALL"
                ).count { it }
                
                if (activeFiltersCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = WitcherRedPrimary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, WitcherRedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$activeFiltersCount active",
                            color = WitcherRedPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Text(
                text = if (showFilters) "COLLAPSE ▲" else "EXPAND ▼",
                color = WitcherRedPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (!showFilters) {
            // Quick summary of selected filters when collapsed
            val summaryParts = mutableListOf<String>()
            if (typeFilter != "ALL") {
                val typeName = when(typeFilter) {
                    "MAIN" -> "Main Story"
                    "SIDE_CONTRACT" -> "Side Contracts"
                    "TREASURE" -> "Treasure"
                    else -> typeFilter
                }
                summaryParts.add(typeName)
            }
            if (regionFilter != "ALL") summaryParts.add(formatLabel(regionFilter))
            if (statusFilter != "ALL") {
                val statusName = when(statusFilter) {
                    "NOT_STARTED" -> "Not Started"
                    "IN_PROGRESS" -> "In Progress"
                    "COMPLETED" -> "Completed"
                    "FAILED" -> "Failed"
                    else -> statusFilter
                }
                summaryParts.add(statusName)
            }
            if (levelRangeFilter != "ALL") {
                val lvlName = when (levelRangeFilter) {
                    "1_5" -> "Novice (1-5)"
                    "6_15" -> "Journeyman (6-15)"
                    "16_25" -> "Master (16-25)"
                    "26_UP" -> "Legendary (26+)"
                    else -> levelRangeFilter
                }
                summaryParts.add(lvlName)
            }

            if (summaryParts.isNotEmpty() || sortBy != "LEVEL_ASC") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Selected: ", fontSize = 11.sp, color = WitcherMutedText)
                    summaryParts.forEach { part ->
                        Surface(
                            color = WitcherDarkSurfaceVariant,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, WitcherBorderColor)
                        ) {
                            Text(
                                text = part,
                                fontSize = 10.sp,
                                color = WitcherWhiteText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    // Quick clear button to clear all filters
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clear All ✖",
                        color = WitcherRedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                viewModel.setTypeFilter("ALL")
                                viewModel.setRegionFilter("ALL")
                                viewModel.setStatusFilter("ALL")
                                viewModel.setLevelRangeFilter("ALL")
                                viewModel.setSearchQuery("")
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            // Horizontal filter block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .verticalScroll(rememberScrollState())
                    .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .background(WitcherDarkSurface)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quest Type Filters Row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterTypes = listOf(
                        Triple("ALL", "All Paths", Icons.Default.List),
                        Triple("MAIN", "Main Story", Icons.Default.Star),
                        Triple("SIDE_CONTRACT", "Side Contracts", Icons.Default.Warning),
                        Triple("TREASURE", "Treasure Hunts", Icons.Default.Search)
                    )
                    filterTypes.forEach { (typeVal, labelText, iconRes) ->
                        FilterChipCustom(
                            label = labelText,
                            isSelected = typeFilter == typeVal,
                            icon = iconRes,
                            onClick = { viewModel.setTypeFilter(typeVal) }
                        )
                    }
                }

                // Region Filters Row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "WHITE_ORCHARD", "VELEN", "NOVIGRAD", "HEART_OF_STONE", "SKELLIGE", "KAER_MORHEN", "TOUSSAINT").forEach { r ->
                        FilterChipCustom(
                            label = formatLabel(r),
                            isSelected = regionFilter == r,
                            onClick = { viewModel.setRegionFilter(r) }
                        )
                    }
                }

                // Status Filters Row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "FAILED").forEach { status ->
                        FilterChipCustom(
                            label = formatLabel(status),
                            isSelected = statusFilter == status,
                            onClick = { viewModel.setStatusFilter(status) }
                        )
                    }
                }

                // Sorting Row
                Text(
                    text = "📈 Sort By",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sortOptions = listOf(
                        Pair("LEVEL_ASC", "Level ↑"),
                        Pair("LEVEL_DESC", "Level ↓"),
                        Pair("TITLE_ASC", "Name A-Z")
                    )
                    sortOptions.forEach { (sortId, labelText) ->
                        FilterChipCustom(
                            label = labelText,
                            isSelected = sortBy == sortId,
                            onClick = { viewModel.setSortBy(sortId) }
                        )
                    }
                }

                // Sorting Row
                Text(
                    text = "📈 Sort By",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sortOptions = listOf(
                        Pair("LEVEL_ASC", "Level ↑"),
                        Pair("LEVEL_DESC", "Level ↓"),
                        Pair("TITLE_ASC", "Name A-Z")
                    )
                    sortOptions.forEach { (sortId, labelText) ->
                        FilterChipCustom(
                            label = labelText,
                            isSelected = sortBy == sortId,
                            onClick = { viewModel.setSortBy(sortId) }
                        )
                    }
                }

                // Level Range Filters Row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val levelRanges = listOf(
                        Pair("ALL", "All Levels"),
                        Pair("1_5", "🟢 Novice (1-5)"),
                        Pair("6_15", "⚔️ Journeyman (6-15)"),
                        Pair("16_25", "🛡️ Master (16-25)"),
                        Pair("26_UP", "💀 Legendary (26+)")
                    )
                    levelRanges.forEach { (rangeVal, labelText) ->
                        FilterChipCustom(
                            label = labelText,
                            isSelected = levelRangeFilter == rangeVal,
                            onClick = { viewModel.setLevelRangeFilter(rangeVal) }
                        )
                    }
                }

                // Sort & Info Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${quests.size} contracts found",
                        fontSize = 11.sp,
                        color = WitcherMutedText
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sort: ", fontSize = 11.sp, color = WitcherMutedText)
                        val sortLabel = when (sortBy) {
                            "LEVEL_ASC" -> "Level ⬆️"
                            "LEVEL_DESC" -> "Level ⬇️"
                            "TITLE_ASC" -> "Name A-Z"
                            else -> "Level"
                        }
                        Text(
                            text = sortLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherRedPrimary,
                            modifier = Modifier
                                .clickable {
                                    val nextSort = when (sortBy) {
                                        "LEVEL_ASC" -> "LEVEL_DESC"
                                        "LEVEL_DESC" -> "TITLE_ASC"
                                        else -> "LEVEL_ASC"
                                    }
                                    viewModel.setSortBy(nextSort)
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quests List
        if (quests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("📜", fontSize = 48.sp, modifier = Modifier.padding(bottom = 8.dp))
                    if (allQuests.isEmpty()) {
                        Text("Empty Journal.", color = WitcherWhiteText, fontWeight = FontWeight.Bold)
                        Text("Preloading defaults...", color = WitcherMutedText, fontSize = 12.sp)
                    } else {
                        Text("No matching contracts found", color = WitcherWhiteText, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        val searchContext = if (searchQuery.isNotEmpty()) "for \"$searchQuery\"" else ""
                        Text(
                            text = "No quests match your active filters $searchContext on the Path. Try searching other regions or clearing your filters.",
                            color = WitcherMutedText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.setTypeFilter("ALL")
                                viewModel.setRegionFilter("ALL")
                                viewModel.setStatusFilter("ALL")
                                viewModel.setLevelRangeFilter("ALL")
                                viewModel.setSearchQuery("")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WitcherRedPrimary,
                                contentColor = WitcherDarkBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "RESET FILTER PATHS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        } else {
            val groupedQuests = quests.groupBy { it.type }
            val mainQuests = (groupedQuests["MAIN"] ?: emptyList()).sortedByDescending { it.tracked }
            val sideContracts = (
                (groupedQuests["SIDE"] ?: emptyList()) + (groupedQuests["CONTRACT"] ?: emptyList())
            ).sortedByDescending { it.tracked }
            val treasureQuests = (groupedQuests["TREASURE"] ?: emptyList()).sortedByDescending { it.tracked }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberLazyListState()
            ) {
                if (mainQuests.isNotEmpty()) {
                    item { Text("Main Tasks", style = MaterialTheme.typography.titleLarge, color = WitcherAmberGold, modifier = Modifier.padding(vertical = 8.dp)) }
                    items(mainQuests, key = { it.id }) { quest ->
                        QuestListItem(
                            quest = quest,
                            onClick = { viewModel.selectQuest(quest) },
                            onConsultGeralt = { viewModel.askAIAboutQuestDirectly(quest, "CHAT") },
                            onConsultAdvisor = { viewModel.askAIAboutQuestDirectly(quest, "ADVISOR") },
                            onToggleStatus = { viewModel.updateQuestStatus(quest.id, if (it) "COMPLETED" else "IN_PROGRESS") },
                            onToggleTracked = { viewModel.toggleQuestTracked(quest.id) }
                        )
                    }
                }
                if (sideContracts.isNotEmpty()) {
                    item { Text("Side Contracts", style = MaterialTheme.typography.titleLarge, color = WitcherAmberGold, modifier = Modifier.padding(vertical = 8.dp)) }
                    items(sideContracts, key = { it.id }) { quest ->
                        QuestListItem(
                            quest = quest,
                            onClick = { viewModel.selectQuest(quest) },
                            onConsultGeralt = { viewModel.askAIAboutQuestDirectly(quest, "CHAT") },
                            onConsultAdvisor = { viewModel.askAIAboutQuestDirectly(quest, "ADVISOR") },
                            onToggleStatus = { viewModel.updateQuestStatus(quest.id, if (it) "COMPLETED" else "IN_PROGRESS") },
                            onToggleTracked = { viewModel.toggleQuestTracked(quest.id) }
                        )
                    }
                }
                if (treasureQuests.isNotEmpty()) {
                    item { Text("Treasure Hunts", style = MaterialTheme.typography.titleLarge, color = WitcherAmberGold, modifier = Modifier.padding(vertical = 8.dp)) }
                    items(treasureQuests, key = { it.id }) { quest ->
                        QuestListItem(
                            quest = quest,
                            onClick = { viewModel.selectQuest(quest) },
                            onConsultGeralt = { viewModel.askAIAboutQuestDirectly(quest, "CHAT") },
                            onConsultAdvisor = { viewModel.askAIAboutQuestDirectly(quest, "ADVISOR") },
                            onToggleStatus = { viewModel.updateQuestStatus(quest.id, if (it) "COMPLETED" else "IN_PROGRESS") },
                            onToggleTracked = { viewModel.toggleQuestTracked(quest.id) }
                        )
                    }
                }
            }
        }
        } else {
            DecisionTreeExplorer(viewModel = viewModel, quests = quests)
        }
    }
}

@Composable
fun FilterChipCustom(
    label: String,
    isSelected: Boolean,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isSelected) WitcherRedPrimary else WitcherDarkSurfaceVariant,
                shape = RoundedCornerShape(20.dp)
            ),
        color = if (isSelected) WitcherRedPrimary.copy(alpha = 0.25f) else WitcherDarkSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) WitcherAmberGold else WitcherMutedText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                color = if (isSelected) WitcherAmberGold else WitcherWhiteText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuestLevelBadge(
    level: Int,
    modifier: Modifier = Modifier,
    useLargeStyle: Boolean = false
) {
    val containerColor: Color
    val contentColor: Color
    val borderColor: Color
    val difficulty: String
    val icon: String

    when {
        level <= 5 -> {
            containerColor = Color(0xFF132A1C)
            contentColor = Color(0xFF81C784)
            borderColor = Color(0xFF234B32)
            difficulty = "Novice"
            icon = "🟢"
        }
        level <= 15 -> {
            containerColor = Color(0xFF332A15)
            contentColor = Color(0xFFE3C567)
            borderColor = Color(0xFF4E4020)
            difficulty = "Journeyman"
            icon = "⚔️"
        }
        level <= 25 -> {
            containerColor = Color(0xFF382314)
            contentColor = Color(0xFFFFA000)
            borderColor = Color(0xFF5E3D23)
            difficulty = "Master"
            icon = "🛡️"
        }
        else -> {
            containerColor = Color(0xFF3E1A1A)
            contentColor = Color(0xFFE57373)
            borderColor = Color(0xFF642E2E)
            difficulty = "Legendary"
            icon = "💀"
        }
    }

    if (useLargeStyle) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = modifier
        ) {
            Surface(
                color = containerColor,
                contentColor = contentColor,
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = icon,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Lvl $level",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor
                    )
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        color = contentColor.copy(alpha = 0.5f)
                    )
                    Text(
                        text = difficulty.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = contentColor,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    } else {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            border = BorderStroke(1.dp, borderColor),
            shape = RoundedCornerShape(6.dp),
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$icon Lvl $level",
                    color = contentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun QuestListItem(
    quest: Quest,
    onClick: () -> Unit,
    onConsultGeralt: (() -> Unit)? = null,
    onConsultAdvisor: (() -> Unit)? = null,
    onToggleStatus: (Boolean) -> Unit,
    onToggleTracked: () -> Unit = {}
) {
    val isCompleted = quest.status == "COMPLETED"

    // 1. Tactile scale pop animation when quest is marked complete
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "QuestScale"
    )

    // 2. Smooth parchment theme background animation
    val animatedBackground by animateColorAsState(
        targetValue = if (isCompleted) WitcherDarkSurface.copy(alpha = 0.55f) else WitcherDarkSurface,
        animationSpec = tween(durationMillis = 350),
        label = "QuestBackground"
    )

    // 3. Smooth animated border color matching the Witcher success state
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            quest.tracked -> WitcherAmberGold
            quest.status == "NOT_STARTED" -> WitcherBorderColor
            quest.status == "IN_PROGRESS" -> WitcherInProgress.copy(alpha = 0.7f)
            quest.status == "COMPLETED" -> WitcherSuccess.copy(alpha = 0.8f)
            quest.status == "FAILED" -> WitcherFailed.copy(alpha = 0.7f)
            else -> WitcherBorderColor
        },
        animationSpec = tween(durationMillis = 350),
        label = "QuestBorderColor"
    )

    // 4. Subtle opacity fade for completed tasks to emphasize they are archived
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isCompleted) 0.65f else 1.0f,
        animationSpec = tween(durationMillis = 350),
        label = "QuestAlpha"
    )

    // 5. Strike-through progress for quest title (tactile slide)
    val strikeThroughProgress by animateFloatAsState(
        targetValue = if (isCompleted) 1.0f else 0.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "QuestStrikeThrough"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = animatedAlpha
            }
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = animatedBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = quest.status == "COMPLETED",
                onCheckedChange = onToggleStatus,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                // Type + Region Category Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val stampText = when (quest.type) {
                        "MAIN" -> "⚔️ Main"
                        "SIDE" -> "📜 Side"
                        "CONTRACT" -> "🐺 Contract"
                        "TREASURE" -> "💎 Hunt"
                        else -> "Quest"
                    }
                    Text(
                        text = stampText.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherRedPrimary
                    )
                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = WitcherMutedText
                    )
                    Text(
                        text = formatLabel(quest.region),
                        fontSize = 10.sp,
                        color = WitcherMutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (quest.tracked) "📍 Tracked" else "○ Untracked",
                        fontSize = 10.sp,
                        fontWeight = if (quest.tracked) FontWeight.Bold else FontWeight.Normal,
                        color = if (quest.tracked) WitcherAmberGold else WitcherMutedText
                    )
                    Text(
                        text = if (quest.tracked) "Untrack" else "Track",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold,
                        modifier = Modifier.clickable { onToggleTracked() }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    val color = when(quest.priority) {
                        "HIGH" -> Color.Red
                        "MEDIUM" -> Color(0xFFFFC107)
                        "LOW" -> Color.Green
                        else -> Color.Gray
                    }
                    Surface(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, color)
                    ) {
                        Text(
                            text = quest.priority.take(1),
                            color = color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = quest.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCompleted) WitcherMutedText else WitcherWhiteText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.drawWithContent {
                        drawContent()
                        if (strikeThroughProgress > 0f) {
                            val y = size.height / 2f
                            drawLine(
                                color = WitcherRedPrimary,
                                start = Offset(0f, y),
                                end = Offset(size.width * strikeThroughProgress, y),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Questgiver / Description snipped
                Text(
                    text = quest.description,
                    fontSize = 12.sp,
                    color = WitcherMutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                var isExpanded by remember { mutableStateOf(false) }
                val tree = remember(quest.title) { com.example.data.QuestDecisionTree.getTreeForQuest(quest.title) }

                // Expandable Decision Tree Trigger
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .border(
                            width = 1.dp,
                            color = if (isExpanded) WitcherAmberGold else WitcherBorderColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    color = if (isExpanded) WitcherDarkSurfaceVariant else WitcherDarkBackground,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⚖️", fontSize = 11.sp)
                        Text(
                            text = if (isExpanded) "Collapse Destiny Tree" else "Expand Destiny Tree",
                            color = if (isExpanded) WitcherAmberGold else WitcherWhiteText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = if (isExpanded) WitcherAmberGold else WitcherMutedText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // If expanded, show the Decision Tree layout
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WitcherDarkBackground, RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, WitcherBorderColor), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        if (tree != null) {
                            Text(
                                text = "DILEMMA: ${tree.dilemmaTitle}",
                                color = WitcherWhiteText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tree.description,
                                color = WitcherMutedText,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Divider(color = WitcherBorderColor, modifier = Modifier.padding(vertical = 6.dp))

                            // Let's display Path A and Path B
                            tree.paths.forEach { path ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                        .background(WitcherDarkSurface, RoundedCornerShape(6.dp))
                                        .border(1.dp, WitcherBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = path.choiceName,
                                        color = WitcherAmberGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = path.immediateNode.description,
                                        color = WitcherMutedText,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "⚖️ Custom Destiny Pathway",
                                color = WitcherWhiteText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "There is no pre-established fate for this contract on the Path. Open Quest Details to simulate a custom moral decision.",
                                color = WitcherMutedText,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 13.sp
                            )
                        }

                        // Displays preloaded customized Advisor/Geralt advice if available
                        if (quest.geraltAdvice != null || quest.advisorAdvice != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = WitcherBorderColor, modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "🔮 PRE-ALCHEMICAL ADVICE",
                                color = WitcherRedPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            if (quest.geraltAdvice != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(WitcherDarkSurface, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("🐺 GERALT'S COUNSEL:", color = WitcherAmberGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(quest.geraltAdvice, color = WitcherWhiteText, fontSize = 10.sp, lineHeight = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            if (quest.advisorAdvice != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(WitcherDarkSurface, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("🧪 ADVISOR'S HIGHLIGHT:", color = WitcherRedPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(quest.advisorAdvice, color = WitcherWhiteText, fontSize = 10.sp, lineHeight = 13.sp)
                                }
                            }
                        } else if (onConsultGeralt != null || onConsultAdvisor != null) {
                            // If no preloaded advice, we show the standard prompt triggers
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = WitcherBorderColor, modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "🔮 WITCHER ADVICE CHANNELS",
                                color = WitcherAmberGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (onConsultGeralt != null) {
                                    Surface(
                                        modifier = Modifier
                                            .clickable { onConsultGeralt() }
                                            .border(1.dp, WitcherAmberGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                                        color = WitcherDarkSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("💬", fontSize = 10.sp)
                                            Text("Geralt Advice", color = WitcherAmberGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                                if (onConsultAdvisor != null) {
                                    Surface(
                                        modifier = Modifier
                                            .clickable { onConsultAdvisor() }
                                            .border(1.dp, WitcherRedPrimary.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                                        color = WitcherDarkSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("🧪", fontSize = 10.sp)
                                            Text("Advisor Advice", color = WitcherRedPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right column: Recommended Level & Status indicator
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Recommended Level badge
                QuestLevelBadge(level = quest.recommendedLevel)

                Spacer(modifier = Modifier.height(8.dp))

                // Status pill
                val (statusColor, statusLabel) = when (quest.status) {
                    "NOT_STARTED" -> Pair(WitcherMutedText, "Not Started")
                    "IN_PROGRESS" -> Pair(WitcherInProgress, "In Progress")
                    "COMPLETED" -> Pair(WitcherSuccess, "Completed")
                    "FAILED" -> Pair(WitcherFailed, "Failed")
                    else -> Pair(WitcherMutedText, "Not Started")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(statusColor, CircleShape)
                    )
                    Text(
                        text = statusLabel,
                        fontSize = 11.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// BESTIARY TAB
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun BestiaryTab(viewModel: QuestViewModel) {
    val search by viewModel.bestiarySearch.collectAsStateWithLifecycle()
    val category by viewModel.bestiaryCategory.collectAsStateWithLifecycle()
    val monsters by viewModel.monsters.collectAsStateWithLifecycle()

    val alchemySearch by viewModel.alchemySearch.collectAsStateWithLifecycle()
    val alchemyCategory by viewModel.alchemyCategory.collectAsStateWithLifecycle()
    val alchemyRecipes by viewModel.alchemyRecipes.collectAsStateWithLifecycle()
    val saddlebagItems by viewModel.saddlebagItems.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("MONSTERS") } // "MONSTERS", "ALCHEMY"

    val context: Any? = null
    var tts by remember { mutableStateOf<DesktopTts?>(null) }
    var speakingMonsterName by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val ttsInstance = DesktopTts(context) { status -> }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun speakMonsterEntry(monster: com.example.data.Monster) {
        tts?.let { speech ->
            if (speakingMonsterName == monster.name) {
                speech.stop()
                speakingMonsterName = null
            } else {
                speech.stop()
                speech.setPitch(0.55f) // deep growl
                speech.setSpeechRate(0.72f) // weathered, ancient speed
                try {
                    speech.setLanguage(java.util.Locale.US)
                } catch (e: Exception) {}

                speech.setOnUtteranceProgressListener(object : com.example.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        speakingMonsterName = monster.name
                    }
                    override fun onDone(utteranceId: String?) {
                        speakingMonsterName = null
                    }
                    override fun onError(utteranceId: String?) {
                        speakingMonsterName = null
                    }
                })

                val weaknessesStr = monster.weaknesses.joinToString(", ")
                val textToSpeak = "${monster.name}. Classified as ${monster.category}. Vulnerabilities: $weaknessesStr. Description: ${monster.description}. Tactical Guide: ${monster.combatGuide}"
                val params = ttsParams().apply {
                    putString(DesktopTts.Engine.KEY_PARAM_UTTERANCE_ID, monster.name)
                }
                speech.speak(textToSpeak, DesktopTts.QUEUE_FLUSH, params, monster.name)
                speakingMonsterName = monster.name
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Sub-Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(WitcherDarkSurface, RoundedCornerShape(8.dp))
                .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val subTabs = listOf(
                Pair("MONSTERS", "🐺 Beasts & Monsters"),
                Pair("ALCHEMY", "🧪 Alchemy Recipes")
            )
            subTabs.forEach { (tabId, label) ->
                val isSelected = activeSubTab == tabId
                Surface(
                    color = if (isSelected) WitcherRedPrimary else Color.Transparent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { activeSubTab = tabId }
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) WitcherDarkBackground else WitcherWhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        if (activeSubTab == "MONSTERS") {
            Text(
                text = "Witcher Bestiary".uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp
                ),
                color = WitcherRedPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Consult vulnerabilities, potion remedies, and combat formulas before engaging target monsters.",
                style = MaterialTheme.typography.bodySmall,
                color = WitcherMutedText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Monster Search
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.setBestiarySearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                placeholder = { Text("Search monsters...", color = WitcherMutedText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WitcherMutedText) },
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

            // Category Filter for Monsters
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val monsterCategories = listOf("ALL", "HUMANOID", "NECROPHAGE", "SPECTER", "HYBRID", "DRACONID", "VAMPIRE", "CURSED")
                monsterCategories.forEach { cat ->
                    val isCatSelected = category == cat
                    Surface(
                        color = if (isCatSelected) WitcherRedPrimary else WitcherDarkSurface,
                        border = BorderStroke(1.dp, if (isCatSelected) WitcherAmberGold else WitcherBorderColor),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.setBestiaryCategory(cat) }
                    ) {
                        Text(
                            text = if (cat == "ALL") "All Categories" else cat,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCatSelected) WitcherDarkBackground else WitcherWhiteText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Monsters list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(monsters) { monster ->
                    MonsterCardItem(
                        monster = monster,
                        isSpeaking = speakingMonsterName == monster.name,
                        onSpeakClick = { speakMonsterEntry(monster) },
                        onAskVesemirClick = { viewModel.askVesemirAboutMonster(monster.name) }
                    )
                }
            }
        } else {
            // ALCHEMY TAB ACTIVE
            Text(
                text = "Alchemy Formulas".uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp
                ),
                color = WitcherAmberGold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Synthesize deadly oils, restorative potions, and toxicity-boosting decoctions using material assets.",
                style = MaterialTheme.typography.bodySmall,
                color = WitcherMutedText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Alchemy Formula Search
            OutlinedTextField(
                value = alchemySearch,
                onValueChange = { viewModel.setAlchemySearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                placeholder = { Text("Search potions, oils, or effects...", color = WitcherMutedText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WitcherMutedText) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = WitcherDarkSurface,
                    unfocusedContainerColor = WitcherDarkSurface,
                    focusedBorderColor = WitcherAmberGold,
                    unfocusedBorderColor = WitcherDarkSurfaceVariant,
                    focusedTextColor = WitcherWhiteText,
                    unfocusedTextColor = WitcherWhiteText
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Filter for Alchemy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val alchemyCategories = listOf(
                    Pair("ALL", "🔮 All Recipes"),
                    Pair("POTION", "🧪 Potions"),
                    Pair("OIL", "🗡️ Oils")
                )
                alchemyCategories.forEach { (catId, label) ->
                    val isCatSelected = alchemyCategory == catId
                    Surface(
                        color = if (isCatSelected) WitcherAmberGold else WitcherDarkSurface,
                        border = BorderStroke(1.dp, if (isCatSelected) WitcherAmberGold else WitcherBorderColor),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.setAlchemyCategory(catId) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCatSelected) WitcherDarkBackground else WitcherWhiteText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Recipes list
            if (alchemyRecipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No alchemy formulas match search.",
                        color = WitcherMutedText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(alchemyRecipes) { recipe ->
                        AlchemyRecipeCardItem(
                            recipe = recipe,
                            saddlebagItems = saddlebagItems,
                            onCraftClick = { viewModel.craftRecipe(recipe) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MonsterCardItem(
    monster: com.example.data.Monster,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    onAskVesemirClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = WitcherBorderColor, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Name and Category badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = monster.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WitcherWhiteText
                    )
                    IconButton(
                        onClick = onSpeakClick,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text(
                            text = if (isSpeaking) "🔇" else "🔊",
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    color = WitcherRedPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, WitcherRedPrimary)
                ) {
                    Text(
                        text = monster.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherRedSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Lore Description
            Text(
                text = "\"${monster.description}\"",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = WitcherMutedText,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Dynamic Illustrations for special beasts
            val beastImageRes = when (monster.name) {
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
                else -> null
            }
            if (beastImageRes != null) {
                Image(
                    painter = painterResource(beastImageRes),
                    contentDescription = monster.name + " portrait sketch",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Weakness tags
            Text(
                text = "⚔️ Vulnerabilities".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherAmberGold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                monster.weaknesses.forEach { weakness ->
                    Surface(
                        color = WitcherDarkSurfaceVariant,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, WitcherBorderColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                com.example.ui.WitcherSoundPlayer.playVulnerabilityClickSound()
                            }
                    ) {
                        Text(
                            text = "⚡ $weakness",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Prep Combat strategy guide
            Text(
                text = "🛡️ Tactician's Council".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherWhiteText,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = monster.combatGuide,
                fontSize = 12.sp,
                color = WitcherWhiteText.copy(alpha = 0.85f),
                lineHeight = 16.sp
            )

            // Consult Vesemir Button
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAskVesemirClick,
                colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp) // Touch target minimum 48dp
            ) {
                Text(
                    text = "👴 Consult Elder Vesemir".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherWhiteText,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AlchemyRecipeCardItem(
    recipe: com.example.data.AlchemyRecipe,
    saddlebagItems: List<com.example.data.SaddlebagItem>,
    onCraftClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = WitcherBorderColor.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Icon + Name, and Category badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, WitcherAmberGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = recipe.icon, fontSize = 20.sp)
                    }
                    Text(
                        text = recipe.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WitcherWhiteText
                    )
                }

                Surface(
                    color = WitcherRedPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, WitcherRedPrimary)
                ) {
                    Text(
                        text = recipe.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherRedSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lore Description
            Text(
                text = recipe.description,
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = WitcherMutedText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Combat effect
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WitcherDarkSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(8.dp)
                    .border(1.dp, WitcherBorderColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            ) {
                Text("🗡️", fontSize = 14.sp)
                Column {
                    Text(
                        text = "COMBAT BENEFIT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold
                    )
                    Text(
                        text = recipe.combatEffect,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherWhiteText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Required list
            Text(
                text = "🧪 Requisite Ingredients".uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherMutedText,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recipe.formula.forEach { req ->
                    val matchingBag = saddlebagItems.find { 
                        it.name.equals(req.name, ignoreCase = true) && it.category == "ALCHEMY_INGREDIENT" 
                    }
                    val ownedQty = matchingBag?.quantity ?: 0
                    val hasEnough = ownedQty >= req.quantity

                    val (chipColor, strokeColor) = if (hasEnough) {
                        Pair(WitcherSuccess.copy(alpha = 0.15f), WitcherSuccess.copy(alpha = 0.6f))
                    } else {
                        Pair(WitcherFailed.copy(alpha = 0.12f), WitcherFailed.copy(alpha = 0.5f))
                    }

                    Surface(
                        color = chipColor,
                        border = BorderStroke(1.dp, strokeColor),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = req.icon, fontSize = 11.sp)
                            Text(
                                text = "${req.name} (${ownedQty}/${req.quantity})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasEnough) WitcherWhiteText else WitcherMutedText
                            )
                            if (hasEnough) {
                                Text("✓", color = WitcherSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("✗", color = WitcherFailed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            val canCraft = recipe.formula.all { req ->
                val owned = saddlebagItems.find { 
                    it.name.equals(req.name, ignoreCase = true) && it.category == "ALCHEMY_INGREDIENT" 
                }?.quantity ?: 0
                owned >= req.quantity
            }

            Button(
                onClick = onCraftClick,
                enabled = canCraft,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WitcherRedPrimary,
                    disabledContainerColor = WitcherDarkSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp) // Minimum Touch Target Size
            ) {
                Text(
                    text = if (canCraft) "🧪 Brew Formula".uppercase() else "⚠️ Missing Requisites".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canCraft) WitcherWhiteText else WitcherMutedText,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// GERALT CHAT TAB
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatTab(viewModel: QuestViewModel) {
    val messages by viewModel.geraltMessages.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputMessageText by remember { mutableStateOf("") }
    val keyboardController: Any? = null
    val focusManager = LocalFocusManager.current

    val context: Any? = null
    var tts by remember { mutableStateOf<com.example.DesktopTts?>(null) }
    var speakingMessageId by remember { mutableStateOf<String?>(null) }
    var lastAutoSpokenId by remember { mutableStateOf<String?>(null) }

    val speechRecognizerLauncher = DesktopSpeechLauncher

    DisposableEffect(context) {
        val ttsInstance = com.example.DesktopTts(context) { status -> }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun speakGeraltText(msgId: String, text: String) {
        tts?.let { speech ->
            if (speakingMessageId == msgId) {
                speech.stop()
                speakingMessageId = null
            } else {
                speech.stop()
                speech.setPitch(0.55f) // deep Witcher voice growl
                speech.setSpeechRate(0.78f) // weathered, measured voice pacing
                try {
                    speech.setLanguage(java.util.Locale.US)
                } catch (e: Exception) {}

                speech.setOnUtteranceProgressListener(object : com.example.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        speakingMessageId = msgId
                    }
                    override fun onDone(utteranceId: String?) {
                        speakingMessageId = null
                    }
                    override fun onError(utteranceId: String?) {
                        speakingMessageId = null
                    }
                })

                val cleanText = text
                    .replace("**", "")
                    .replace("*", "")
                    .replace("#", "")
                    .replace("- ", " ")
                    .replace("`", "")

                val params = ttsParams().apply {
                    putString(com.example.DesktopTts.Engine.KEY_PARAM_UTTERANCE_ID, msgId)
                }
                speech.speak(cleanText, com.example.DesktopTts.QUEUE_FLUSH, params, msgId)
                speakingMessageId = msgId
            }
        }
    }

    val configuration = rememberDesktopConfiguration()
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = configuration.screenWidthDp < 360

    val responsivePadding = if (isSmallScreen) 10.dp else 16.dp
    val maxBubbleWidth = screenWidth * 0.76f
    val headerTitleSize = if (isSmallScreen) 13.sp else 15.sp
    val bodyTextSize = if (isSmallScreen) 12.sp else 13.sp
    val avatarSize = if (isSmallScreen) 38.dp else 48.dp

    // Scroll to bottom when a new message is appended
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Auto-speak each new finished (non-user, non-pending) reply exactly once.
    // Keyed on stable scalars (last message identity + pending state) so it only
    // fires when the last message actually changes, not on every list emission.
    // The speak call is wrapped so a TTS failure can never kill recomposition.
    LaunchedEffect(messages.lastOrNull()?.timestamp, messages.lastOrNull()?.isPending) {
        val last = messages.lastOrNull()
        if (last != null && last.sender != "USER" && !last.isPending) {
            val id = last.timestamp.toString()
            if (id != lastAutoSpokenId) {
                lastAutoSpokenId = id
                try {
                    speakGeraltText(id, last.text)
                } catch (_: Throwable) {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(responsivePadding)
    ) {
        // Geralt Counsel Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WitcherDarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, WitcherAmberGold, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🕯️", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
            Column {
                Text(
                    text = "Geralt's Counsel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold
                )
                Text(
                    text = "The White Wolf's wisdom",
                    fontSize = 12.sp,
                    color = WitcherMutedText
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Chat Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WitcherDarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, WitcherAmberGold, RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputMessageText,
                onValueChange = { inputMessageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Ask for counsel, Witcher...", color = WitcherMutedText, fontSize = 12.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = WitcherWhiteText,
                    unfocusedTextColor = WitcherWhiteText
                ),
                textStyle = TextStyle(fontSize = 13.sp)
            )
            IconButton(
                onClick = {
                    viewModel.sendGeraltMessage(inputMessageText)
                    inputMessageText = ""
                    /* keyboard hide no-op on desktop */
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(WitcherRedPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = WitcherWhiteText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // Chat message history list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, WitcherBorderColor, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .witcherParchmentTexture()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "USER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) WitcherRedPrimary.copy(alpha = 0.15f) else WitcherDarkSurfaceVariant
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isUser) WitcherRedPrimary else WitcherBorderColor
                        ),
                        modifier = Modifier
                            .widthIn(max = maxBubbleWidth)
                            .padding(horizontal = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(if (isSmallScreen) 8.dp else 10.dp)) {
                            // Chat Label
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isUser) "You (Witcher)" else "Geralt",
                                    fontSize = if (isSmallScreen) 10.sp else 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) WitcherAmberGold else WitcherRedPrimary
                                )
                                if (!isUser) {
                                    Text(
                                        text = "🐺",
                                        fontSize = if (isSmallScreen) 9.sp else 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Message Text
                            if (msg.isPending) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        color = WitcherAmberGold,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = bodyTextSize,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = WitcherMutedText,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            } else {
                                Text(
                                    text = msg.text,
                                    fontSize = bodyTextSize,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WitcherWhiteText,
                                    lineHeight = if (isSmallScreen) 16.sp else 18.sp
                                )
                                if (!isUser) {
                                    TextButton(
                                        onClick = { try { speakGeraltText(msg.timestamp.toString(), msg.text) } catch (_: Throwable) {} },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (speakingMessageId == msg.timestamp.toString()) "⏹ Stop" else "🔊 Play",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Input Controller Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = inputMessageText,
                onValueChange = { inputMessageText = it },
                placeholder = {
                    Text(
                        text = "Ask Geralt for guide on the Path...",
                        fontSize = if (isSmallScreen) 11.sp else 12.sp,
                        color = WitcherMutedText
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, WitcherBorderColor, RoundedCornerShape(12.dp))
                    .testTag("geralt_chat_input"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = WitcherWhiteText,
                    unfocusedTextColor = WitcherWhiteText,
                    focusedContainerColor = WitcherDarkSurface,
                    unfocusedContainerColor = WitcherDarkSurface,
                    disabledContainerColor = WitcherDarkSurface,
                    cursorColor = WitcherAmberGold,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                maxLines = 3,
                textStyle = TextStyle(fontSize = bodyTextSize)
            )

            // Voice Input (Speech-to-Text) Button
            IconButton(
                onClick = {
                    try {
                        val intent = null
                        speechRecognizerLauncher.launch(intent)
                    } catch (e: Exception) {
                        println("Voice dictation not available on desktop.")
                    }
                },
                modifier = Modifier
                    .size(if (isSmallScreen) 44.dp else 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WitcherDarkSurface)
                    .border(1.dp, WitcherBorderColor, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "🎤",
                    fontSize = if (isSmallScreen) 16.sp else 18.sp
                )
            }

            // Submit Action button
            IconButton(
                onClick = {
                    if (inputMessageText.trim().isNotEmpty()) {
                        viewModel.sendGeraltMessage(inputMessageText)
                        inputMessageText = ""
                        /* keyboard hide no-op on desktop */
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .size(if (isSmallScreen) 44.dp else 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WitcherAmberGold)
                    .border(1.dp, WitcherBorderColor, RoundedCornerShape(12.dp))
                    .testTag("geralt_chat_send_button"),
                enabled = inputMessageText.trim().isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send scroll message to Geralt",
                    tint = WitcherDarkBackground,
                    modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                )
            }
        }
    }
}

// ADVISOR CHAT TAB (Vesemir's Council)
@Composable
fun AdvisorTab(viewModel: QuestViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val selectedAdvisor by viewModel.selectedAdvisor.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputMessageText by remember { mutableStateOf("") }
    val keyboardController: Any? = null
    val focusManager = LocalFocusManager.current

    var selectedSubTab by remember { mutableStateOf("CHAT") }

    val context: Any? = null
    var tts by remember { mutableStateOf<com.example.DesktopTts?>(null) }
    var speakingMessageId by remember { mutableStateOf<String?>(null) }
    var lastAutoSpokenId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val ttsInstance = com.example.DesktopTts(context) { status -> }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun speakAdvisorText(msgId: String, text: String) {
        tts?.let { speech ->
            if (speakingMessageId == msgId) {
                speech.stop()
                speakingMessageId = null
            } else {
                speech.stop()
                speech.setPitch(0.55f)
                speech.setSpeechRate(0.78f)
                try {
                    speech.setLanguage(java.util.Locale.US)
                } catch (e: Exception) {}

                speech.setOnUtteranceProgressListener(object : com.example.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        speakingMessageId = msgId
                    }
                    override fun onDone(utteranceId: String?) {
                        speakingMessageId = null
                    }
                    override fun onError(utteranceId: String?) {
                        speakingMessageId = null
                    }
                })

                val cleanText = text
                    .replace("**", "")
                    .replace("*", "")
                    .replace("#", "")
                    .replace("- ", " ")
                    .replace("`", "")

                val params = ttsParams().apply {
                    putString(com.example.DesktopTts.Engine.KEY_PARAM_UTTERANCE_ID, msgId)
                }
                speech.speak(cleanText, com.example.DesktopTts.QUEUE_FLUSH, params, msgId)
                speakingMessageId = msgId
            }
        }
    }

    val configuration = rememberDesktopConfiguration()
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = configuration.screenWidthDp < 360

    val responsivePadding = if (isSmallScreen) 10.dp else 16.dp
    val maxBubbleWidth = screenWidth * 0.76f
    val headerTitleSize = if (isSmallScreen) 18.sp else 22.sp
    val bodyTextSize = if (isSmallScreen) 13.sp else 14.sp
    val avatarSize = if (isSmallScreen) 38.dp else 46.dp

    // Scroll to bottom when a new message is appended
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Auto-speak each new finished (non-user, non-pending) reply exactly once.
    // Keyed on stable scalars (last message identity + pending state) so it only
    // fires when the last message actually changes, not on every list emission.
    // The speak call is wrapped so a TTS failure can never kill recomposition.
    LaunchedEffect(messages.lastOrNull()?.timestamp, messages.lastOrNull()?.isPending) {
        val last = messages.lastOrNull()
        if (last != null && last.sender != "USER" && !last.isPending) {
            val id = last.timestamp.toString()
            if (id != lastAutoSpokenId) {
                lastAutoSpokenId = id
                try {
                    speakAdvisorText(id, last.text)
                } catch (_: Throwable) {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(responsivePadding)
    ) {
        // Chat room header with clear action and dynamic avatar portrait
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 12.dp),
                modifier = Modifier.weight(1f)
            ) {
                val avatarRes = when (selectedAdvisor) {
                    "Yennefer" -> Res.drawable.img_gwent_yennefer
                    "Jaskier" -> Res.drawable.img_gwent_jaskier
                    "Vesemir" -> Res.drawable.img_gwent_vesemir
                    else -> Res.drawable.img_gwent_geralt
                }
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(1.5.dp, WitcherAmberGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(avatarRes),
                        contentDescription = "$selectedAdvisor Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column {
                    Text(
                        text = selectedAdvisor.uppercase(),
                        fontSize = if (isSmallScreen) 16.sp else 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.2.sp,
                        color = WitcherRedPrimary
                    )
                }
            }

            if (selectedSubTab == "CHAT") {
                IconButton(
                    onClick = { viewModel.clearChatHistory() },
                    modifier = Modifier
                        .size(if (isSmallScreen) 34.dp else 40.dp)
                        .clip(CircleShape)
                        .background(WitcherDarkSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Chat history",
                        tint = WitcherWhiteText,
                        modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Sub Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .background(WitcherDarkSurface, RoundedCornerShape(8.dp))
                .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Pair("CHAT", "📜 Council Chat"),
                Pair("BRANCHER", "⚖️ Scenario Brancher")
            )
            tabs.forEach { (tabId, label) ->
                val isSelected = selectedSubTab == tabId
                Surface(
                    color = if (isSelected) WitcherRedPrimary else Color.Transparent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { selectedSubTab = tabId }
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) WitcherDarkBackground else WitcherWhiteText,
                        fontSize = if (isSmallScreen) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedSubTab == "CHAT") {
            // Advisor selector row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val advisors = listOf(
                    Triple("Vesemir", "👴 Vesemir", "Old Master"),
                    Triple("Yennefer", "🔮 Yennefer", "Sorceress"),
                    Triple("Jaskier", "🪕 Jaskier", "Glam Bard")
                )
                
                advisors.forEach { (id, label, subtitle) ->
                    val isSelected = selectedAdvisor == id
                    Surface(
                        color = if (isSelected) WitcherRedPrimary.copy(alpha = 0.15f) else WitcherDarkSurface,
                        border = BorderStroke(1.dp, if (isSelected) WitcherAmberGold else WitcherBorderColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setAdvisor(id) }
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 4.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = label.substringAfter(" "),
                                fontSize = if (isSmallScreen) 9.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitcherWhiteText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chat message history list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .witcherParchmentTexture()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isUser = msg.sender == "USER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isUser) 12.dp else 2.dp,
                                bottomEnd = if (isUser) 2.dp else 12.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) WitcherRedPrimary.copy(alpha = 0.25f) else WitcherDarkSurfaceVariant
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isUser) WitcherRedPrimary else WitcherDarkSurfaceVariant
                            ),
                            modifier = Modifier
                                .widthIn(max = maxBubbleWidth)
                                .padding(horizontal = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(if (isSmallScreen) 8.dp else 10.dp)) {
                                // Chat Label
                                Text(
                                    text = if (isUser) "Geralt (Path)" else selectedAdvisor,
                                    fontSize = if (isSmallScreen) 10.sp else 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) WitcherAmberGold else WitcherRedPrimary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                
                                // Message Text
                                if (msg.isPending) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            color = WitcherAmberGold,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = msg.text,
                                            fontSize = bodyTextSize,
                                            color = WitcherMutedText
                                        )
                                    }
                                } else {
                                    Text(
                                        text = msg.text,
                                        fontSize = bodyTextSize,
                                        color = WitcherWhiteText,
                                        lineHeight = if (isSmallScreen) 16.sp else 18.sp
                                    )
                                    if (!isUser) {
                                        TextButton(
                                            onClick = { try { speakAdvisorText(msg.timestamp.toString(), msg.text) } catch (_: Throwable) {} },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (speakingMessageId == msg.timestamp.toString()) "⏹ Stop" else "🔊 Play",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Send Message controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputMessageText,
                    onValueChange = { inputMessageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text(
                            text = "Consult $selectedAdvisor...", 
                            fontSize = if (isSmallScreen) 11.sp else 12.sp,
                            color = WitcherMutedText
                        ) 
                    },
                    singleLine = false,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputMessageText.trim().isNotEmpty()) {
                            viewModel.sendAdvisorMessage(inputMessageText)
                            inputMessageText = ""
                            /* keyboard hide no-op on desktop */
                            focusManager.clearFocus()
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WitcherDarkSurface,
                        unfocusedContainerColor = WitcherDarkSurface,
                        focusedBorderColor = WitcherRedPrimary,
                        unfocusedBorderColor = WitcherDarkSurfaceVariant,
                        focusedTextColor = WitcherWhiteText,
                        unfocusedTextColor = WitcherWhiteText
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = bodyTextSize)
                )

                IconButton(
                    onClick = {
                        if (inputMessageText.trim().isNotEmpty()) {
                            viewModel.sendAdvisorMessage(inputMessageText)
                            inputMessageText = ""
                            /* keyboard hide no-op on desktop */
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .size(if (isSmallScreen) 44.dp else 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WitcherRedPrimary)
                        .border(1.dp, WitcherBorderColor, RoundedCornerShape(12.dp)),
                    enabled = inputMessageText.trim().isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send advice query",
                        tint = WitcherDarkBackground,
                        modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                    )
                }
            }
        } else {
            // BRANCH SCRITERION GENERATOR
            var scenarioTextState by remember { mutableStateOf("") }
            val branchResult by viewModel.branchResult.collectAsStateWithLifecycle()
            val isGeneratingBranches by viewModel.isGeneratingBranches.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
                    border = BorderStroke(1.dp, WitcherDarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚖️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Trial of Choices",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitcherAmberGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Evil is evil, lesser, greater, middling, makes no difference. Lay out your dilemma or custom quest scenario below, and we shall consult the wisdom of the Path to weigh the tragic branches of fate.",
                            fontSize = 12.sp,
                            color = WitcherWhiteText.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "💡 Lore Dilemmas (Tap to load)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        Triple("Whispering Hillock", "Save the Ancient oak spirit to rescue the Swamp orphans, or kill it to protect the nearby village of Downwarren.", "🌲"),
                        Triple("The Striga's Crypt", "Hold off the guards and spend the night in the crypt to lift Princess Adda's striga curse, or slay her to save lives.", "🐺"),
                        Triple("The Novigrad Doppler", "A doppler is caught stealing cargo to feed starving elven refugees. Hand him to the witch hunters, or shield him.", "🎭")
                    )

                    presets.forEach { (title, description, icon) ->
                        Surface(
                            color = WitcherDarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, WitcherDarkSurfaceVariant),
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { scenarioTextState = description }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WitcherRedPrimary,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = description,
                                    fontSize = 11.sp,
                                    color = WitcherMutedText,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "📜 Quest Scenario",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = scenarioTextState,
                    onValueChange = { scenarioTextState = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter a custom quest scenario or moral conflict...", color = WitcherMutedText) },
                    minLines = 3,
                    maxLines = 6,
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

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scenarioTextState = ""
                            viewModel.clearBranchResult()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WitcherWhiteText),
                        border = BorderStroke(1.dp, WitcherDarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Clear", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (scenarioTextState.trim().isNotEmpty()) {
                                viewModel.generateScenarioBranches(scenarioTextState)
                            }
                        },
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary, contentColor = WitcherDarkBackground),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isGeneratingBranches && scenarioTextState.trim().isNotEmpty()
                    ) {
                        if (isGeneratingBranches) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = WitcherDarkBackground,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Contemplating...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Weigh Consequences", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (branchResult != null || isGeneratingBranches) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "⚖️ Resulting Branches",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, WitcherAmberGold, RoundedCornerShape(12.dp)),
                        color = WitcherDarkSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (isGeneratingBranches) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = WitcherRedPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Vesemir is consulting the scrolls...",
                                        fontSize = 13.sp,
                                        color = WitcherMutedText
                                    )
                                }
                            } else {
                                Text(
                                    text = branchResult ?: "",
                                    fontSize = 13.sp,
                                    color = WitcherWhiteText,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun WitcherSchoolBuildCard(
    school: String,
    onApplyClick: () -> Unit
) {
    val buildDetails = when (school) {
        "Cat" -> Triple(
            "🐱 Feline (Light Armor) Build",
            "Targets critical strike damage, heavy fast attack velocity, and perfect dodge mastery. Highly mobile, quick killing flow.",
            listOf(
                "• Cat School Techniques (General - Lvl 1/1 max)",
                "• Muscle Memory (Combat - Lvl 3/3 max)",
                "• Fleet Footed (Combat - Lvl 3/3 max)",
                "• Whirl (Combat - Lvl 3/3 max)"
            )
        )
        "Griffin" -> Triple(
            "🦅 Griffin (Medium Armor) Build",
            "Targets massive Sign intensity, instant stamina recovery, and magical sign spellcasting options. Ultimate Battle-Mage flow.",
            listOf(
                "• Griffin School Techniques (General - Lvl 1/1 max)",
                "• Melt Armor (Signs - Lvl 3/3 max)",
                "• Firestream (Signs - Lvl 3/3 max)",
                "• Active Shield (Signs - Lvl 3/3 max)"
            )
        )
        "Bear" -> Triple(
            "🐻 Ursine (Heavy Armor) Build",
            "Targets ultimate defense tanks, explosive heavy attacks, and superior safety margins. Unstoppable heavy juggernaut strength.",
            listOf(
                "• Bear School Techniques (General - Lvl 1/1 max)",
                "• Strength Training (Combat - Lvl 3/3 max)",
                "• Rend (Combat - Lvl 3/3 max)",
                "• Heightened Tolerance (Alchemy - Lvl 3/3 max)"
            )
        )
        "Wolf" -> Triple(
            "🐺 Wolven (Medium Armor) Build",
            "A flexible jack-of-all-trades config utilizing light signs, balanced potion buffers, and fast sword attacks.",
            listOf(
                "• Griffin School Techniques (General - Lvl 1/1 max)",
                "• Muscle Memory (Combat - Lvl 3/3 max)",
                "• Melt Armor (Signs - Lvl 3/3 max)",
                "• Refreshment (Alchemy - Lvl 3/3 max)"
            )
        )
        "Viper" -> Triple(
            "🐍 Viper (Medium Armor) Build",
            "Focuses on poisonous silver strikes, high agility, and chemical toxicity threshold management. Quick assassination tactics.",
            listOf(
                "• Cat School Techniques (General - Lvl 1/1 max)",
                "• Poisoned Blades (Alchemy - Lvl 3/3 max)",
                "• Heightened Tolerance (Alchemy - Lvl 3/3 max)",
                "• Muscle Memory (Combat - Lvl 3/3 max)"
            )
        )
        "Manticore" -> Triple(
            "🦁 Manticore (Medium Armor) Build",
            "Focuses on maximum toxicity limit, allowing double decoctions, heavy potion uses, and increased critical gains.",
            listOf(
                "• Griffin School Techniques (General - Lvl 1/1 max)",
                "• Heightened Tolerance (Alchemy - Lvl 3/3 max)",
                "• Refreshment (Alchemy - Lvl 3/3 max)",
                "• Acquired Tolerance (Alchemy - Lvl 3/3 max)"
            )
        )
        else -> Triple(
            "⚔️ General Witcher Build",
            "Balanced set of witcher techniques and tactical training.",
            listOf()
        )
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛠️ " + buildDetails.first.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WitcherAmberGold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = WitcherRedPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, WitcherRedPrimary)
                ) {
                    Text(
                        text = "Next-Gen 4.0 Cap",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherRedPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildDetails.second,
                fontSize = 11.sp,
                color = WitcherWhiteText.copy(alpha = 0.85f),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Key Skill Point Distribution:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherMutedText
            )

            Spacer(modifier = Modifier.height(4.dp))
            buildDetails.third.forEach { skill ->
                Text(
                    text = skill,
                    fontSize = 11.sp,
                    color = WitcherAmberGold.copy(alpha = 0.9f),
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onApplyClick,
                colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp) // Touch target minimum 48dp
            ) {
                Text(
                    text = "🔮 Allocate Skills For This Build".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherWhiteText,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// PROFILE TAB COMPONENTS
@Composable
fun ProfileTab(
    viewModel: QuestViewModel,
    quests: List<Quest>
) {
    val level by viewModel.witcherLevel.collectAsStateWithLifecycle()
    val school by viewModel.witcherSchool.collectAsStateWithLifecycle()
    val skills by viewModel.witcherSkills.collectAsStateWithLifecycle()
    
    // Simple custom state for user choice notebook persistence notes
    var personalNotes by remember { mutableStateOf("") }

    val speechRecognizerLauncher = DesktopSpeechLauncher

    // Calculate database accomplishments stats
    val totalCount = quests.size
    val completedCount = quests.count { it.status == "COMPLETED" }
    val inProgressCount = quests.count { it.status == "IN_PROGRESS" }
    val failedCount = quests.count { it.status == "FAILED" }
    val notStartedCount = quests.count { it.status == "NOT_STARTED" }

    val completionPercentage = if (totalCount > 0) {
        (completedCount.toFloat() / totalCount * 100).toInt()
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Geralt's Profile".uppercase(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.5.sp
            ),
            color = WitcherRedPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Manage attributes, track path completion rates, and record outcomes.",
            style = MaterialTheme.typography.bodySmall,
            color = WitcherMutedText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Geralt's Stat Slate Card
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Witcher medallion visual representing level
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = WitcherDarkSurfaceVariant,
                        border = BorderStroke(2.dp, WitcherAmberGold)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🐺", fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Geralt of Rivia (Witcher)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "School of the $school Gear Set active",
                            fontSize = 12.sp,
                            color = WitcherAmberGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Steppers to Increments Level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Level", fontSize = 12.sp, color = WitcherMutedText)
                        Text("LVL $level", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { viewModel.setWitcherLevel(level - 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = WitcherDarkSurfaceVariant),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-", fontSize = 18.sp, color = WitcherWhiteText, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = { viewModel.setWitcherLevel(level + 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = WitcherDarkSurfaceVariant),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+", fontSize = 18.sp, color = WitcherWhiteText, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Steppers to select school
                Text("Select Witcher School", fontSize = 12.sp, color = WitcherMutedText, modifier = Modifier.padding(bottom = 6.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Wolf", "Griffin", "Cat", "Bear", "Viper", "Manticore").forEach { sc ->
                        val isSelected = school == sc
                        Surface(
                            modifier = Modifier.clickable { viewModel.setWitcherSchool(sc) },
                            color = if (isSelected) WitcherRedPrimary else WitcherDarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) WitcherAmberGold else WitcherDarkSurfaceVariant)
                        ) {
                            Text(
                                text = sc,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitcherWhiteText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        WitcherSchoolBuildCard(
            school = school,
            onApplyClick = { viewModel.applyRecommendedBuild(school) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // INTERACTIVE PATH COMPLETION ASTROLABE DASHBOARD
        var selectedCategory by remember { mutableStateOf("MAIN") }

        // Path statistics
        val mainQuests = quests.filter { it.type == "MAIN" }
        val mainTotal = mainQuests.size
        val mainCompleted = mainQuests.count { it.status == "COMPLETED" }
        val mainProgress = if (mainTotal > 0) mainCompleted.toFloat() / mainTotal else 0.0f

        val sideContractQuests = quests.filter { it.type == "SIDE" || it.type == "CONTRACT" }
        val sideTotal = sideContractQuests.size
        val sideCompleted = sideContractQuests.count { it.status == "COMPLETED" }
        val sideProgress = if (sideTotal > 0) sideCompleted.toFloat() / sideTotal else 0.0f

        val treasureQuests = quests.filter { it.type == "TREASURE" }
        val treasureTotal = treasureQuests.size
        val treasureCompleted = treasureQuests.count { it.status == "COMPLETED" }
        val treasureProgress = if (treasureTotal > 0) treasureCompleted.toFloat() / treasureTotal else 0.0f

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏆 Interactive Astrolabe Completion Dashboard",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Central canvas astrolabe chart
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    val next = when (selectedCategory) {
                                        "MAIN" -> "SIDE_CONTRACT"
                                        "SIDE_CONTRACT" -> "TREASURE"
                                        else -> "MAIN"
                                    }
                                    selectedCategory = next
                                }
                        ) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val outerRadius = size.width / 2f - 4.dp.toPx()

                            // Base astrolabe dial plate
                            drawCircle(
                                color = Color(0xFF1B1A1E),
                                radius = outerRadius,
                                center = center,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0f)
                            )
                            
                            // Compass ticks
                            drawLine(
                                color = Color(0xFF2C2A30),
                                start = Offset(center.x, center.y - outerRadius),
                                end = Offset(center.x, center.y + outerRadius),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color(0xFF2C2A30),
                                start = Offset(center.x - outerRadius, center.y),
                                end = Offset(center.x + outerRadius, center.y),
                                strokeWidth = 1.dp.toPx()
                            )

                            val baseThickness = 6.dp.toPx()

                            // Ring 1: Main Path (Outer) - Gold
                            val isMainSelected = selectedCategory == "MAIN"
                            val r1 = outerRadius - 10.dp.toPx()
                            val t1 = if (isMainSelected) baseThickness * 1.5f else baseThickness
                            
                            drawCircle(
                                color = Color(0xFF23201F),
                                radius = r1,
                                center = center,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = baseThickness)
                            )
                            drawArc(
                                color = if (isMainSelected) WitcherAmberGold else WitcherAmberGold.copy(alpha = 0.5f),
                                startAngle = -90f,
                                sweepAngle = mainProgress * 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = t1, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                                size = Size(r1 * 2, r1 * 2),
                                topLeft = Offset(center.x - r1, center.y - r1)
                            )

                            // Ring 2: Side Contracts Path (Middle) - Red
                            val isSideSelected = selectedCategory == "SIDE_CONTRACT"
                            val r2 = outerRadius - 26.dp.toPx()
                            val t2 = if (isSideSelected) baseThickness * 1.5f else baseThickness

                            drawCircle(
                                color = Color(0xFF2B1F1F),
                                radius = r2,
                                center = center,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = baseThickness)
                            )
                            drawArc(
                                color = if (isSideSelected) WitcherRedPrimary else WitcherRedPrimary.copy(alpha = 0.5f),
                                startAngle = -90f,
                                sweepAngle = sideProgress * 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = t2, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                                size = Size(r2 * 2, r2 * 2),
                                topLeft = Offset(center.x - r2, center.y - r2)
                            )

                            // Ring 3: Treasure Hunts Path (Inner) - Silver/Blueish Gray
                            val isTreasureSelected = selectedCategory == "TREASURE"
                            val r3 = outerRadius - 42.dp.toPx()
                            val t3 = if (isTreasureSelected) baseThickness * 1.5f else baseThickness

                            drawCircle(
                                color = Color(0xFF1E1F24),
                                radius = r3,
                                center = center,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = baseThickness)
                            )
                            drawArc(
                                color = if (isTreasureSelected) Color(0xFFB0BEC5) else Color(0xFFB0BEC5).copy(alpha = 0.4f),
                                startAngle = -90f,
                                sweepAngle = treasureProgress * 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = t3, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                                size = Size(r3 * 2, r3 * 2),
                                topLeft = Offset(center.x - r3, center.y - r3)
                            )

                            // Center pin
                            drawCircle(
                                color = if (isMainSelected) WitcherAmberGold else if (isSideSelected) WitcherRedPrimary else Color(0xFFB0BEC5),
                                radius = 4.dp.toPx(),
                                center = center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Dynamic path selection columns
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Main stories Selector
                        Surface(
                            onClick = { selectedCategory = "MAIN" },
                            color = if (selectedCategory == "MAIN") WitcherDarkSurfaceVariant else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (selectedCategory == "MAIN") WitcherAmberGold else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚔️ Main Story", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WitcherAmberGold)
                                    Text("${(mainProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WitcherAmberGold)
                                }
                                LinearProgressIndicator(
                                    progress = mainProgress,
                                    modifier = Modifier.fillMaxWidth().height(4.dp).padding(top = 4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = WitcherAmberGold,
                                    trackColor = WitcherDarkSurfaceVariant
                                )
                                Text("Completed: $mainCompleted / $mainTotal", fontSize = 9.sp, color = WitcherMutedText, modifier = Modifier.padding(top = 2.dp))
                            }
                        }

                        // Side quests & contracts Selector
                        Surface(
                            onClick = { selectedCategory = "SIDE_CONTRACT" },
                            color = if (selectedCategory == "SIDE_CONTRACT") WitcherDarkSurfaceVariant else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (selectedCategory == "SIDE_CONTRACT") WitcherRedPrimary else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📜 Contracts & Sides", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WitcherRedPrimary)
                                    Text("${(sideProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WitcherRedPrimary)
                                }
                                LinearProgressIndicator(
                                    progress = sideProgress,
                                    modifier = Modifier.fillMaxWidth().height(4.dp).padding(top = 4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = WitcherRedPrimary,
                                    trackColor = WitcherDarkSurfaceVariant
                                )
                                Text("Completed: $sideCompleted / $sideTotal", fontSize = 9.sp, color = WitcherMutedText, modifier = Modifier.padding(top = 2.dp))
                            }
                        }

                        // Treasure Hunts Selector
                        Surface(
                            onClick = { selectedCategory = "TREASURE" },
                            color = if (selectedCategory == "TREASURE") WitcherDarkSurfaceVariant else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (selectedCategory == "TREASURE") Color(0xFFB0BEC5) else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💎 Hunt Treasures", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB0BEC5))
                                    Text("${(treasureProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB0BEC5))
                                }
                                LinearProgressIndicator(
                                    progress = treasureProgress,
                                    modifier = Modifier.fillMaxWidth().height(4.dp).padding(top = 4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = Color(0xFFB0BEC5),
                                    trackColor = WitcherDarkSurfaceVariant
                                )
                                Text("Completed: $treasureCompleted / $treasureTotal", fontSize = 9.sp, color = WitcherMutedText, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                // Lore text block corresponding to selection
                val (categoryTitle, categoryColor, categoryQuote, categoryDesc, selectedQuestsList) = when (selectedCategory) {
                    "MAIN" -> quintet(
                        "Geralt's Primary Odyssey",
                        WitcherAmberGold,
                        "\"Destiny is a double-edged sword. You are one edge, and the other is Ciri.\"",
                        "The tracks of your child of surprise are cold, but elder magic leaves echoes. Tracing Ciri through the battles of Temeria and the courts of Skellige represents your primary path. Keep advancing.",
                        mainQuests
                    )
                    "SIDE_CONTRACT" -> quintet(
                        "Critical Side Contracts & Codes",
                        WitcherRedPrimary,
                        "\"A witcher doesn't work out of charity, but leaving villagers to rot in necrophage swamps smells of dishonor.\"",
                        "Slaying grave hags, liftings noonwraith curses, or helping local barons maintain their realms provides vital coins to buy gear. These contracts forged your professional renown.",
                        sideContractQuests
                    )
                    else -> quintet(
                        "Grand Scavenger Treasure Hunts",
                        Color(0xFFB0BEC5),
                        "\"These diagrams belonged to Kolgrim of the Viper school. If we recover them, Willis can forge lethal steel.\"",
                        "Legendary blueprints of forgotten Witcher schools (Wolf, Viper, Cat, Bear, Griffin) await in ancient ruins, crypts, and towers. Retrieve them to acquire extreme combat advantages.",
                        treasureQuests
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WitcherDarkSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = categoryTitle.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = categoryQuote,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = WitcherAmberGold.copy(alpha = 0.85f),
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = categoryDesc,
                        fontSize = 11.sp,
                        color = WitcherWhiteText.copy(alpha = 0.9f),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-browser of detailed quests in this focused category
                Text(
                    text = "📂 Quests Registered on This Path: (${selectedQuestsList.size} Total)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherWhiteText,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                if (selectedQuestsList.isEmpty()) {
                    Text("No quests listed in this path yet. Add some to begin!", fontSize = 11.sp, color = WitcherMutedText, modifier = Modifier.padding(vertical = 10.dp))
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Take up to 4 most relevant to avoid massive bloated lists
                        selectedQuestsList.take(5).forEach { quest ->
                            Surface(
                                onClick = { viewModel.selectQuest(quest) },
                                color = WitcherDarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(0.5.dp, WitcherBorderColor.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val statusSym = when (quest.status) {
                                        "COMPLETED" -> "🟢"
                                        "IN_PROGRESS" -> "🟡"
                                        "FAILED" -> "🔴"
                                        else -> "⚪"
                                    }
                                    Text(statusSym, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(quest.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                                        Text("Giver: ${quest.questgiver}", fontSize = 10.sp, color = WitcherMutedText)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Surface(
                                        color = when {
                                            quest.recommendedLevel <= 5 -> Color(0xFF132A1C)
                                            quest.recommendedLevel <= 15 -> Color(0xFF332A15)
                                            else -> Color(0xFF3E1A1A)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "LVL ${quest.recommendedLevel}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                quest.recommendedLevel <= 5 -> Color(0xFF81C784)
                                                quest.recommendedLevel <= 15 -> Color(0xFFE3C567)
                                                else -> Color(0xFFE57373)
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (selectedQuestsList.size > 5) {
                            Text(
                                text = "...and ${selectedQuestsList.size - 5} more on the Journal Tab",
                                fontSize = 10.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = WitcherMutedText,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Witcher Abilities/Skills Allocation Column Card
        WitcherAbilityTree(
            viewModel = viewModel,
            skills = skills,
            level = level
        )

        Spacer(modifier = Modifier.height(14.dp))

        EquipmentReforgeCard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(14.dp))

        // Personal witcher journal secrets notepad
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "✍️ Path Chronicles (Memo)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Keep track of critical decision logs (e.g. Sparing Keira Metz, Baron fates, or romance paths).",
                    style = MaterialTheme.typography.bodySmall,
                    color = WitcherMutedText,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = personalNotes,
                    onValueChange = { personalNotes = it },
                    placeholder = { Text("Record your path choices...", color = WitcherMutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val intent = null
                                speechRecognizerLauncher.launch(intent)
                            }
                        ) {
                            Text("🎤", fontSize = 18.sp)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WitcherDarkSurfaceVariant,
                        unfocusedContainerColor = WitcherDarkSurfaceVariant,
                        focusedBorderColor = WitcherRedPrimary,
                        unfocusedBorderColor = WitcherDarkSurfaceVariant,
                        focusedTextColor = WitcherWhiteText,
                        unfocusedTextColor = WitcherWhiteText
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gwent Cards & Collection Gallery
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
                        text = "🃏 Gwent Deck & Cards",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold
                    )
                    Surface(
                        color = WitcherRedPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, WitcherRedPrimary)
                    ) {
                        Text(
                            text = "Northern Realms",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = "Behold your active combat deck, featuring legendary hero cards collected along your adventure.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WitcherMutedText,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Grid/Row of cards (Horizontally Scrollable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Card 1: Gwent Back
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_back),
                            contentDescription = "Gwent Card Back",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Card Back",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "Wolf Medallion",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 2: Geralt
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_geralt),
                            contentDescription = "Geralt of Rivia Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Geralt of Rivia",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (15)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 3: Ciri
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_ciri),
                            contentDescription = "Ciri Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ciri of Cintra",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (15)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card Jaskier: Grand Minstrel
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_jaskier),
                            contentDescription = "Jaskier Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Dandelion / Jaskier",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Minstrel Card (2)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 4: Vesemir
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_vesemir),
                            contentDescription = "Vesemir Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Vesemir",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (6)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 5: Yennefer
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_yennefer),
                            contentDescription = "Yennefer Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Yennefer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (7)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 6: Triss
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_triss),
                            contentDescription = "Triss Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Triss Merigold",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (7)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 7: Regis (Newly Added)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_regis),
                            contentDescription = "Regis Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Emiel Regis",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (10)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 8: Zoltan Chivay (Newly Added)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_zoltan),
                            contentDescription = "Zoltan Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Zoltan Chivay",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "Scout Card (5)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 9: Roach (Newly Added)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_roach),
                            contentDescription = "Roach Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Roach",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "Unit Card (3)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 10: Letho of Gulet (Newly Added)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_letho),
                            contentDescription = "Letho Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Letho of Gulet",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (10)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 11: Eredin Gwent Card (Newly Added)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_eredin),
                            contentDescription = "Eredin Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Eredin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Leader Card (10)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 12: Emhyr Gwent Card (Newly Added)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_emhyr),
                            contentDescription = "Emhyr Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Emhyr var Emreis",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Leader Card (10)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 13: Lambert Gwent Card (Newly Generated & Added!)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_lambert),
                            contentDescription = "Lambert Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Lambert",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (15)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 14: Eskel Gwent Card (Newly Generated & Added!)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_eskel),
                            contentDescription = "Eskel Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Eskel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Hero Card (11)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 15: Gaunter O'Dimm Gwent Card (Newly Generated & Added!)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_gaunter),
                            contentDescription = "Gaunter Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Gaunter O'Dimm",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherAmberGold
                        )
                        Text(
                            text = "Leader Card (12)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }

                    // Card 16: Bloody Baron Gwent Card (Newly Generated & Added!)
                    Column(
                        modifier = Modifier.width(115.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_gwent_baron),
                            contentDescription = "Bloody Baron Gwent Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bloody Baron",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "Unit Card (6)",
                            fontSize = 10.sp,
                            color = WitcherMutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                // Quick deck stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Deck Size", fontSize = 10.sp, color = WitcherMutedText)
                        Text("36 / 45", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hero & Leaders", fontSize = 10.sp, color = WitcherMutedText)
                        Text("11", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherAmberGold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Power", fontSize = 10.sp, color = WitcherMutedText)
                        Text("212 PTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tavern Soundboard & Ambient Settings Card
        val ambientSoundEnabled by viewModel.soundAmbientEnabled.collectAsStateWithLifecycle()
        val sfxEnabled by viewModel.soundSfxEnabled.collectAsStateWithLifecycle()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚙️ Kaer Morhen Soundboard",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Design your sensory atmosphere with authentic ambient and notification echoes synthesized directly on the Path.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WitcherMutedText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Row 1: Ambient Sounds Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🌲 Immersive Ambient Sounds",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "Continuous rustle of wilderness winds & hollow mountain drones.",
                            fontSize = 10.sp,
                            color = WitcherMutedText,
                            lineHeight = 13.sp
                        )
                    }
                    Switch(
                        checked = ambientSoundEnabled,
                        onCheckedChange = { viewModel.setAmbientSoundEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WitcherDarkBackground,
                            checkedTrackColor = WitcherAmberGold,
                            uncheckedThumbColor = WitcherMutedText,
                            uncheckedTrackColor = WitcherDarkSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: SFX Update Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚔️ Quest Update Chimes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherWhiteText
                        )
                        Text(
                            text = "Play a majestic perfect fifth chime when updating or writing quests.",
                            fontSize = 10.sp,
                            color = WitcherMutedText,
                            lineHeight = 13.sp
                        )
                    }
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = { viewModel.setSfxEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WitcherDarkBackground,
                            checkedTrackColor = WitcherAmberGold,
                            uncheckedThumbColor = WitcherMutedText,
                            uncheckedTrackColor = WitcherDarkSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Button to Test Sound Effects
                Button(
                    onClick = {
                        com.example.ui.WitcherSoundPlayer.playQuestUpdateChime()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔊 Test Medallion Chime",
                            fontSize = 12.sp,
                            color = WitcherWhiteText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatPill(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = count.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = WitcherMutedText
        )
    }
}


// DIALOG COMPONENT: QUEST DETAILS
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AlchemyChecklist(
    ingredients: List<String>,
    checkedIngredients: Set<String>,
    onToggleIngredient: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        ingredients.distinct().forEach { ingredient ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clickable { onToggleIngredient(ingredient) }
            ) {
                Checkbox(
                    checked = checkedIngredients.contains(ingredient),
                    onCheckedChange = { onToggleIngredient(ingredient) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = WitcherRedPrimary,
                        uncheckedColor = WitcherMutedText
                    )
                )
                Text(
                    text = ingredient,
                    fontSize = 12.sp,
                    color = if (checkedIngredients.contains(ingredient)) WitcherMutedText else WitcherWhiteText
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun QuestDetailsDialog(
    quest: Quest,
    viewModel: QuestViewModel,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var editNotesText by remember { mutableStateOf(quest.notes) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var sketchGenerated by remember(quest.id) { mutableStateOf(false) }
    var generatingState by remember(quest.id) { mutableStateOf<String?>(null) }
    
    // Alchemy tracking
    var checkedIngredients by remember(quest.id) { mutableStateOf(setOf<String>()) }
    
    val questAdvice by viewModel.questAdvice.collectAsStateWithLifecycle()
    val context: Any? = null

    // Alchemy tracking
    val oilIngredients = mapOf(
        "Cursed Oil" to listOf("Dog Tallow", "Fool's Parsley Leaves"),
        "Necrophage Oil" to listOf("Dwarven Spirit", "Rotfiend Bile"),
        "Hybrid Oil" to listOf("Bear Fat", "White Myrtle Petals"),
        "Vampire Oil" to listOf("Dwarven Spirit", "Blood Moss")
    )
    val requiredIngredients = remember(quest.monsterWeaknesses) {
        val weaknesses = quest.monsterWeaknesses?.split(",")?.map { it.trim() } ?: emptyList()
        weaknesses.flatMap { oilIngredients[it] ?: emptyList() }.distinct()
    }

    val speechRecognizerLauncher = DesktopSpeechLauncher
    val isAdviceLoading by viewModel.isFetchAdviceLoading.collectAsStateWithLifecycle()

    var tts by remember { mutableStateOf<DesktopTts?>(null) }
    var speakingText by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val ttsInstance = DesktopTts(context) { status ->
            if (status == DesktopTts.SUCCESS) {
                // Initialized successfully
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun speakText(text: String) {
        tts?.let { speech ->
            if (speakingText == text) {
                speech.stop()
                speakingText = null
            } else {
                speech.stop()
                speech.setPitch(0.55f) // deep voice
                speech.setSpeechRate(0.78f) // slow, weathered pace
                try {
                    speech.setLanguage(Locale.US)
                } catch (e: Exception) {
                    // Fallback
                }

                speech.setOnUtteranceProgressListener(object : com.example.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        speakingText = text
                    }
                    override fun onDone(utteranceId: String?) {
                        speakingText = null
                    }
                    override fun onError(utteranceId: String?) {
                        speakingText = null
                    }
                })

                val params = ttsParams().apply {
                    putString(DesktopTts.Engine.KEY_PARAM_UTTERANCE_ID, "VesemirSpeechId")
                }
                // Clean the markdown tags (bolding like **, etc.) for a better voice read out
                val cleanText = text
                    .replace("**", "")
                    .replace("*", "")
                    .replace("#", "")
                    .replace("- ", " ")
                    .replace("`", "")
                
                speech.speak(cleanText, DesktopTts.QUEUE_FLUSH, params, "VesemirSpeechId")
                speakingText = text
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, WitcherAmberGold, RoundedCornerShape(16.dp)),
            color = WitcherDarkSurface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = formatLabel(quest.type) + " MODULE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherRedPrimary
                        )

                        Text(
                            text = quest.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WitcherWhiteText
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close detailed view", tint = WitcherMutedText)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail elements grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Region", fontSize = 11.sp, color = WitcherMutedText)
                        Text(formatLabel(quest.region), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recommended Lvl", fontSize = 11.sp, color = WitcherMutedText)
                        Spacer(modifier = Modifier.height(2.dp))
                        QuestLevelBadge(level = quest.recommendedLevel, useLargeStyle = true)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("Contractor / Quest Giver", fontSize = 11.sp, color = WitcherMutedText)
                    Text(quest.questgiver, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Mission Synopsis
                Text(
                    text = "📜 Mission Synopsis".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = quest.description,
                    fontSize = 12.sp,
                    color = WitcherWhiteText,
                    lineHeight = 16.sp
                )

                // Suggested weapons weaknesses
                quest.monsterWeaknesses?.let { weaknesses ->
                    if (weaknesses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "⚔️ Tactical Vulnerabilities".uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitcherRedPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                        ) {
                            weaknesses.split(",").forEach { weaknessPart ->
                                val cleanWeakness = weaknessPart.trim()
                                if (cleanWeakness.isNotEmpty()) {
                                    Surface(
                                        color = WitcherDarkSurfaceVariant,
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, WitcherBorderColor)
                                    ) {
                                        Text(
                                            text = cleanWeakness,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WitcherAmberGold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (requiredIngredients.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "🧪 Required Alchemy Ingredients".uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherRedPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    AlchemyChecklist(
                        ingredients = requiredIngredients,
                        checkedIngredients = checkedIngredients,
                        onToggleIngredient = { ingredient ->
                            checkedIngredients = if (checkedIngredients.contains(ingredient)) {
                                checkedIngredients - ingredient
                            } else {
                                checkedIngredients + ingredient
                            }
                        }
                    )
                }

                // Dynamic bestiary sketched representation
                val matchedMonster = remember(quest.id) {
                    val titleL = quest.title.lowercase()
                    val descL = quest.description.lowercase()
                    when {
                        titleL.contains("well") || descL.contains("noonwraith") -> "Noonwraith"
                        titleL.contains("white orchard") || descL.contains("griffin") || titleL.contains("griffin") -> "Royal Griffin"
                        titleL.contains("viper") || descL.contains("ghoul") -> "Ghoul"
                        titleL.contains("wild at heart") || descL.contains("werewolf") -> "Werewolf"
                        titleL.contains("jenny") || descL.contains("nightwraith") -> "Nightwraith"
                        titleL.contains("byways") || descL.contains("katakan") || descL.contains("vampire") && !titleL.contains("toad") -> "Katakan"
                        titleL.contains("swamp thing") || descL.contains("foglet") -> "Foglet"
                        titleL.contains("tome") || descL.contains("grave hag") -> "Grave Hag"
                        titleL.contains("phantom") || descL.contains("wyvern") || titleL.contains("patrol") -> "Wyvern"
                        titleL.contains("heart of the woods") || descL.contains("leshen") -> "Leshen"
                        titleL.contains("dragon") || descL.contains("forktail") -> "Noonshade Forktail"
                        titleL.contains("shrieker") || descL.contains("cockatrice") -> "Cockatrice"
                        titleL.contains("basilisk") || titleL.contains("basilisk") -> "Basilisk"
                        titleL.contains("chort") || titleL.contains("chort") -> "Chort"
                        titleL.contains("drowner") || titleL.contains("drowner") -> "Drowner"
                        titleL.contains("nekker") || titleL.contains("nekker") -> "Nekker"
                        titleL.contains("toad") || titleL.contains("toad") || titleL.contains("oxenfurt sewers") -> "Toad Prince"
                        titleL.contains("beast of honorton") || descL.contains("water hag") || descL.contains("swamp crone") || titleL.contains("muire d'yaeblen") -> "Water Hag"
                        quest.monsterWeaknesses?.lowercase()?.contains("vampire") == true -> "Higher Vampire"
                        else -> "Unknown Beast"
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, WitcherBorderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = WitcherDarkSurfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎨 Target Creature Bestiary Sketch".uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitcherAmberGold
                            )
                            if (sketchGenerated) {
                                Surface(
                                    color = WitcherSuccess.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "TRANSCRIPTION COMPLETE ✓",
                                        color = WitcherSuccess,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!sketchGenerated && generatingState == null) {
                            Text(
                                text = "A professional Witcher is guided by precise anatomy. A record of $matchedMonster was detected in this report, but its visual charcoal sketch hasn't been rendered yet.",
                                fontSize = 11.sp,
                                color = WitcherMutedText,
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        generatingState = "Staining parchment with swamp oils..."
                                        kotlinx.coroutines.delay(1000)
                                        generatingState = "Grinding charcoal pigments..."
                                        kotlinx.coroutines.delay(1000)
                                        generatingState = "Inscribing runes of protection..."
                                        kotlinx.coroutines.delay(1000)
                                        generatingState = null
                                        sketchGenerated = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary, contentColor = WitcherDarkBackground),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("🪄 Spark Bestiary Sketch Placeholder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (generatingState != null) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = WitcherRedPrimary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = generatingState ?: "Transcribing...",
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = WitcherAmberGold
                                )
                            }
                        } else {
                            // Generated sketch portrait row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val beastImageRes = when (matchedMonster) {
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
                                    else -> null
                                }

                                if (beastImageRes != null) {
                                    Image(
                                        painter = painterResource(beastImageRes),
                                        contentDescription = "$matchedMonster sketch",
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, WitcherAmberGold, RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Custom visual generator fallback (Runic Shield Drawing on Canvas)
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .background(WitcherDarkSurface, RoundedCornerShape(8.dp))
                                            .border(1.dp, WitcherRedPrimary, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                            val c = Offset(size.width / 2f, size.height / 2f)
                                            drawCircle(
                                                color = WitcherRedPrimary.copy(alpha = 0.4f),
                                                radius = size.width / 3f,
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                                            )
                                            drawLine(
                                                color = WitcherAmberGold.copy(alpha = 0.5f),
                                                start = Offset(c.x - 20.dp.toPx(), c.y - 20.dp.toPx()),
                                                end = Offset(c.x + 20.dp.toPx(), c.y + 20.dp.toPx()),
                                                strokeWidth = 2.dp.toPx()
                                            )
                                            drawLine(
                                                color = WitcherAmberGold.copy(alpha = 0.5f),
                                                start = Offset(c.x + 20.dp.toPx(), c.y - 20.dp.toPx()),
                                                end = Offset(c.x - 20.dp.toPx(), c.y + 20.dp.toPx()),
                                                strokeWidth = 2.dp.toPx()
                                            )
                                        }
                                        Text("🛡️", fontSize = 24.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = matchedMonster,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WitcherAmberGold
                                    )
                                    Text(
                                        text = "Anatomical Sketch of this target beast recorded inside the journal databases. High-contrast sketch outline created successfully.",
                                        fontSize = 10.sp,
                                        color = WitcherWhiteText,
                                        lineHeight = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Weaknesses: ${quest.monsterWeaknesses ?: "Apply Necrophage Oil"}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WitcherRedPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rewards
                Text(
                    text = "🏆 Adventure Rewards".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = quest.rewards,
                    fontSize = 12.sp,
                    color = WitcherWhiteText
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Preloaded Advice section (Geralt & Advisor) with Voice integration
                if (quest.geraltAdvice != null || quest.advisorAdvice != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(WitcherDarkSurfaceVariant, RoundedCornerShape(10.dp))
                            .border(1.dp, WitcherBorderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📜 Preloaded Tactical Counsel".uppercase(),
                            color = WitcherAmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (quest.geraltAdvice != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(WitcherDarkBackground, RoundedCornerShape(6.dp))
                                    .border(1.dp, WitcherBorderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🐺 Geralt's Counsel".uppercase(), color = WitcherAmberGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { speakText(quest.geraltAdvice) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text(
                                            text = if (speakingText == quest.geraltAdvice) "🔇" else "🔊",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(quest.geraltAdvice, color = WitcherWhiteText, fontSize = 11.sp, lineHeight = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (quest.advisorAdvice != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(WitcherDarkBackground, RoundedCornerShape(6.dp))
                                    .border(1.dp, WitcherBorderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🧪 Advisor's Counsel".uppercase(), color = WitcherRedPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { speakText(quest.advisorAdvice) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text(
                                            text = if (speakingText == quest.advisorAdvice) "🔇" else "🔊",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(quest.advisorAdvice, color = WitcherWhiteText, fontSize = 11.sp, lineHeight = 14.sp)
                            }
                        }
                    }
                }

                // AI Advice Button & Response Box
                Text(
                    text = "🔮 Witcher's AI Counsel".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                if (questAdvice == null && !isAdviceLoading) {
                    Button(
                        onClick = { viewModel.fetchQuestAdvice(quest) },
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Seek Vesemir's Counsel",
                            tint = WitcherDarkBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Seek Vesemir's Counsel",
                            color = WitcherDarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, WitcherRedPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        color = WitcherDarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Advisor advice",
                                        tint = WitcherAmberGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Vesemir's Path Advice",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WitcherAmberGold
                                    )
                                    if (questAdvice != null && !isAdviceLoading) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                questAdvice?.let { speakText(it) }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text(
                                                text = if (speakingText == questAdvice) "🔇" else "🔊",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                if (!isAdviceLoading) {
                                    Text(
                                        text = "Re-Consult",
                                        fontSize = 10.sp,
                                        color = WitcherMutedText,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .clickable { viewModel.fetchQuestAdvice(quest) }
                                            .border(0.5.dp, WitcherMutedText, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isAdviceLoading) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = WitcherAmberGold,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Consulting old Vesemir...",
                                        fontSize = 12.sp,
                                        color = WitcherMutedText,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            } else {
                                Text(
                                    text = questAdvice ?: "",
                                    fontSize = 12.sp,
                                    color = WitcherWhiteText,
                                    lineHeight = 17.sp
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val adviceText = questAdvice
                                        if (!adviceText.isNullOrEmpty()) {
                                            val newNotes = if (editNotesText.isEmpty()) adviceText else "$editNotesText\n\n📝 AI Advice:\n$adviceText"
                                            editNotesText = newNotes
                                            onNotesChange(newNotes)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WitcherDarkSurface),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .height(30.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Log to Decisions",
                                        fontSize = 10.sp,
                                        color = WitcherAmberGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "💬 Link selection to Chat Rooms".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.askAIAboutQuestDirectly(quest, "CHAT")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherDarkSurfaceVariant),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, WitcherAmberGold.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🐺", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ask Geralt", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.askAIAboutQuestDirectly(quest, "ADVISOR")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherDarkSurfaceVariant),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, WitcherRedPrimary.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🧪", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ask Advisor", color = WitcherRedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Decision Tree Visualizer
                DecisionTreeVisualizer(
                    quest = quest,
                    onNotesChange = { loggedReason ->
                        editNotesText = loggedReason
                        onNotesChange(loggedReason)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = WitcherDarkSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Path Decision notes input
                Text(
                    text = "✍️ Decisions Logged for this Quest",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherWhiteText,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = editNotesText,
                    onValueChange = {
                        editNotesText = it
                        onNotesChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val intent = null
                                speechRecognizerLauncher.launch(intent)
                            }
                        ) {
                            Text("🎤", fontSize = 16.sp)
                        }
                    },
                    placeholder = { Text("Log choices made in this path...", color = WitcherMutedText, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WitcherDarkSurfaceVariant,
                        unfocusedContainerColor = WitcherDarkSurfaceVariant,
                        focusedBorderColor = WitcherRedPrimary,
                        unfocusedBorderColor = WitcherDarkSurfaceVariant,
                        focusedTextColor = WitcherWhiteText,
                        unfocusedTextColor = WitcherWhiteText
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Critical Narrative Choices Input
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "🎭 Critical Narrative Choices",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherWhiteText,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var narrativeText by remember { mutableStateOf(quest.narrativeChoices) }
                OutlinedTextField(
                    value = narrativeText,
                    onValueChange = {
                        narrativeText = it
                        viewModel.updateQuestNarrativeChoices(quest.id, it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    placeholder = { Text("Log crucial story decisions here...", color = WitcherMutedText, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WitcherDarkSurfaceVariant,
                        unfocusedContainerColor = WitcherDarkSurfaceVariant,
                        focusedBorderColor = WitcherAmberGold,
                        unfocusedBorderColor = WitcherDarkSurfaceVariant,
                        focusedTextColor = WitcherWhiteText,
                        unfocusedTextColor = WitcherWhiteText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Adjust Status Picker
                Text(
                    text = "⚙️ Update Status",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitcherMutedText,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf(
                        Pair("NOT_STARTED", "Not Started"),
                        Pair("IN_PROGRESS", "Active"),
                        Pair("COMPLETED", "Completed"),
                        Pair("FAILED", "Failed")
                    )

                    statuses.forEach { (code, label) ->
                        val isCurrent = quest.status == code
                        val buttonColor = when (code) {
                            "NOT_STARTED" -> WitcherMutedText
                            "IN_PROGRESS" -> WitcherInProgress
                            "COMPLETED" -> WitcherSuccess
                            "FAILED" -> WitcherFailed
                            else -> WitcherMutedText
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1.0f)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onStatusChange(code) }
                                .border(
                                    width = 1.dp,
                                    color = if (isCurrent) buttonColor else WitcherDarkSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ),
                            color = if (isCurrent) buttonColor.copy(alpha = 0.25f) else WitcherDarkSurfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) buttonColor else WitcherWhiteText,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // If user custom created, allow deleting
                if (quest.isCustom) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherFailed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete quest", tint = WitcherWhiteText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete Custom Path", fontSize = 12.sp, color = WitcherWhiteText)
                    }
                }

                // Export Functionality
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        val exportStr = "Quest: ${quest.title}\nStatus: ${quest.status}\nNarrative Choices: ${quest.narrativeChoices}\nNotes: ${quest.notes}"
                        com.example.copyToClipboard(exportStr)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WitcherAmberGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Export quest", tint = WitcherDarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Quest", fontSize = 12.sp, color = WitcherDarkBackground)
                }
            }
        }
    }
}

// DIALOG: ADD CUSTOM QUEST DIALOG
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AddQuestDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("MAIN") }
    var region by remember { mutableStateOf("VELEN") }
    var levelText by remember { mutableStateOf("1") }
    var description by remember { mutableStateOf("") }
    var questgiver by remember { mutableStateOf("Board Announcement") }
    var rewards by remember { mutableStateOf("100 XP, Crowns") }
    var monsterWeakness by remember { mutableStateOf("") }

    var isTitleError by remember { mutableStateOf(false) }

    var activeDictationField by remember { mutableStateOf<String?>(null) }

    val speechRecognizerLauncher = DesktopSpeechLauncher

    val triggerDictation = { field: String, prompt: String ->
        activeDictationField = field
        val intent = null
        speechRecognizerLauncher.launch(intent)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(2.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(16.dp)),
            color = WitcherDarkSurface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "⚔️ Draft New Contract",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WitcherAmberGold
                )

                // Title Input
                Column {
                    Text("Quest Title *", fontSize = 11.sp, color = WitcherMutedText)
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            isTitleError = it.isEmpty()
                        },
                        isError = isTitleError,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = { triggerDictation("TITLE", "Dictate quest title...") }
                            ) {
                                Text("🎤", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WitcherDarkSurfaceVariant,
                            unfocusedContainerColor = WitcherDarkSurfaceVariant,
                            focusedBorderColor = WitcherRedPrimary,
                            unfocusedBorderColor = WitcherDarkSurfaceVariant,
                            focusedTextColor = WitcherWhiteText,
                            unfocusedTextColor = WitcherWhiteText
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (isTitleError) {
                        Text("Title is required", color = WitcherFailed, fontSize = 10.sp)
                    }
                }

                // Type Slider
                Column {
                    Text("Quest Type", fontSize = 11.sp, color = WitcherMutedText, modifier = Modifier.padding(bottom = 4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("MAIN", "SIDE", "CONTRACT", "TREASURE").forEach { tp ->
                            val isSel = type == tp
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { type = tp },
                                color = if (isSel) WitcherRedPrimary else WitcherDarkSurfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = formatLabel(tp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WitcherWhiteText,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Region Slider
                Column {
                    Text("Region", fontSize = 11.sp, color = WitcherMutedText, modifier = Modifier.padding(bottom = 4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("WHITE_ORCHARD", "VELEN", "NOVIGRAD", "SKELLIGE", "KAER_MORHEN", "TOUSSAINT").forEach { r ->
                            val isSel = region == r
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { region = r },
                                color = if (isSel) WitcherRedPrimary else WitcherDarkSurfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = formatLabel(r),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WitcherWhiteText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Level + Rewards Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recommended Lvl", fontSize = 11.sp, color = WitcherMutedText)
                        OutlinedTextField(
                            value = levelText,
                            onValueChange = { levelText = it.filter { c -> c.isDigit() } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WitcherDarkSurfaceVariant,
                                unfocusedContainerColor = WitcherDarkSurfaceVariant,
                                focusedBorderColor = WitcherRedPrimary,
                                unfocusedBorderColor = WitcherDarkSurfaceVariant,
                                focusedTextColor = WitcherWhiteText,
                                unfocusedTextColor = WitcherWhiteText
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(2.0f)) {
                        Text("Quest Giver", fontSize = 11.sp, color = WitcherMutedText)
                        OutlinedTextField(
                            value = questgiver,
                            onValueChange = { questgiver = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WitcherDarkSurfaceVariant,
                                unfocusedContainerColor = WitcherDarkSurfaceVariant,
                                focusedBorderColor = WitcherRedPrimary,
                                unfocusedBorderColor = WitcherDarkSurfaceVariant,
                                focusedTextColor = WitcherWhiteText,
                                unfocusedTextColor = WitcherWhiteText
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Description Synopsis
                Column {
                    Text("Synopsis / Description", fontSize = 11.sp, color = WitcherMutedText)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = { triggerDictation("DESCRIPTION", "Dictate synopsis / description...") }
                            ) {
                                Text("🎤", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WitcherDarkSurfaceVariant,
                            unfocusedContainerColor = WitcherDarkSurfaceVariant,
                            focusedBorderColor = WitcherRedPrimary,
                            unfocusedBorderColor = WitcherDarkSurfaceVariant,
                            focusedTextColor = WitcherWhiteText,
                            unfocusedTextColor = WitcherWhiteText
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Monster Weaknesses
                if (type == "CONTRACT") {
                    Column {
                        Text("Monster Weaknesses (Oils, Bombs, Signs)", fontSize = 11.sp, color = WitcherMutedText)
                        OutlinedTextField(
                            value = monsterWeakness,
                            onValueChange = { monsterWeakness = it },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(
                                    onClick = { triggerDictation("WEAKNESS", "Dictate monster weaknesses...") }
                                ) {
                                    Text("🎤", fontSize = 16.sp)
                                }
                            },
                            placeholder = { Text("e.g. Yrden, Specter Oil", fontSize = 12.sp, color = WitcherMutedText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WitcherDarkSurfaceVariant,
                                unfocusedContainerColor = WitcherDarkSurfaceVariant,
                                focusedBorderColor = WitcherRedPrimary,
                                unfocusedBorderColor = WitcherDarkSurfaceVariant,
                                focusedTextColor = WitcherWhiteText,
                                unfocusedTextColor = WitcherWhiteText
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Rewards
                Column {
                    Text("Enterprise Rewards", fontSize = 11.sp, color = WitcherMutedText)
                    OutlinedTextField(
                        value = rewards,
                        onValueChange = { rewards = it },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = { triggerDictation("REWARDS", "Dictate rewards...") }
                            ) {
                                Text("🎤", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WitcherDarkSurfaceVariant,
                            unfocusedContainerColor = WitcherDarkSurfaceVariant,
                            focusedBorderColor = WitcherRedPrimary,
                            unfocusedBorderColor = WitcherDarkSurfaceVariant,
                            focusedTextColor = WitcherWhiteText,
                            unfocusedTextColor = WitcherWhiteText
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Buttons Save Cancel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WitcherWhiteText),
                        border = BorderStroke(1.dp, WitcherDarkSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (title.isEmpty()) {
                                isTitleError = true
                            } else {
                                val lvl = levelText.toIntOrNull() ?: 1
                                val weaknesses = if (type == "CONTRACT") monsterWeakness else null
                                onSave(title, type, region, lvl, description.ifEmpty { "Geralt tracks down a lead in " + formatLabel(region) }, questgiver, rewards, weaknesses)
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Embark Path", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitcherWhiteText)
                    }
                }
            }
        }
    }
}

// Helpers
fun formatLabel(input: String): String {
    return input.lowercase()
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}

// Custom parchment paper modifier for a medieval manuscript feel
fun Modifier.witcherParchmentTexture(): Modifier = this.drawBehind {
    // Draw deep warm dark sepia leather/parchment background
    drawRect(color = Color(0xFF161411))
    
    // Faint aged paper fibers seed-stable texture noise
    val random = java.util.Random(99)
    val textureColor = Color(0xFF2E2721).copy(alpha = 0.25f)
    val lighterFiberColor = Color(0xFF1D1A17).copy(alpha = 0.35f)
    for (i in 0 until 40) {
        val startX = random.nextFloat() * size.width
        val startY = random.nextFloat() * size.height
        val length = 20f + random.nextFloat() * 120f
        val isHorizontal = random.nextBoolean()
        if (isHorizontal) {
            drawLine(
                color = if (random.nextBoolean()) textureColor else lighterFiberColor,
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset((startX + length).coerceAtMost(size.width), startY),
                strokeWidth = 1.2f
            )
        } else {
            drawLine(
                color = if (random.nextBoolean()) textureColor else lighterFiberColor,
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(startX, (startY + length).coerceAtMost(size.height)),
                strokeWidth = 1.2f
            )
        }
    }
    
    // Darkened vignetted burnt edges for vintage styling
    val edgeVignette = Color(0xFF0A0908)
    // Left Edge
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(edgeVignette, Color.Transparent),
            startX = 0f,
            endX = 45f
        )
    )
    // Right Edge
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(Color.Transparent, edgeVignette),
            startX = size.width - 45f,
            endX = size.width
        )
    )
    // Top Edge
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(edgeVignette, Color.Transparent),
            startY = 0f,
            endY = 45f
        )
    )
    // Bottom Edge
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color.Transparent, edgeVignette),
            startY = size.height - 45f,
            endY = size.height
        )
    )
}

@Composable
fun EquipmentReforgeCard(viewModel: QuestViewModel) {
    val slots by viewModel.gearSlots.collectAsStateWithLifecycle()
    val looks by viewModel.schoolLooks.collectAsStateWithLifecycle()
    var craftsman by remember { mutableStateOf("Yoana") }
    var selectedSlotId by remember { mutableStateOf("armor") }
    val selectedSlot = slots.find { it.id == selectedSlotId } ?: slots.first()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "🔨 Equipment Reforge".uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WitcherAmberGold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Only Yoana at Crow's Perch and Hattori in Novigrad change a piece's look. Stats and Blood and Wine dyes stay.",
                fontSize = 10.sp,
                color = WitcherMutedText
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Yoana", "Hattori").forEach { name ->
                    val selected = craftsman == name
                    Surface(
                        modifier = Modifier.clickable { craftsman = name },
                        color = if (selected) WitcherAmberGold.copy(alpha = 0.2f) else WitcherDarkBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (selected) WitcherAmberGold else WitcherDarkSurfaceVariant)
                    ) {
                        Text(
                            text = name,
                            color = if (selected) WitcherAmberGold else WitcherMutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            slots.forEach { slot ->
                val selected = slot.id == selectedSlot.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSlotId = slot.id }
                        .background(WitcherDarkBackground, RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            if (selected) WitcherAmberGold else WitcherDarkSurfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(slot.label, color = WitcherWhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(slot.statName, color = WitcherMutedText, fontSize = 10.sp)
                    Text(
                        text = "Appearance: ${slot.appearance}" + (slot.dye?.let { " · Dye: $it" } ?: ""),
                        color = WitcherAmberGold,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = "Apply a look to ${selectedSlot.label}",
                color = WitcherWhiteText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                looks.forEach { look ->
                    val label = if (look.unlocked) look.name else "Unlock ${look.name}"
                    Surface(
                        modifier = Modifier.clickable {
                            if (look.unlocked) {
                                viewModel.reforgeEquipment(selectedSlot.id, look.id, craftsman)
                            } else {
                                viewModel.unlockSchoolLook(look.id)
                            }
                        },
                        color = if (look.unlocked) WitcherRedPrimary.copy(alpha = 0.25f) else WitcherDarkBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (look.unlocked) WitcherRedPrimary else WitcherDarkSurfaceVariant)
                    ) {
                        Text(
                            text = label,
                            color = if (look.unlocked) WitcherWhiteText else WitcherMutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WitcherAbilityTree(
    viewModel: QuestViewModel,
    skills: List<WitcherSkill>,
    level: Int
) {
    var selectedCategory by remember { mutableStateOf("COMBAT") } // "COMBAT", "SIGNS", "ALCHEMY", "GENERAL"
    val filteredSkills = skills.filter { it.category == selectedCategory }
    
    val totalSpent = skills.sumOf { it.level }
    val totalPoints = level + 5
    val availablePoints = totalPoints - totalSpent

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
                Column {
                    Text(
                        text = "⚔️ Abilities & Skills".uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Three ranks each. Later skills need their prerequisite.",
                        fontSize = 10.sp,
                        color = WitcherMutedText
                    )
                }

                Surface(
                    color = WitcherRedPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, WitcherRedPrimary)
                ) {
                    Text(
                        text = "Points: $availablePoints / $totalPoints",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherAmberGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub tabs for skill categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val categories = listOf(
                    Triple("COMBAT", "⚔️ Combat", WitcherRedPrimary),
                    Triple("SIGNS", "✨ Signs", Color(0xFF3498DB)),
                    Triple("ALCHEMY", "🧪 Alchemy", Color(0xFF2ECC71)),
                    Triple("GENERAL", "⭐ General", WitcherAmberGold)
                )

                categories.forEach { (catId, label, catColor) ->
                    val isSelected = selectedCategory == catId
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCategory = catId },
                        color = if (isSelected) catColor.copy(alpha = 0.25f) else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) catColor else Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) WitcherWhiteText else WitcherMutedText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Skill items
            if (filteredSkills.isEmpty()) {
                Text("No skills in this category.", color = WitcherMutedText, fontSize = 12.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredSkills.forEach { skill ->
                        val themeColor = when (skill.category) {
                            "COMBAT" -> WitcherRedPrimary
                            "SIGNS" -> Color(0xFF3498DB)
                            "ALCHEMY" -> Color(0xFF2ECC71)
                            else -> WitcherAmberGold
                        }
                        val unmetPrerequisites = skill.prerequisiteIds.mapNotNull { id ->
                            skills.find { it.id == id }?.takeIf { it.level < 1 }?.name
                        }
                        val locked = unmetPrerequisites.isNotEmpty()
                        val refundBlocked = skill.level == 1 && skills.any { other ->
                            other.level > 0 && skill.id in other.prerequisiteIds
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WitcherDarkBackground, RoundedCornerShape(8.dp))
                                .border(1.dp, WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = skill.name,
                                        color = if (locked) WitcherMutedText else WitcherWhiteText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (locked) {
                                        Text(
                                            text = "Requires ${unmetPrerequisites.joinToString()}",
                                            color = WitcherAmberGold,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        (1..skill.maxLevel).forEach { i ->
                                            val isFilled = i <= skill.level
                                            Text(
                                                text = if (isFilled) "◆" else "◇",
                                                fontSize = 12.sp,
                                                color = if (isFilled) themeColor else WitcherMutedText,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Rank ${skill.level}/${skill.maxLevel}",
                                            fontSize = 9.sp,
                                            color = WitcherAmberGold
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.adjustSkillPoints(skill.id, -1) },
                                        enabled = skill.level > 0 && !refundBlocked,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(WitcherDarkSurfaceVariant, CircleShape)
                                    ) {
                                        Text("-", color = WitcherWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = "${skill.level}",
                                        color = WitcherWhiteText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    IconButton(
                                        onClick = { viewModel.adjustSkillPoints(skill.id, 1) },
                                        enabled = !locked && skill.level < skill.maxLevel,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(WitcherDarkSurfaceVariant, CircleShape)
                                    ) {
                                        Text("+", color = if (locked) WitcherMutedText else WitcherWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = skill.description,
                                color = WitcherMutedText,
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper data class for astrolabe dashboard metadata
data class Quintet<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

fun <A, B, C, D, E> quintet(a: A, b: B, c: C, d: D, e: E): Quintet<A, B, C, D, E> {
    return Quintet(a, b, c, d, e)
}
