package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.data.Monster
import com.example.ui.theme.parchmentBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestiaryScreen(viewModel: BestiaryViewModel) {
    val monsters by viewModel.monsters.collectAsState(initial = emptyList())
    val query by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Bestiary") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(monsters) { monster ->
                MonsterCard(monster, onAskVesemir = { /* TODO: Implement Ask Vesemir */ })
            }
        }
    }
}

@Composable
fun MonsterCard(monster: Monster, onAskVesemir: (Monster) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = monster.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = monster.category, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = monster.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Weaknesses: ${monster.weaknesses.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Combat Guide: ${monster.combatGuide}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onAskVesemir(monster) }) {
                Text("Ask Vesemir")
            }
        }
    }
}
