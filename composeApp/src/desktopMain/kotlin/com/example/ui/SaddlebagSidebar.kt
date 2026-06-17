package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SaddlebagItem
import com.example.ui.theme.*

@Composable
fun SaddlebagSidebarContent(
    viewModel: QuestViewModel,
    onClose: () -> Unit
) {
    val items by viewModel.saddlebagItems.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf("ALL") } // "ALL", "QUEST", "ALCHEMY"
    var showAddItemDialog by remember { mutableStateOf(false) }

    val filteredItems = items.filter {
        when (activeFilter) {
            "QUEST" -> it.category == "QUEST_ITEM"
            "ALCHEMY" -> it.category == "ALCHEMY_INGREDIENT"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WitcherDarkBackground)
            .padding(12.dp)
    ) {
        // Sidebar Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "👜",
                    fontSize = 20.sp
                )
                Column {
                    Text(
                        text = "Saddlebags".uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = WitcherAmberGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Roach's Mobile Stash",
                        fontSize = 9.sp,
                        color = WitcherMutedText
                    )
                }
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .background(WitcherDarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .testTag("saddlebag_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close saddlebags",
                    tint = WitcherWhiteText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Divider(color = WitcherBorderColor, thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Inventory Sub Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(WitcherDarkSurface, RoundedCornerShape(8.dp))
                .border(1.dp, WitcherBorderColor, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val filters = listOf(
                Pair("ALL", "🎒 All"),
                Pair("QUEST", "🔮 Quest"),
                Pair("ALCHEMY", "🧪 Alchemy")
            )
            filters.forEach { (filterId, label) ->
                val isSelected = activeFilter == filterId
                Surface(
                    color = if (isSelected) WitcherRedPrimary else Color.Transparent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { activeFilter = filterId }
                        .testTag("inventory_tab_$filterId")
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) WitcherDarkBackground else WitcherWhiteText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        // List view
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📭",
                        fontSize = 40.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Saddlebag is Empty",
                        color = WitcherWhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Scavenge contracts and ruins to collect potions or monster mutagens.",
                        color = WitcherMutedText,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    SaddlebagItemRow(
                        item = item,
                        onUpdateQty = { delta ->
                            viewModel.updateSaddlebagItemQty(item.id, item.quantity + delta)
                        },
                        onDelete = {
                            viewModel.deleteSaddlebagItem(item.id)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Trigger Button for additions
        Button(
            onClick = { showAddItemDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .testTag("saddlebag_add_discovery_button"),
            colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(16.dp))
                Text(
                    text = "Report Discovery".uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onSave = { name, category, quantity, description, rarity, iconLabel ->
                viewModel.addSaddlebagItem(name, category, quantity, description, rarity, iconLabel)
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun SaddlebagItemRow(
    item: SaddlebagItem,
    onUpdateQty: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val rarityColor = when (item.rarity) {
        "RELIC" -> WitcherAmberGold
        "MAGIC" -> Color(0xFF8E44AD) // Purple
        "RARE" -> Color(0xFF2980B9)  // Blue
        else -> WitcherMutedText     // Common
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .border(
                width = if (item.rarity == "RELIC") 1.5.dp else 1.dp,
                color = if (item.rarity == "RELIC") WitcherAmberGold.copy(alpha = 0.5f) else WitcherBorderColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = WitcherDarkSurface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(WitcherDarkSurfaceVariant, RoundedCornerShape(6.dp))
                        .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconLabel, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name & details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitcherWhiteText
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.rarity,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = rarityColor
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .background(WitcherMutedText, RoundedCornerShape(50))
                        )
                        Text(
                            text = if (item.category == "QUEST_ITEM") "Quest Item" else "Alchemy",
                            fontSize = 8.sp,
                            color = WitcherMutedText
                        )
                    }
                }

                // Quantity controller
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onUpdateQty(-1) },
                        modifier = Modifier
                            .size(24.dp)
                            .background(WitcherDarkSurfaceVariant, CircleShape)
                    ) {
                        Text("-", color = WitcherWhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${item.quantity}",
                        color = WitcherWhiteText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { onUpdateQty(1) },
                        modifier = Modifier
                            .size(24.dp)
                            .background(WitcherDarkSurfaceVariant, CircleShape)
                    ) {
                        Text("+", color = WitcherWhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Trash item",
                            tint = WitcherFailed.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Expanded Description
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(WitcherDarkSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = item.description,
                        fontSize = 10.sp,
                        color = WitcherWhiteText.copy(alpha = 0.9f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ALCHEMY_INGREDIENT") } // "ALCHEMY_INGREDIENT", "QUEST_ITEM"
    var quantityText by remember { mutableStateOf("1") }
    var description by remember { mutableStateOf("") }
    var rarity by remember { mutableStateOf("COMMON") } // "COMMON", "RARE", "MAGIC", "RELIC"
    var iconLabel by remember { mutableStateOf("🧪") } // Default alchemy icon

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, WitcherAmberGold, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = WitcherDarkBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Log New Discovery",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = WitcherAmberGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = WitcherBorderColor)

                // Item Name
                Column {
                    Text("Item Name", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth().testTag("add_item_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WitcherWhiteText,
                            unfocusedTextColor = WitcherWhiteText,
                            focusedBorderColor = WitcherAmberGold,
                            unfocusedBorderColor = WitcherBorderColor,
                            focusedContainerColor = WitcherDarkSurface,
                            unfocusedContainerColor = WitcherDarkSurface
                        )
                    )
                }

                // Category selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf(
                        Pair("ALCHEMY_INGREDIENT", "🧪 Alchemy"),
                        Pair("QUEST_ITEM", "🔮 Quest Item")
                    )
                    categories.forEach { (catId, label) ->
                        val isSelected = category == catId
                        Surface(
                            color = if (isSelected) WitcherRedPrimary else WitcherDarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) WitcherAmberGold else WitcherBorderColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    category = catId
                                    iconLabel = if (catId == "QUEST_ITEM") "🔮" else "🧪"
                                }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) WitcherDarkBackground else WitcherWhiteText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Rarity & Quantity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quantity
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Qty", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { if (it.all { char -> char.isDigit() }) quantityText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = WitcherWhiteText,
                                unfocusedTextColor = WitcherWhiteText,
                                focusedBorderColor = WitcherAmberGold,
                                unfocusedBorderColor = WitcherBorderColor,
                                focusedContainerColor = WitcherDarkSurface,
                                unfocusedContainerColor = WitcherDarkSurface
                            )
                        )
                    }

                    // Emoji Icon choosing
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Graphic Icon", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(WitcherDarkSurface, RoundedCornerShape(4.dp))
                                .border(1.dp, WitcherBorderColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val icons = if (category == "QUEST_ITEM") listOf("🔮", "📜", "💀", "👁️") else listOf("🧪", "🌿", "🧠", "🩸")
                            icons.forEach { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(if (iconLabel == emoji) WitcherRedPrimary.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(4.dp))
                                        .border(1.dp, if (iconLabel == emoji) WitcherAmberGold else Color.Transparent, RoundedCornerShape(4.dp))
                                        .clickable { iconLabel = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // Rarity Grid
                Column {
                    Text("Quality & Rarity", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val rarities = listOf("COMMON", "RARE", "MAGIC", "RELIC")
                        rarities.forEach { rank ->
                            val isSelected = rarity == rank
                            val rankColor = when (rank) {
                                "RELIC" -> WitcherAmberGold
                                "MAGIC" -> Color(0xFF8E44AD)
                                "RARE" -> Color(0xFF2980B9)
                                else -> WitcherMutedText
                            }
                            Surface(
                                color = if (isSelected) rankColor else WitcherDarkSurface,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, if (isSelected) WitcherAmberGold else WitcherBorderColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { rarity = rank }
                            ) {
                                Text(
                                    text = rank,
                                    color = if (isSelected) WitcherDarkBackground else WitcherWhiteText,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Description
                Column {
                    Text("Aesthetic Lore Description", color = WitcherAmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WitcherWhiteText,
                            unfocusedTextColor = WitcherWhiteText,
                            focusedBorderColor = WitcherAmberGold,
                            unfocusedBorderColor = WitcherBorderColor,
                            focusedContainerColor = WitcherDarkSurface,
                            unfocusedContainerColor = WitcherDarkSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WitcherWhiteText),
                        border = BorderStroke(1.dp, WitcherBorderColor)
                    ) {
                        Text("Farewell", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name,
                                    category,
                                    quantityText.toIntOrNull() ?: 1,
                                    description,
                                    rarity,
                                    iconLabel
                                )
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = WitcherRedPrimary)
                    ) {
                        Text("Log Discovery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
