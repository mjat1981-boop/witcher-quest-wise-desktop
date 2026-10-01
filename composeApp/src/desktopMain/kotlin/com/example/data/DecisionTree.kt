package com.example.data

enum class NodeType {
    DECISION,
    IMMEDIATE_CONSEQUENCE,
    LONG_TERM_CONSEQUENCE
}

enum class ConsequenceSeverity {
    POSITIVE,  // Golden / Green
    NEUTRAL,   // Silver / Gray
    NEGATIVE   // Red / Tragic
}

data class DecisionNode(
    val id: String,
    val type: NodeType,
    val title: String,
    val description: String,
    val flavorNote: String? = null,
    val severity: ConsequenceSeverity = ConsequenceSeverity.NEUTRAL
)

data class DecisionPath(
    val id: String,
    val choiceName: String,
    val summary: String,
    val immediateNode: DecisionNode,
    val longTermNode: DecisionNode,
    val logMessage: String // Message logged to the quest notes when selected
)

data class QuestDecisionTree(
    val questTitle: String,
    val dilemmaTitle: String,
    val description: String,
    val paths: List<DecisionPath>
) {
    companion object {
        fun getTreeForQuest(title: String): QuestDecisionTree? {
            return (registry + choiceDialogues()).find { it.questTitle.equals(title, ignoreCase = true) }
        }

        fun getAllTrees(): List<QuestDecisionTree> {
            return registry + choiceDialogues()
        }

        private val registry = listOf(
            // 1. THE BLOODY BARON / FAMILY MATTERS (The Whispering Hillock)
            QuestDecisionTree(
                questTitle = "Family Matters",
                dilemmaTitle = "The Whispering Hillock Spirit",
                description = "Deep in Crow's Perch and Crookback Bog, Geralt must resolve the fate of a dark ancient spirit imprisoned beneath an old knotty tree.",
                paths = listOf(
                    DecisionPath(
                        id = "hillock_free",
                        choiceName = "🟢 Free the Ancient Spirit",
                        summary = "Trust the spirit inside the hillock and perform the ritual to free it into a dark stallion.",
                        immediateNode = DecisionNode(
                            id = "hillock_free_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "The Orphans of Crookback Bog are Saved",
                            description = "The wild spirit gallops into Crookback Bog as a black beauty horse, rescuing the innocent orphans from the clutches of the sinister Crones.",
                            flavorNote = "Requires: Raven Feather and Black Horse horse bones.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "hillock_free_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Tragedy at Crow's Perch",
                            description = "Furious, the Crones turn Anna into a monstrous Water Hag. Geralt lifts the curse, but she dies in her family's arms. The Bloody Baron, consumed by grief, hangs himself in the courtyard. Downwarren is completely razed by the vengeful spirit.",
                            flavorNote = "🪦 Tragic Outfall: Baron dies, Downwarren destroyed.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        logMessage = "Decision Path Chosen: Free the Whispering Hillock. Saved the orphans from the Crones, but resulted in Anna's tragic corruption and death, leading to the Bloody Baron's suicide at Crow's Perch."
                    ),
                    DecisionPath(
                        id = "hillock_slay",
                        choiceName = "🔴 Slay the Ancient Spirit",
                        summary = "Deem the spirit untrustworthy, slaying it or tricking it into a trap during the ritual.",
                        immediateNode = DecisionNode(
                            id = "hillock_slay_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "The Spirit is Exterminated",
                            description = "Geralt battles and destroys the rooted heart of the spirit. The orphans of Crookback Bog are subsequently captured and eaten by the Crones.",
                            flavorNote = "Combat: Fight the Heart's insectoid spawns.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "hillock_slay_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Shattered Peace & Survival",
                            description = "Anna is spared the Hag curse, but loses her mind with grief over the children. The Bloody Baron survives and resolves to carry Anna deep into the Blue Mountains in search of a legendary master healer. Downwarren is left standing.",
                            flavorNote = "🌻 Somber Survival: Baron lives, Downwarren is safe.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Slay the Whispering Hillock. The orphans were lost to the Crones, but Anna survived her physical curse. The Baron remains alive and has broken his drinking to seek a healer in the Blue Mountains."
                    )
                )
            ),

            // 1b. Ladies of the Wood (Matches also)
            QuestDecisionTree(
                questTitle = "Ladies of the Wood",
                dilemmaTitle = "The Whispering Hillock Spirit",
                description = "Deep in Crow's Perch and Crookback Bog, Geralt must resolve the fate of a dark ancient spirit imprisoned beneath an old knotty tree.",
                paths = listOf(
                    DecisionPath(
                        id = "ladies_free",
                        choiceName = "🟢 Free the Ancient Spirit",
                        summary = "Trust the spirit inside the hillock and perform the ritual to free it into a dark stallion.",
                        immediateNode = DecisionNode(
                            id = "ladies_free_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "The Orphans of Crookback Bog are Saved",
                            description = "The wild spirit gallops into Crookback Bog as a black beauty horse, rescuing the innocent orphans from the clutches of the sinister Crones.",
                            flavorNote = "Requires: Raven Feather and Black Horse horse bones.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "ladies_free_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Tragedy at Crow's Perch",
                            description = "Furious, the Crones turn Anna into a monstrous Water Hag. Geralt lifts the curse, but she dies in her family's arms. The Bloody Baron, consumed by grief, hangs himself in the courtyard. Downwarren is completely razed by the vengeful spirit.",
                            flavorNote = "🪦 Tragic Outfall: Baron dies, Downwarren destroyed.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        logMessage = "Decision Path Chosen: Free the Whispering Hillock. Saved the orphans from the Crones, but resulted in Anna's tragic corruption and death, leading to the Bloody Baron's suicide at Crow's Perch."
                    ),
                    DecisionPath(
                        id = "ladies_slay",
                        choiceName = "🔴 Slay the Ancient Spirit",
                        summary = "Deem the spirit untrustworthy, slaying it or tricking it into a trap during the ritual.",
                        immediateNode = DecisionNode(
                            id = "ladies_slay_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "The Spirit is Exterminated",
                            description = "Geralt battles and destroys the rooted heart of the spirit. The orphans of Crookback Bog are subsequently captured and eaten by the Crones.",
                            flavorNote = "Combat: Fight the Heart's insectoid spawns.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "ladies_slay_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Shattered Peace & Survival",
                            description = "Anna is spared the Hag curse, but loses her mind with grief over the children. The Bloody Baron survives and resolves to carry Anna deep into the Blue Mountains in search of a legendary master healer. Downwarren is left standing.",
                            flavorNote = "🌻 Somber Survival: Baron lives, Downwarren is safe.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Slay the Whispering Hillock. The orphans were lost to the Crones, but Anna survived her physical curse. The Baron remains alive and has broken his drinking to seek a healer in the Blue Mountains."
                    )
                )
            ),

            // 2. A TOWERFUL OF MICE
            QuestDecisionTree(
                questTitle = "A Towerful of Mice",
                dilemmaTitle = "The Plague Maiden of Fyke Island",
                description = "Geralt must decide whether to trust the melancholic ghost of Anabelle, who seeks to have her bones buried on the mainland by her lover Graham.",
                paths = listOf(
                    DecisionPath(
                        id = "mice_trust",
                        choiceName = "🟢 Trust Anabelle & Take Bones",
                        summary = "Believe Anabelle's tragic story and agree to take her mortal bones to her former lover Graham on the mainland.",
                        immediateNode = DecisionNode(
                            id = "mice_trust_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Tragic Death of Graham",
                            description = "Geralt delivers the bones. Graham buries them. However, Anabelle reveals her true form as a horrific Pesta (Plague Maiden), brutally murders Graham, and sweeps back into Velen.",
                            flavorNote = "☠️ Betrayal: Trusting a Pesta leads to death.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "mice_trust_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Plague Escapes in Velen",
                            description = "Anabelle's soul is not at rest; she roams Velen as a highly infectious vector, spreading pestilence and lethal diseases across villages, leaving a trail of misery.",
                            flavorNote = "🧪 Alchemy hint: Golden Oriole cannot cure global plague.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        logMessage = "Decision Path Chosen: Trusted Anabelle and transported her bones. Resulted in Graham's gruesome demise and let a dangerous Plague Maiden loose to contaminate Velen."
                    ),
                    DecisionPath(
                        id = "mice_confront",
                        choiceName = "🔴 Confront Ghost & Bring Graham",
                        summary = "Recognize Anabelle as a specter. Refuse to take the bones, forcing the truth and bringing Graham to the tower.",
                        immediateNode = DecisionNode(
                            id = "mice_confront_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "True Love's Final Kiss",
                            description = "Geralt guides Graham directly to the haunted laboratory tower. Graham proves his undying love by passionately kissing Anabelle's ghastly form, breaking the curse, but dying peacefully in the act.",
                            flavorNote = "Oil preference: Apply Specter Oil before ascending.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "mice_confront_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Peace Restored to Fyke Isle",
                            description = "The curse is completely dissolved and Anabelle's soul is finally at peace. Fyke Island is cleansed of its specter infestation. Keira Metz can safely secure her cure research.",
                            flavorNote = "🕊️ Clean ending: Soul freed, plague stopped.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Confronted Anabelle and brought Graham to Fyke Isle. The curse was lifted through Graham's final sacrifice, putting Anabelle's soul to rest, cleansing the island."
                    )
                )
            ),

            // 3. THE LAST WISH
            QuestDecisionTree(
                questTitle = "The Last Wish",
                dilemmaTitle = "A Life on the Path or Silent Retirement",
                description = "Once the Djinn's magical bond is destroyed, Geralt and Yennefer are left with their raw feelings. Geralt must decide his declaration.",
                paths = listOf(
                    DecisionPath(
                        id = "yen_love",
                        choiceName = "🟢 Confess Love: \"I still love you\"",
                        summary = "Affirm that the magic was not responsible for your affection and confess your deep love to Yennefer on the ship.",
                        immediateNode = DecisionNode(
                            id = "yen_love_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Bound Under Deep Skies",
                            description = "The magical pressure is gone, but the spark is completely unchanged. Geralt and Yennefer share an exceptionally warm, intimate embrace on the snowy mountain peak.",
                            flavorNote = "Romance locked: Yennefer of Vengerberg.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "yen_love_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Retirement in Kovir's Border",
                            description = "After the war, Geralt retires with Yennefer in a peaceful cottage snug in Kovir. They spend quiet, warm mornings, away from politics and coin contracts, living happily ever after.",
                            flavorNote = "🏡 Peaceful destiny with Yennefer.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Confessed love to Yennefer in \"The Last Wish\". Solidified Geralt's romance with Yennefer, aiming for a peaceful retirement together."
                    ),
                    DecisionPath(
                        id = "yen_refuse",
                        choiceName = "🔴 Break Connection: \"The magic is gone\"",
                        summary = "Confess to Yennefer that without the Djinn's influence, you no longer feel the same romantic connection.",
                        immediateNode = DecisionNode(
                            id = "yen_refuse_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "A Cold Separation",
                            description = "A deep silence falls over the ship. Yennefer is visibly heartbroken, recognizing the genuine magic between them is dead. They part ways as simple companions.",
                            flavorNote = "Romance broken with Yennefer.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "yen_refuse_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Triss Romance / The Path Alone",
                            description = "Opens the door to pursue a cozy life in Kovir with Triss Merigold, or remain a lone wolf wandering the dusty trails of the continent, old, grizzled, and alone.",
                            flavorNote = "🐺 Lone wolf or alternative romance path.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        logMessage = "Decision Path Chosen: Declined Yennefer's affection. Stated the magic had vanished, allowing Geralt to walk the Path alone or seek alternative company."
                    )
                )
            ),

            // 4. SCENES FROM A MARRIAGE
            QuestDecisionTree(
                questTitle = "Scenes From a Marriage",
                dilemmaTitle = "The Tragedy of Iris's Painted Rose",
                description = "Deep in the Painted world of Iris's memory, Geralt must decide whether to remove the Violet Rose, her final anchoring tether to existence.",
                paths = listOf(
                    DecisionPath(
                        id = "rose_take",
                        choiceName = "🟢 Take the Violet Rose",
                        summary = "Fulfill Olgierd's contract literally. Take the rose from Iris von Everec.",
                        immediateNode = DecisionNode(
                            id = "rose_take_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Iris Fades into Peace",
                            description = "Releases her from her purgatory world, but completely destroys the painted pocket dimension.",
                            flavorNote = "Contract met: You have the physical violet rose.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "rose_take_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "The Debt Cleansed",
                            description = "Armed with the real, physical rose, Geralt can complete the final task for Olgierd von Everec, releasing Olgierd from his cold stagnation.",
                            flavorNote = "⚔️ Ready for the final chapter.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Take the rose from Iris von Everec. Releases her from her purgatory world, but completely destroys the painted pocket dimension."
                    ),
                    DecisionPath(
                        id = "rose_leave",
                        choiceName = "🔴 Leave the Violet Rose",
                        summary = "Refuse to take the rose, leaving Iris to exist in her memory world.",
                        immediateNode = DecisionNode(
                            id = "rose_leave_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Sanctuary Restored",
                            description = "Keeps Iris intact inside her memory world, but you must find an alternate way to fulfill Olgierd's third wish using a replica.",
                            flavorNote = "Geralt cuts out a portrait canvas of Iris holding the rose.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        longTermNode = DecisionNode(
                            id = "rose_leave_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "A Picture of Sorrow",
                            description = "Geralt presents the canvas replica to Olgierd instead. Olgierd is deeply struck by his wife's eternal trauma, recognizing the tragic gravity of his emotionless choice.",
                            flavorNote = "💔 Somber: Olgierd confronts his coldness.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        logMessage = "Decision Path Chosen: Leave the rose with Iris. Keeps Iris intact inside her memory world, but you must find an alternate way to fulfill Olgierd's third wish using a replica."
                    )
                )
            ),

            // 5. KING'S GAMBIT (The Ruler of Skellige)
            QuestDecisionTree(
                questTitle = "King's Gambit",
                dilemmaTitle = "Sovereignty of the Isles",
                description = "During a bloody massacre at Castle Kaer Trolde, Geralt must decide whom to support, determining the future of the Skellige Isles.",
                paths = listOf(
                    DecisionPath(
                        id = "skellige_cerys",
                        choiceName = "🟢 Help Cerys Investigate",
                        summary = "Join Cerys to launch a meticulous investigation into the feast massacre, uncovering the dark conspiracy.",
                        immediateNode = DecisionNode(
                            id = "skellige_cerys_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Birna Bran is Unmasked",
                            description = "Cerys unmasks Birna Bran as the true poisoner. Birna is sentenced to be chained to a rocky shore to die of exposure.",
                            flavorNote = "No combat required. Unlocks the coronation ceremony.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "skellige_cerys_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "The Golden Age of Skellige",
                            description = "Cerys is crowned Queen of Skellige. She ushers in a golden era of internal unity, peace, and trade, refusing to needlessly bleed Skellige in Nilfgaardian wars.",
                            flavorNote = "👑 Queen Cerys rules: Stronger Skellige allies.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Helped Cerys. Exposed Birna Bran and coronated Cerys an Craite as the Queen of Skellige, securing a prosperous peace."
                    ),
                    DecisionPath(
                        id = "skellige_hjalmar",
                        choiceName = "🔴 Join Hjalmar's Raid",
                        summary = "Accompany Hjalmar on an immediate blood-revenge raid on the Vildkaarl clan village who engineered the bear-shifting feast.",
                        immediateNode = DecisionNode(
                            id = "skellige_hjalmar_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "The Vildkaarls Exterminated",
                            description = "Geralt and Hjalmar storm the village, slaying the druids and berserkers. Although they get revenge, the true puppeteer, Birna, escapes justice.",
                            flavorNote = "Heavy Combat: Slay the bear-shifting berserkers.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "skellige_hjalmar_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Continuous War & Raids",
                            description = "Hjalmar becomes King. He initiates endless raids against the Nilfgaardian Empire, leading to high Skelligan casualties and keeping Skellige in eternal conflict.",
                            flavorNote = "⚔️ King Hjalmar rules: Constant imperial warfare.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        logMessage = "Decision Path Chosen: Joined Hjalmar. Slew the berserker clan, leading to Hjalmar's coronation as King. Skellige remains trapped in endless blood raids against Nilfgaard."
                    ),
                    DecisionPath(
                        id = "skellige_neither",
                        choiceName = "Help neither",
                        summary = "Refuse both Cerys's investigation and Hjalmar's raid.",
                        immediateNode = DecisionNode(
                            id = "skellige_neither_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "The feast stays unresolved",
                            description = "Neither an Craite child gets Geralt's sword or his questions.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "skellige_neither_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Svanrige takes the throne",
                            description = "Svanrige Bran takes the throne and rules Skellige as a dictator.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        logMessage = "Helped neither Cerys nor Hjalmar. Svanrige Bran takes the throne and rules as a dictator."
                    )
                )
            ),

            // 6. NOW OR NEVER (Triss Romance)
            QuestDecisionTree(
                questTitle = "Now or Never",
                dilemmaTitle = "Triss's Departure from Novigrad",
                description = "As the mages escape Novigrad by boat, Geralt must decide whether to profess his love to Triss Merigold or let her leave for Kovir.",
                paths = listOf(
                    DecisionPath(
                        id = "triss_love",
                        choiceName = "🟢 Confess Love: \"I love you\"",
                        summary = "Beg Triss to stay behind on the Novigrad harbor docks, declaring your absolute romantic devotion.",
                        immediateNode = DecisionNode(
                            id = "triss_love_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "A Warm Return at the Lighthouse",
                            description = "Triss boards the ship, but walks back onto the harbor. They share a passionate night together inside the lighthouse overlook.",
                            flavorNote = "Romance locked: Triss Merigold. Warning: Avoid double-romancing Yennefer.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "triss_love_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Cozy Life in Kovir",
                            description = "Geralt moves to Kovir with Triss, where she works as the royal advisor. Geralt takes occasional minor contracts, living in luxury and deep peaceful comfort.",
                            flavorNote = "🏡 Sunny Kovir retirement with Triss.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Declared love to Triss in \"Now or Never\". Solidified Geralt's romance with Triss, heading towards a cozy life in Kovir."
                    ),
                    DecisionPath(
                        id = "triss_leave",
                        choiceName = "🔴 Let Her Go: \"Farewell, Triss\"",
                        summary = "Encourage Triss to lead the mages safely to Kovir, wishing her the best without a romantic confession.",
                        immediateNode = DecisionNode(
                            id = "triss_leave_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Silent Ship Sails Out",
                            description = "Triss sails out on the ship towards Kovir with a sad, final smile, parting ways with Geralt as a dedicated companion and friend.",
                            flavorNote = "Romance severed with Triss Merigold.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        longTermNode = DecisionNode(
                            id = "triss_leave_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Wandering the Path / Yennefer Path",
                            description = "Keeps the door fully open to pursue Yennefer, or live a lone wolf lifestyle wandering the trails from village to village.",
                            flavorNote = "🐺 Lone wolf or alternative romance path.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        logMessage = "Decision Path Chosen: Bid farewell to Triss Merigold. Allowed her to lead the mages to safety, leaving Geralt free to pursue Yennefer of Vengerberg or the lone Path."
                    )
                )
            ),

            // 7. THE NIGHT OF LONG FANGS (Vampire Siege choice)
            QuestDecisionTree(
                questTitle = "The Night of Long Fangs",
                dilemmaTitle = "Search for Syanna vs Meet the Unseen Elder",
                description = "As vampires invade Beauclair, Geralt must decide whether to save Syanna in the fairytale domain or consult the ancient Unseen Elder vampire.",
                paths = listOf(
                    DecisionPath(
                        id = "unseen_elder",
                        choiceName = "🟢 Seek Out the Unseen Elder",
                        summary = "Seek out the Unseen Elder vampire.",
                        immediateNode = DecisionNode(
                            id = "unseen_elder_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "A Dangerous Audience",
                            description = "Forces a dangerous meeting with ancient vampire nobility, locking you into a darker ending path for Toussaint.",
                            flavorNote = "Warning: The Elder kills instantly if disobeyed.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "unseen_elder_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Dark Destiny Looming",
                            description = "Guarantees a confrontation with Dettlaff but blocks the possibility of achieving a peaceful sisterly reconciliation.",
                            flavorNote = "🪓 Tragic outcome for the royal sisters.",
                            severity = ConsequenceSeverity.NEGATIVE
                        ),
                        logMessage = "Decision Path Chosen: Seek out the Unseen Elder vampire. Forces a dangerous meeting with ancient vampire nobility, locking you into a darker ending path for Toussaint."
                    ),
                    DecisionPath(
                        id = "find_syanna",
                        choiceName = "🔴 Go Find Syanna in Fairyland",
                        summary = "Go find Syanna in the Land of a Thousand Fables.",
                        immediateNode = DecisionNode(
                            id = "find_syanna_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "A Fantastic Journey",
                            description = "Opens an interactive fairytale dream world, giving you a chance to save both sisters and achieve a peaceful ending.",
                            flavorNote = "Explore a whimsical, corrupted storybook world.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "find_syanna_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Sovereign Sisterly Unity",
                            description = "Paves the path to saving Syanna, challenging Dettlaff directly, and seeking a warm, happy reconciliation at Beauclair.",
                            flavorNote = "🌻 Peaceful ending: Save Anna Henrietta and Syanna.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Go find Syanna in the Land of a Thousand Fables. Opens an interactive fairytale dream world, giving you a chance to save both sisters and achieve a peaceful ending."
                    )
                )
            ),

            // 8. THE ISLE OF MISTS (Ciri's Fate Decision)
            QuestDecisionTree(
                questTitle = "The Isle of Mists",
                dilemmaTitle = "Ciri's Future Career & Freedom Paths",
                description = "Choose whether to present Ciri directly to her biological imperial father or shelter her to develop her as a wildcard Witcher.",
                paths = listOf(
                    DecisionPath(
                        id = "ciri_emperor",
                        choiceName = "🟢 Visit the Emperor With Ciri",
                        summary = "Take Ciri to see her father, Emperor Emhyr, but refuse his coin payment.",
                        immediateNode = DecisionNode(
                            id = "ciri_emperor_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Imperial Succession Path Set",
                            description = "Puts Ciri on the path to becoming Empress of Nilfgaard.",
                            flavorNote = "Refusing payment proves Geralt has purely fatherly affection.",
                            severity = ConsequenceSeverity.NEUTRAL
                        ),
                        longTermNode = DecisionNode(
                            id = "ciri_emperor_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Empress Ciri of Nilfgaard",
                            description = "Three or more positive points and Ciri lives. Fewer, and she dies in the White Frost. If she lives and Geralt took her to Emhyr, she becomes Empress.",
                            flavorNote = "👑 Empress Ciri: Melancholy but noble duty.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Take Ciri to see her father, Emperor Emhyr, but refuse his coin payment. Puts Ciri on the path to becoming Empress of Nilfgaard."
                    ),
                    DecisionPath(
                        id = "ciri_witcher",
                        choiceName = "🔴 Go Straight to Velen/Novigrad",
                        summary = "Bring Ciri straight to Velen / Novigrad without visiting the Emperor.",
                        immediateNode = DecisionNode(
                            id = "ciri_witcher_imm",
                            type = NodeType.IMMEDIATE_CONSEQUENCE,
                            title = "Wild Wolf Freedom",
                            description = "Keeps her focused on the path of becoming a free, independent Witcher.",
                            flavorNote = "Ciri remains unburdened by regional royal politics.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        longTermNode = DecisionNode(
                            id = "ciri_witcher_lt",
                            type = NodeType.LONG_TERM_CONSEQUENCE,
                            title = "Witcher Ciri on the Path",
                            description = "Three or more positive points and Ciri lives. Fewer, and she dies in the White Frost. If she lives and Geralt did not take her to Emhyr, she becomes a Witcher.",
                            flavorNote = "🐺 Witcher Ciri: Pure joy, liberating freedom.",
                            severity = ConsequenceSeverity.POSITIVE
                        ),
                        logMessage = "Decision Path Chosen: Bring Ciri straight to Velen / Novigrad without visiting the Emperor. Keeps her focused on the path of becoming a free, independent Witcher."
                    )
                )
            )
        )
    }
}
