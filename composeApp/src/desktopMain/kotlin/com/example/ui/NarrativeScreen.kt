package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.QuestDecisionTree
import com.example.data.DecisionPath

@Composable
fun NarrativeScreen() {
    val trees = remember { QuestDecisionTree.getAllTrees() }
    var selectedTree by remember { mutableStateOf<QuestDecisionTree?>(null) }
    var selectedPath by remember { mutableStateOf<DecisionPath?>(null) }

    if (selectedTree == null) {
        // List of quests/dilemmas
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(trees) { tree ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { selectedTree = tree }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = tree.questTitle, style = MaterialTheme.typography.titleMedium)
                        Text(text = tree.dilemmaTitle, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    } else if (selectedPath == null) {
        // Show dilemma and paths
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.heightIn(max = 200.dp)) {
                Text(text = selectedTree!!.dilemmaTitle, style = MaterialTheme.typography.headlineSmall)
                Text(text = selectedTree!!.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            selectedTree!!.paths.forEach { path ->
                Button(
                    onClick = { selectedPath = path },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(text = path.choiceName)
                }
            }
            TextButton(onClick = { selectedTree = null }) {
                Text("Back")
            }
        }
    } else {
        // Show outcomes
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.heightIn(max = 200.dp)) {
                Text(text = "Outcome", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = selectedPath!!.immediateNode.title, style = MaterialTheme.typography.titleMedium)
                Text(text = selectedPath!!.immediateNode.description, style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = selectedPath!!.longTermNode.title, style = MaterialTheme.typography.titleMedium)
                Text(text = selectedPath!!.longTermNode.description, style = MaterialTheme.typography.bodyMedium)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(onClick = { 
                selectedTree = null
                selectedPath = null
            }) {
                Text("Back to Contracts")
            }
        }
    }
}
