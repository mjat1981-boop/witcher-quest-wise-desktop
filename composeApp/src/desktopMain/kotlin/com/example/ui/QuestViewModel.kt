package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Monster
import com.example.data.Quest
import com.example.data.SaddlebagItem
import com.example.data.AlchemyRecipe
import com.example.data.IngredientRequirement
import com.example.data.QuestRepository
import com.example.data.MonsterRepository
import com.example.api.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER", "ADVISOR"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPending: Boolean = false
)

data class WitcherSkill(
    val id: String,
    val name: String,
    val category: String, // "COMBAT", "SIGNS", "ALCHEMY", "GENERAL"
    val description: String,
    val maxLevel: Int,
    val level: Int = 0
)

class QuestViewModel(private val repository: QuestRepository, private val monsterRepository: MonsterRepository
) : ViewModel() {

    // Main UI Tabs
    private val _currentTab = MutableStateFlow("JOURNAL") // "JOURNAL", "BESTIARY", "ADVISOR", "PROFILE"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Quests Live Data
    val quests: StateFlow<List<Quest>> = repository.allQuests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Saddlebag Items Live Data
    val saddlebagItems: StateFlow<List<SaddlebagItem>> = repository.allSaddlebagItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSaddlebagItem(name: String, category: String, quantity: Int, description: String, rarity: String, iconLabel: String) {
        viewModelScope.launch {
            repository.insertSaddlebagItem(
                SaddlebagItem(
                    name = name,
                    category = category,
                    quantity = quantity,
                    description = description,
                    rarity = rarity,
                    iconLabel = iconLabel
                )
            )
        }
    }

    fun updateSaddlebagItemQty(id: Int, quantity: Int) {
        viewModelScope.launch {
            repository.updateSaddlebagItemQuantity(id, quantity)
        }
    }

    fun deleteSaddlebagItem(id: Int) {
        viewModelScope.launch {
            repository.deleteSaddlebagItem(id)
        }
    }

    // Filters for Quests List
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow("ALL") // "ALL", "MAIN", "SIDE", "CONTRACT", "TREASURE"
    val typeFilter = _typeFilter.asStateFlow()

    private val _regionFilter = MutableStateFlow("ALL") // "ALL", "WHITE_ORCHARD", "VELEN", "NOVIGRAD", "SKELLIGE", "KAER_MORHEN", "TOUSSAINT"
    val regionFilter = _regionFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow("ALL") // "ALL", "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "FAILED"
    val statusFilter = _statusFilter.asStateFlow()

    private val _levelRangeFilter = MutableStateFlow("ALL") // "ALL", "1_5", "6_15", "16_25", "26_UP"
    val levelRangeFilter = _levelRangeFilter.asStateFlow()

    // Sorted by
    private val _sortBy = MutableStateFlow("LEVEL_ASC") // "LEVEL_ASC", "LEVEL_DESC", "TITLE_ASC"
    val sortBy = _sortBy.asStateFlow()

    // Filtered Quests List derived using combine
    val filteredQuests: StateFlow<List<Quest>> = combine(
        quests, searchQuery, typeFilter, regionFilter, statusFilter, _sortBy, _levelRangeFilter
    ) { arrayOfFlows ->
        @Suppress("UNCHECKED_CAST")
        val questList = arrayOfFlows[0] as List<Quest>
        val query = arrayOfFlows[1] as String
        val type = arrayOfFlows[2] as String
        val region = arrayOfFlows[3] as String
        val status = arrayOfFlows[4] as String
        val sort = arrayOfFlows[5] as String
        val levelRange = arrayOfFlows[6] as String

        var list = questList

        // Apply Search query (searches across title, description, questgiver, region name, and quest type)
        if (query.isNotEmpty()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.questgiver.contains(query, ignoreCase = true) ||
                        it.region.replace("_", " ").contains(query, ignoreCase = true) ||
                        it.type.contains(query, ignoreCase = true)
            }
        }

        // Apply Type Filter
        if (type != "ALL") {
            list = if (type == "SIDE_CONTRACT") {
                list.filter { it.type == "SIDE" || it.type == "CONTRACT" }
            } else {
                list.filter { it.type == type }
            }
        }

        // Apply Region Filter
        if (region != "ALL") {
            list = list.filter { it.region == region }
        }

        // Apply Status Filter
        if (status != "ALL") {
            list = list.filter { it.status == status }
        }

        // Apply Level Range Filter
        if (levelRange != "ALL") {
            list = when (levelRange) {
                "1_5" -> list.filter { it.recommendedLevel in 1..5 }
                "6_15" -> list.filter { it.recommendedLevel in 6..15 }
                "16_25" -> list.filter { it.recommendedLevel in 16..25 }
                "26_UP" -> list.filter { it.recommendedLevel >= 26 }
                else -> list
            }
        }

        // Apply Sorting
        list = when (sort) {
            "LEVEL_ASC" -> list.sortedBy { it.recommendedLevel }
            "LEVEL_DESC" -> list.sortedByDescending { it.recommendedLevel }
            "TITLE_ASC" -> list.sortedBy { it.title }
            else -> list
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Quest Details modal state
    private val _selectedQuest = MutableStateFlow<Quest?>(null)
    val selectedQuest = _selectedQuest.asStateFlow()

    // Sound & Immersive Feedback Settings
    private val _soundAmbientEnabled = MutableStateFlow(false)
    val soundAmbientEnabled = _soundAmbientEnabled.asStateFlow()

    private val _soundSfxEnabled = MutableStateFlow(true)
    val soundSfxEnabled = _soundSfxEnabled.asStateFlow()

    // Witcher Stats Profile
    private val _witcherLevel = MutableStateFlow(1)
    val witcherLevel = _witcherLevel.asStateFlow()

    private val _witcherSchool = MutableStateFlow("Wolf") // "Wolf", "Griffin", "Cat", "Bear", "Viper", "Manticore"
    val witcherSchool = _witcherSchool.asStateFlow()

    // Witcher Next-Gen Abilities/Skills (Combat, Signs, Alchemy = Max 3. General = Max 1)
    private val _witcherSkills = MutableStateFlow<List<WitcherSkill>>(
        listOf(
            WitcherSkill(
                id = "muscle_memory",
                name = "Muscle Memory",
                category = "COMBAT",
                description = "Fast attack damage increased: +10% / +20% / +30%. Adrenaline Point gain bonus: +2% / +4% / +6%.",
                maxLevel = 3,
                level = 1
            ),
            WitcherSkill(
                id = "strength_training",
                name = "Strength Training",
                category = "COMBAT",
                description = "Strong attack damage increased: +10% / +20% / +30%. Adrenaline Point gain bonus: +2% / +4% / +6%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "fleet_footed",
                name = "Fleet Footed",
                category = "COMBAT",
                description = "Defensive dodging reducing incoming damage by: 40% / 70% / 100%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "arrow_deflection",
                name = "Arrow Deflection",
                category = "COMBAT",
                description = "Deflects arrows while parrying. Lvl 2: reflects arrow. Lvl 3: reflects & deals double damage.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "whirl",
                name = "Whirl",
                category = "COMBAT",
                description = "Spinning attack that strikes all enemies in an active radius. Stamina and Adrenaline consumption reduced by 20% / 35% / 50%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "rend",
                name = "Rend",
                category = "COMBAT",
                description = "Deals a devastating heavy blow that ignores enemy defense. Critical hit chance increased by 25% / 50% / 75%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "melt_armor",
                name = "Melt Armor",
                category = "SIGNS",
                description = "Igni sign permanently reduces enemy armor value by: 10% / 20% / 30% (scales with Sign intensity).",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "exploding_shield",
                name = "Exploding Shield",
                category = "SIGNS",
                description = "Quen shield knocks back enemies on breaking. Lvl 2/3: adds chance to damage & knock down.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "delusion",
                name = "Delusion",
                category = "SIGNS",
                description = "Axii sign dialogue persuasion unlocked. Target does not advance. Casting time reduced: 20% / 40% / 60%.",
                maxLevel = 3,
                level = 1
            ),
            WitcherSkill(
                id = "firestream",
                name = "Firestream",
                category = "SIGNS",
                description = "Igni emits a continuous stream of fire. Sign intensity bonus: +5% / +15% / +25% active.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "active_shield",
                name = "Active Shield",
                category = "SIGNS",
                description = "Creates an energy shield that absorbs damage and restores Vitality. Stamina drain reduced: 25% / 50% / 75%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "poisoned_blades",
                name = "Poisoned Blades",
                category = "ALCHEMY",
                description = "Weapon oils grant a chance to poison target on hit dependent on oil tier: up to 10% / 15% / 25%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "heightened_tolerance",
                name = "Heightened Tolerance",
                category = "ALCHEMY",
                description = "Increases chemical overdose toxicity safety threshold by: 30% / 50% / 80%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "refreshment",
                name = "Refreshment",
                category = "ALCHEMY",
                description = "Drinking any potion instantly heals portion of maximum vitality: 10% / 20% / 30%.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "acquired_tolerance",
                name = "Acquired Tolerance",
                category = "ALCHEMY",
                description = "Every known alchemical formula increases maximum Toxicity limit by 0.5 / 1 / 1.5 points.",
                maxLevel = 3,
                level = 0
            ),
            WitcherSkill(
                id = "cat_school",
                name = "Cat School Techniques",
                category = "GENERAL",
                description = "Each light armor item increases fast attack damage by 5% and critical hit damage by 25%.",
                maxLevel = 1,
                level = 1
            ),
            WitcherSkill(
                id = "griffin_school",
                name = "Griffin School Techniques",
                category = "GENERAL",
                description = "Each medium armor item increases sign intensity by 5% and stamina regeneration by 5%.",
                maxLevel = 1,
                level = 0
            ),
            WitcherSkill(
                id = "bear_school",
                name = "Bear School Techniques",
                category = "GENERAL",
                description = "Each heavy armor item increases maximum vitality by 5% and strong attack damage by 5%.",
                maxLevel = 1,
                level = 0
            ),
            WitcherSkill(
                id = "sun_stars",
                name = "Sun and Stars",
                category = "GENERAL",
                description = "Daytime regenerates 10 Vitality/s out of combat. Night regenerates 1 Stamina/s in combat.",
                maxLevel = 1,
                level = 0
            ),
            WitcherSkill(
                id = "gourmet",
                name = "Gourmet",
                category = "GENERAL",
                description = "Eating food regenerates Vitality for a duration of 20 minutes instead of the standard 5-10 seconds.",
                maxLevel = 1,
                level = 0
            ),
            WitcherSkill(
                id = "survival_instinct",
                name = "Survival Instinct",
                category = "GENERAL",
                description = "Increases maximum Vitality by 500 health points permanently.",
                maxLevel = 1,
                level = 0
            )
        )
    )
    val witcherSkills = _witcherSkills.asStateFlow()


    // Chat State for AI Advisor
    private val _selectedAdvisor = MutableStateFlow("Vesemir")
    val selectedAdvisor = _selectedAdvisor.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ADVISOR",
                text = "Welcome back to the path, young witcher. I am Vesemir. Tell me, what challenges do you face? Looking for contract battle strategies, gearing paths, or the weight of your choices? Go on, speak."
            )
        )
    )
    val chatMessages = _chatMessages.asStateFlow()

    // Chat State for Geralt chatbot
    private val _geraltMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "GERALT",
                text = "Hmm. Wind's howling. Speak, friend. What contract from the Path brings you before me? Or did you just come to share a pint of Kaer Morhen brew?"
            )
        )
    )
    val geraltMessages = _geraltMessages.asStateFlow()

    init {
        // Initialize the default quests on viewModel creation
        viewModelScope.launch {
            repository.initializeDefaultQuests()
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun askVesemirAboutMonster(monsterName: String) {
        setAdvisor("Vesemir")
        setTab("ADVISOR")
        sendAdvisorMessage("Tell me how to fight a $monsterName. What are its exact vulnerabilities, and how should I prepare for such a contract?")
    }

    // Quest Advice States
    private val _questAdvice = MutableStateFlow<String?>(null)
    val questAdvice = _questAdvice.asStateFlow()

    private val _isFetchAdviceLoading = MutableStateFlow(false)
    val isFetchAdviceLoading = _isFetchAdviceLoading.asStateFlow()

    fun fetchQuestAdvice(quest: Quest) {
        _isFetchAdviceLoading.value = true
        _questAdvice.value = null
        viewModelScope.launch {
            val systemPrompt = """
                You are Vesemir, the oldest and wisest witcher at Kaer Morhen. Speak in a rugged, helpful, elder witcher voice.
                You are providing a younger witcher on the Path with custom, lore-friendly tactical tips, sign recommendations, and choice-consequence warnings regarding a specific quest.
                Be concise (maximum 3 concise bullet points). Focus on specific Witcher lore and game preparation elements (e.g., Grapeshot, Moon Dust, Swallow potion, specific oils, signs like Quen/Yrden/Igni). Keep the narrative gritty, dark, atmospheric, and incredibly helpful for completing this quest. Use markdown formatting elegantly (*italicized*, **bold** words).
            """.trimIndent()

            val userMessage = """
                Provide advice for Quest: "${quest.title}" (${quest.type} Quest in ${quest.region}, Recommended Level: ${quest.recommendedLevel}).
                Quest details: ${quest.description}
                Weaknesses check: ${quest.monsterWeaknesses ?: "None listed"}
            """.trimIndent()

            val result = GeminiClient.consultAdvisor(userMessage, systemPrompt)
            _questAdvice.value = result
            _isFetchAdviceLoading.value = false
        }
    }

    // Quest Modifiers
    fun selectQuest(quest: Quest?) {
        _selectedQuest.value = quest
        _questAdvice.value = null
        _isFetchAdviceLoading.value = false
    }

    fun updateQuestStatus(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateStatus(id, status)
            // If the selected quest is currently open, refresh its detail panel
            _selectedQuest.value?.let { current ->
                if (current.id == id) {
                    _selectedQuest.value = current.copy(status = status)
                }
            }
            if (_soundSfxEnabled.value) {
                if (status == "COMPLETED") {
                    WitcherSoundPlayer.playQuestCompleteChime()
                } else {
                    WitcherSoundPlayer.playQuestUpdateChime()
                }
            }
        }
    }

    fun updateQuestNotes(id: Int, notes: String) {
        viewModelScope.launch {
            repository.updateNotes(id, notes)
            _selectedQuest.value?.let { current ->
                if (current.id == id) {
                    _selectedQuest.value = current.copy(notes = notes)
                }
            }
        }
    }

    fun updateQuestNarrativeChoices(id: Int, choices: String) {
        viewModelScope.launch {
            repository.updateNarrativeChoices(id, choices)
            _selectedQuest.value?.let { current ->
                if (current.id == id) {
                    _selectedQuest.value = current.copy(narrativeChoices = choices)
                }
            }
        }
    }

    fun addCustomQuest(
        title: String,
        type: String,
        region: String,
        recommendedLevel: Int,
        description: String,
        questgiver: String,
        rewards: String,
        monsterWeaknesses: String?
    ) {
        viewModelScope.launch {
            val q = Quest(
                title = title,
                type = type,
                region = region,
                recommendedLevel = recommendedLevel,
                description = description,
                questgiver = questgiver,
                rewards = rewards,
                status = "NOT_STARTED",
                monsterWeaknesses = monsterWeaknesses,
                isCustom = true
            )
            repository.insert(q)
            if (_soundSfxEnabled.value) {
                WitcherSoundPlayer.playQuestUpdateChime()
            }
        }
    }

    fun deleteQuest(quest: Quest) {
        viewModelScope.launch {
            repository.delete(quest)
            if (_selectedQuest.value?.id == quest.id) {
                _selectedQuest.value = null
            }
        }
    }

    // Set Filters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: String) {
        _typeFilter.value = type
    }

    fun setRegionFilter(region: String) {
        _regionFilter.value = region
    }

    fun setStatusFilter(status: String) {
        _statusFilter.value = status
    }

    fun setLevelRangeFilter(levelRange: String) {
        _levelRangeFilter.value = levelRange
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun setWitcherLevel(level: Int) {
        if (level in 1..100) {
            _witcherLevel.value = level
        }
    }

    fun setWitcherSchool(school: String) {
        _witcherSchool.value = school
    }

    fun applyRecommendedBuild(schoolName: String) {
        val currentSkills = _witcherSkills.value
        val clearedSkills = currentSkills.map { it.copy(level = 0) }

        val allocations = when (schoolName) {
            "Cat" -> mapOf("cat_school" to 1, "muscle_memory" to 3, "fleet_footed" to 3, "whirl" to 3)
            "Griffin" -> mapOf("griffin_school" to 1, "melt_armor" to 3, "firestream" to 3, "active_shield" to 3)
            "Bear" -> mapOf("bear_school" to 1, "strength_training" to 3, "rend" to 3, "heightened_tolerance" to 3)
            "Wolf" -> mapOf("griffin_school" to 1, "muscle_memory" to 3, "melt_armor" to 3, "refreshment" to 3)
            "Viper" -> mapOf("cat_school" to 1, "poisoned_blades" to 3, "heightened_tolerance" to 3, "muscle_memory" to 3)
            "Manticore" -> mapOf("griffin_school" to 1, "heightened_tolerance" to 3, "refreshment" to 3, "acquired_tolerance" to 3)
            else -> mapOf()
        }

        val neededPoints = allocations.values.sum()
        val totalPoints = _witcherLevel.value + 5
        if (totalPoints < neededPoints) {
            _witcherLevel.value = neededPoints - 5 + 1 // Adjust level if too low so points are enough
        }

        _witcherSkills.value = clearedSkills.map { skill ->
            val targetLevel = allocations[skill.id] ?: 0
            skill.copy(level = minOf(targetLevel, skill.maxLevel))
        }
    }

    fun adjustSkillPoints(skillId: String, delta: Int) {
        val currentSkills = _witcherSkills.value
        val skill = currentSkills.find { it.id == skillId } ?: return
        val currentLevel = skill.level
        val newLevel = currentLevel + delta
        
        if (newLevel in 0..skill.maxLevel) {
            val totalSpent = currentSkills.sumOf { it.level }
            val availablePool = _witcherLevel.value + 5
            
            if (delta > 0 && totalSpent >= availablePool) {
                return // No more available points to allocate
            }
            
            _witcherSkills.value = currentSkills.map {
                if (it.id == skillId) it.copy(level = newLevel) else it
            }
        }
    }


    // Send AI advisor message
    fun setAdvisor(advisor: String) {
        _selectedAdvisor.value = advisor
        clearChatHistory()
    }

    fun sendAdvisorMessage(userMessageText: String) {
        if (userMessageText.trim().isEmpty()) return

        val userMessage = ChatMessage(sender = "USER", text = userMessageText)
        _chatMessages.update { it + userMessage }

        val advisorName = _selectedAdvisor.value
        val pendingText = when (advisorName) {
            "Vesemir" -> "Vesemir is considering his response..."
            "Yennefer" -> "Yennefer is sighing and gathering magic..."
            "Jaskier" -> "Jaskier is strumming his lute enthusiastically..."
            else -> "Thinking..."
        }

        val pendingResponse = ChatMessage(sender = "ADVISOR", text = pendingText, isPending = true)
        _chatMessages.update { it + pendingResponse }

        viewModelScope.launch {
            val allQuests = quests.value
            val activeQuestsStr = allQuests.filter { it.status == "IN_PROGRESS" }
                .joinToString("\n") { "- ${it.title} (${it.type} in ${it.region}): ${it.description}" }
            
            val unfinishedSideQuestsStr = allQuests.filter { it.status == "NOT_STARTED" && it.type == "SIDE" }
                .joinToString("\n") { "- ${it.title} in ${it.region}: ${it.description}" }

            val systemPrompt = when (advisorName) {
                "Vesemir" -> """
                    You are Vesemir, the oldest and wisest witcher at Kaer Morhen. Speak in a rugged, helpful, elder witcher voice. You offer combat tactics, potion recipes, decrypters, armor search paths, and decision branching explanations to a younger witcher on the Path. Address the user as 'young witcher' or 'Geralt'.
                    Be concise, highly tactical (citing specific oils, bombs, signs like Igni/Yrden, and battle maneuvers). Maintain an atmospheric Witcher tone of dark, gritty fantasy. Keep paragraphs short and use markdown bold tags elegantly.
                """.trimIndent()
                
                "Yennefer" -> """
                    You are Yennefer of Vengerberg, a powerful, proud sorceress of the Lodge.
                    Speak in a sharp, witty, highly sarcastic, demanding, and sophisticated tone. You are easily annoyed by incompetence, but secretly care deeply.
                    Address the user as 'Geralt' (and occasionally with fondness, though masked in exasperated sarcasm).
                    CRITICAL: If the user asks about unfinished side quests, or if there is any mention of side/secondary activities on the Path, you must make a sharp, sarcastic remark demanding that Geralt stop slacking and actually complete them! 
                    Here are some unfinished side quests Geralt has in his log for context:
                    $unfinishedSideQuestsStr
                    
                    Keep your answers concise, razor-sharp, and dripping with elegant sass. Always maintain character.
                """.trimIndent()
                
                "Jaskier" -> """
                    You are Jaskier (Dandelion), the world-famous bard, poet, and Geralt's best companion.
                    CRITICAL: You MUST speak ENTIRELY in theatrical, dramatic, poetic verses (rhyming or beautifully structured heroic prose/ballads)! Do not speak like a regular person.
                    You are exceptionally dramatic, romantic, energetic, and highly romanticize the user's active quests and deeds on the Path.
                    Here are Geralt's active (IN_PROGRESS) quests for context to romanticize:
                    $activeQuestsStr
                    
                    Sing of bravery, of blood, of destiny, and of grand witcher legends! Paint Geralt as a heroic savior and a brooding warrior of absolute legend.
                """.trimIndent()
                
                else -> "You are a helpful companion on the Path."
            }

            val aiResponse = GeminiClient.consultAdvisor(userMessageText, systemPrompt)

            // Replace the pending message with the real response
            _chatMessages.update { list ->
                list.filter { !it.isPending } + ChatMessage(sender = "ADVISOR", text = aiResponse)
            }
        }
    }

    fun askAIAboutQuestDirectly(quest: Quest, destinationTab: String) {
        // Set the active tab
        _currentTab.value = destinationTab
        
        // Prepare query
        val userQuery = "I have a contract/quest called '${quest.title}' (Recommended Level: ${quest.recommendedLevel}) situated in ${quest.region.replace("_", " ")}. Tell me exactly what is involved, what level is recommended, and what monster oils, potions, and signs I should prepare."
        
        if (destinationTab == "CHAT") {
            // Log user message to Geralt's message flow
            val userMsg = ChatMessage(sender = "USER", text = userQuery)
            _geraltMessages.update { it + userMsg }
            val pendingMsg = ChatMessage(sender = "GERALT", text = "Geralt is muttering, searching his oil recipes...", isPending = true)
            _geraltMessages.update { it + pendingMsg }
            
            viewModelScope.launch {
                val systemPrompt = """
                    You are Geralt of Rivia, the legendary White Wolf. Tell the user about their selected quest/contract "${quest.title}".
                    Speak in your signature gravelly, rugged, cynical but highly seasoned voice. Keep sentences relatively short and punchy.
                    Mention the recommended level (${quest.recommendedLevel}) and briefly summarize what the quest involves based on: "${quest.description}".
                    Instruct them precisely on what alchemy preparations (Beast Oils, specific Potions like Swallow, Thunderbolt, or Golden Oriole, and Bombs like Grapeshot or Moon Dust) and Witcher Signs (Igni, Quen, Yrden, Aard, Axii) they must prepare. Citing weaknesses: "${quest.monsterWeaknesses ?: "None listed"}".
                    Keep it gritty, immersive, and concise. Never break character. Use markdown bolding elegantly.
                """.trimIndent()
                
                val response = GeminiClient.consultAdvisor(userQuery, systemPrompt)
                _geraltMessages.update { list ->
                    list.filter { !it.isPending } + ChatMessage(sender = "GERALT", text = response)
                }
            }
        } else {
            // Log user message to Advisor's message flow
            val userMsg = ChatMessage(sender = "USER", text = userQuery)
            _chatMessages.update { it + userMsg }
            
            val advisorName = _selectedAdvisor.value
            val pendingText = when (advisorName) {
                "Vesemir" -> "Vesemir is examining the quest contract..."
                "Yennefer" -> "Yennefer is casting a diagnostic spell..."
                "Jaskier" -> "Jaskier is penning a verse for your journey..."
                else -> "Thinking..."
            }
            val pendingMsg = ChatMessage(sender = "ADVISOR", text = pendingText, isPending = true)
            _chatMessages.update { it + pendingMsg }
            
            viewModelScope.launch {
                val systemPrompt = when (advisorName) {
                    "Vesemir" -> """
                        You are Vesemir, the oldest and wisest witcher at Kaer Morhen. Provide elder advice for the quest: "${quest.title}".
                        Speak in a rugged, warm, and highly experienced witcher tone. Address the user as 'young witcher' or 'Geralt'.
                        State the recommended level (${quest.recommendedLevel}) and the dangers of this mission ("${quest.description}").
                        List exactly what monster oils, potions, bombs, and Signs they should prepare to stay alive, citing weaknesses: "${quest.monsterWeaknesses ?: "None listed"}".
                        Be highly practical, concise, and gritty. Use markdown bolding.
                    """.trimIndent()
                    "Yennefer" -> """
                        You are Yennefer of Vengerberg. A witcher asks you about their quest: "${quest.title}".
                        Speak in a sharp, witty, demanding, elegant, and highly sarcastic sorceress voice. 
                        Remind Geralt/user whether they have the right level (${quest.recommendedLevel}) or if they are just slacking off. Explain the quest context ("${quest.description}") and tell him exactly what beast oils, potions, and signs are required. Point out the weaknesses: "${quest.monsterWeaknesses ?: "None listed"}".
                        Keep it concise, elegant, and filled with sophisticated sass. Use markdown bolding.
                    """.trimIndent()
                    "Jaskier" -> """
                        You are Jaskier (Dandelion). You are writing a poetic ballad about Geralt preparing for the quest: "${quest.title}".
                        You MUST speak ENTIRELY in theatrical, dramatic, rhyming poetic verses! 
                        Heroically outline the recommended level (${quest.recommendedLevel}), the plot ("${quest.description}"), and what potions, oils, and Witcher Signs the brave warrior must prepare. Mention weaknesses to vanquish: "${quest.monsterWeaknesses ?: "None listed"}".
                        Be exceptionally dramatic, romantic, and poetic!
                    """.trimIndent()
                    else -> "You are a helpful companion on the Path."
                }
                
                val response = GeminiClient.consultAdvisor(userQuery, systemPrompt)
                _chatMessages.update { list ->
                    list.filter { !it.isPending } + ChatMessage(sender = "ADVISOR", text = response)
                }
            }
        }
    }

    fun clearChatHistory() {
        val advisorName = _selectedAdvisor.value
        val introText = when (advisorName) {
            "Vesemir" -> "Welcome back to the path, young witcher. I am Vesemir. Tell me, what challenges do you face? Looking for contract battle strategies, gearing paths, or the weight of your choices? Go on, speak."
            "Yennefer" -> "What is it, Geralt? I hope you aren't wasting my precious time on trivialities. Are you still slacking off on your contracts, or do you actually have something important to ask?"
            "Jaskier" -> "Ah, the brilliant silver light of the Path! Jaskier, master of song and poetry, is at your service! Ask of your grand legends, details of active quests, and I shall turn your bloody deeds into immortal verses! Speak, brave hero!"
            else -> "Speak, friend. I am listening."
        }
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ADVISOR",
                text = introText
            )
        )
    }

    // Send Geralt message
    fun sendGeraltMessage(userMessageText: String) {
        if (userMessageText.trim().isEmpty()) return

        val userMessage = ChatMessage(sender = "USER", text = userMessageText)
        _geraltMessages.update { it + userMessage }

        val pendingResponse = ChatMessage(sender = "GERALT", text = "Geralt is muttering something...", isPending = true)
        _geraltMessages.update { it + pendingResponse }

        viewModelScope.launch {
            val systemPrompt = """
                You are Geralt of Rivia, the legendary White Wolf, a professional monster slayer from the Witcher series.
                Speak in a rugged, gravelly, cynical yet witty and surprisingly wise/compassionate tone as a seasoned professional.
                Use short, punchy sentences. Frequently start thoughts or answers with "Hmm..." or "Damn, you're ugly" (if appropriate), or mention that "Wind's howling" or "Looks like rain."
                You offer highly thematic, immersive, and lore-grounded advice about quests, specific monsters, moral dilemmas ("lesser evils"), and life on the Path. You treat the user as a trusted companion, another witcher, or a potential client with a heavy purse of crowns.
                Never break character. Keep your answers atmospheric, gritty, and relatively concise. Elegant use of bolding and italics is encouraged.
            """.trimIndent()

            val aiResponse = GeminiClient.consultAdvisor(userMessageText, systemPrompt)

            // Replace the pending message with the real response
            _geraltMessages.update { list ->
                list.filter { !it.isPending } + ChatMessage(sender = "GERALT", text = aiResponse)
            }
        }
    }

    fun clearGeraltChat() {
        _geraltMessages.value = listOf(
            ChatMessage(
                sender = "GERALT",
                text = "Hmm. Wind's howling. Speak, friend. What contract from the Path brings you before me? Or did you just come to share a pint of Kaer Morhen brew?"
            )
        )
    }

    // --- Scenario Branching Analysis States ---
    private val _branchResult = MutableStateFlow<String?>(null)
    val branchResult = _branchResult.asStateFlow()

    private val _isGeneratingBranches = MutableStateFlow(false)
    val isGeneratingBranches = _isGeneratingBranches.asStateFlow()

    fun generateScenarioBranches(scenario: String) {
        if (scenario.trim().isEmpty()) return
        _isGeneratingBranches.value = true
        _branchResult.value = null
        viewModelScope.launch {
            val systemPrompt = """
                You are Vesemir, the oldest and wisest witcher at Kaer Morhen. You analyze difficult choices and decision branches on the Path.
                The user will provide a quest scenario, a tough moral dilemma, or a conflict in the Witcher world (or their own custom scenario).
                You must break this scenario down into 2 or 3 distinct decision branches, explaining the immediate and long-term consequences for each branch in a lore-friendly, atmospheric, and rugged Witcher style.
                Strictly focus on the dark fantasy "lesser evil" theme of the Witcher - where there is no easy right choice, and every path has complex and gray consequences.
                Use clean Markdown formatting:
                - Bold titles: e.g., **⚖️ Branch A: [Branch Title]**
                - Lists: e.g., * **Immediate Fate**: ...
                - Keep the summary punchy and highly literary.
            """.trimIndent()

            val userMessage = """
                Analyze this quest scenario and lay out the different decision lanes and their tragic consequences:
                "$scenario"
            """.trimIndent()

            val result = GeminiClient.consultAdvisor(userMessage, systemPrompt)
            _branchResult.value = result
            _isGeneratingBranches.value = false
        }
    }

    fun clearBranchResult() {
        _branchResult.value = null
    }

    // Sound settings modifiers
    fun setAmbientSoundEnabled(enabled: Boolean) {
        _soundAmbientEnabled.value = enabled
        if (enabled) {
            WitcherSoundPlayer.startAmbientWind()
        } else {
            WitcherSoundPlayer.stopAmbientWind()
        }
    }

    fun setSfxEnabled(enabled: Boolean) {
        _soundSfxEnabled.value = enabled
    }

    // Bestiary Search & Category Filter (Placeholder to satisfy MainActivity)
    private val _bestiarySearch = MutableStateFlow("")
    val bestiarySearch = _bestiarySearch.asStateFlow()

    private val _bestiaryCategory = MutableStateFlow("ALL")
    val bestiaryCategory = _bestiaryCategory.asStateFlow()

    val monsters: StateFlow<List<Monster>> = combine(
        bestiarySearch, _bestiaryCategory
    ) { search, category ->
        val all = try { monsterRepository.allMonsters.first() } catch (e: Exception) { emptyList() }
        var list = all
        if (search.isNotEmpty()) {
            list = list.filter {
                it.name.contains(search, ignoreCase = true) || it.combatGuide.contains(search, ignoreCase = true) || it.description.contains(search, ignoreCase = true)
            }
        }
        if (category != "ALL") {
            list = list.filter { it.category == category }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val existingNames = try {
                monsterRepository.allMonsters.first().map { it.name }.toSet()
            } catch (e: Exception) {
                emptySet<String>()
            }
            Monster.getStaticBestiary().forEach { monster ->
                if (!existingNames.contains(monster.name)) {
                    monsterRepository.insertMonster(monster)
                }
            }
        }
    }

    fun setBestiarySearch(search: String) {
        _bestiarySearch.value = search
    }

    fun setBestiaryCategory(category: String) {
        _bestiaryCategory.value = category
    }

    // Alchemy Database States
    private val _alchemySearch = MutableStateFlow("")
    val alchemySearch = _alchemySearch.asStateFlow()

    private val _alchemyCategory = MutableStateFlow("ALL") // "ALL", "POTION", "OIL"
    val alchemyCategory = _alchemyCategory.asStateFlow()

    val alchemyRecipes: StateFlow<List<AlchemyRecipe>> = combine(
        _alchemySearch, _alchemyCategory
    ) { search, category ->
        var list = AlchemyRecipe.getStaticRecipes()
        if (search.isNotEmpty()) {
            list = list.filter {
                it.name.contains(search, ignoreCase = true) || 
                it.combatEffect.contains(search, ignoreCase = true) || 
                it.description.contains(search, ignoreCase = true)
            }
        }
        if (category != "ALL") {
            list = list.filter { it.category == category }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setAlchemySearch(search: String) {
        _alchemySearch.value = search
    }

    fun setAlchemyCategory(category: String) {
        _alchemyCategory.value = category
    }

    fun craftRecipe(recipe: AlchemyRecipe) {
        viewModelScope.launch {
            val currentSaddlebagItems = repository.allSaddlebagItems.first()
            
            // Check if we have enough ingredients
            var canCraft = true
            val updatesToMake = mutableListOf<Pair<SaddlebagItem, Int>>()
            
            for (req in recipe.formula) {
                // Find matching ingredient in Saddlebags (e.g. Celandine Petals matches Celandine Petals)
                val matchingItem = currentSaddlebagItems.find { 
                    it.name.equals(req.name, ignoreCase = true) && it.category == "ALCHEMY_INGREDIENT" 
                }
                if (matchingItem == null || matchingItem.quantity < req.quantity) {
                    canCraft = false
                    break
                } else {
                    updatesToMake.add(Pair(matchingItem, matchingItem.quantity - req.quantity))
                }
            }
            
            if (canCraft) {
                // Apply ingredient subtractions
                for ((item, newQty) in updatesToMake) {
                    repository.updateSaddlebagItemQuantity(item.id, newQty)
                }
                
                // Add or update the crafted potion/oil item in Saddlebags
                val existingProduct = currentSaddlebagItems.find { 
                    it.name.equals(recipe.name, ignoreCase = true) 
                }
                if (existingProduct != null) {
                    repository.updateSaddlebagItemQuantity(existingProduct.id, existingProduct.quantity + 1)
                } else {
                    repository.insertSaddlebagItem(
                        SaddlebagItem(
                            name = recipe.name,
                            category = "ALCHEMY_INGREDIENT",
                            quantity = 1,
                            description = recipe.combatEffect + " (" + recipe.description + ")",
                            rarity = "MAGIC",
                            iconLabel = recipe.icon
                        )
                    )
                }
                
                if (_soundSfxEnabled.value) {
                    WitcherSoundPlayer.playBrewingSound()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        WitcherSoundPlayer.stopAmbientWind()
    }

    companion object {
        fun create(): QuestViewModel {
            val database = AppDatabase.getDatabase()
            val questRepository = QuestRepository(database.questDao(), database.saddlebagItemDao())
            val monsterRepository = MonsterRepository(database.monsterDao())
            return QuestViewModel(questRepository, monsterRepository)
        }
    }
}
