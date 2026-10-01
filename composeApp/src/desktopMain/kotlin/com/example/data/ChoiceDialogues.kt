package com.example.data

internal fun choiceDialogues(): List<QuestDecisionTree> = listOf(
    tree(
        "Get Junior",
        "Whoreson Junior",
        "Whoreson has Dandelion. What Geralt does with him decides who still talks.",
        listOf(
            branch("junior_spare", "Let him live", "Leave Whoreson breathing so he can be used later.",
                "Whoreson talks", "He gives up what he knows about Dandelion's captors.",
                "A rat in the walls", "Dijkstra keeps a man he can still squeeze.",
                ConsequenceSeverity.NEUTRAL),
            branch("junior_kill", "Kill him", "End Whoreson in the room where he is holding court.",
                "The room goes quiet", "No one in that house bargains again.",
                "One less name", "Novigrad's gangs close over the gap, and Dijkstra loses a lever.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Reason of State",
        "Radovid's assassination",
        "Roche and Dijkstra both want the king dead. They do not want the same thing after.",
        listOf(
            branch("reason_roche", "Side with Roche", "Stop Dijkstra from murdering the Temerians.",
                "Dijkstra dies", "Roche and Thaler live, and Temeria has a chance under Nilfgaard.",
                "A vassal Temeria", "The north keeps a Temeria in name, ruled from Vizima with Emhyr's leave.",
                ConsequenceSeverity.POSITIVE),
            branch("reason_dijkstra", "Side with Dijkstra", "Let Dijkstra finish Roche, Ves, and Thaler.",
                "The Temerians fall", "Dijkstra takes the plot for himself.",
                "Redania without a king", "Dijkstra holds the north, and Roche is gone.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Where the Cat and Wolf Play...",
        "Gaetan",
        "A Cat School witcher slaughtered a village that tried to cheat him.",
        listOf(
            branch("gaetan_spare", "Let him go", "Hear him out and leave him the medallion he earned.",
                "Gaetan walks", "He leaves a diagram and a warning about the Path.",
                "Another witcher lives", "The village stays dead. Geralt does not add one more.",
                ConsequenceSeverity.NEUTRAL),
            branch("gaetan_kill", "Kill him", "Judge the slaughter and end it.",
                "Gaetan dies", "The village is avenged and no diagram changes hands.",
                "The Path stays narrow", "Geralt chooses the villagers over a brother of the trade.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Whatsoever a Man Soweth...",
        "Gaunter O'Dimm's riddle",
        "At the Temple of Lilvani, Geralt can solve the riddle or take what Gaunter offers.",
        listOf(
            branch("olgiers_save", "Solve the riddle", "Name the hour that was never sung and bind Gaunter to his own words.",
                "Olgierd is freed", "The pact breaks. Gaunter leaves without a soul.",
                "A man unmade and remade", "Olgierd lives, poorer and human, and Iris stays dead.",
                ConsequenceSeverity.POSITIVE),
            branch("olgiers_reward", "Take the reward", "Let Gaunter keep Olgierd and choose a prize.",
                "The pact is paid", "Olgierd's soul is collected on the spot.",
                "A wish with a hook", "Whatever Geralt takes from Gaunter is never only a gift.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Beyond Hill and Dale...",
        "The ribbon",
        "Syanna's land of fables will kill her unless Geralt brought the ribbon from the duchess.",
        listOf(
            branch("ribbon_bring", "Use the ribbon", "Give Syanna the ribbon that keeps the girl who was wronged from killing her.",
                "Syanna lives the tale", "She can leave the fables alive.",
                "A sister still breathing", "The night in Beauclair can end without both sisters dead.",
                ConsequenceSeverity.POSITIVE),
            branch("ribbon_skip", "Leave the ribbon", "Enter the tale without the one thing that stops the final girl.",
                "The tale bites", "Syanna does not walk out of the story.",
                "A duchess alone", "Anna Henrietta's ending is a grave no matter the speech.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Blood on the Battlefield",
        "The laboratory",
        "Ciri asks to destroy Avallac'h's laboratory alone.",
        listOf(
            branch("ciri_alone", "Let her go in alone", "Trust her with the wreck of the elf's work.",
                "She wrecks it herself", "Ciri comes out angry and certain.",
                "A woman who was trusted", "That trust is one of the things that lets her live past the White Frost.",
                ConsequenceSeverity.POSITIVE),
            branch("ciri_follow", "Follow her in", "Refuse to leave her with the laboratory.",
                "She is overruled", "Geralt walks in on the only thing she asked to do alone.",
                "A colder ending", "Treating her as a child here pushes her ending toward the tower.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Carnal Sins",
        "The Reverend",
        "The killer in Novigrad is preaching as he cuts.",
        listOf(
            branch("reverend_kill", "Kill Nathaniel", "Stop the Reverend before he chooses another sinner.",
                "The sermons end", "Novigrad loses a murderer and keeps the rumor.",
                "Priscilla's song", "The cabaret still has a wound, but the knife is gone.",
                ConsequenceSeverity.POSITIVE),
            branch("reverend_spare", "Let him go", "Decide the church will deal with its own.",
                "He walks", "Another body is likely.",
                "A sermon continues", "The Eternal Fire keeps a killer in a collar.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Possession",
        "The hym",
        "A hym is feeding on a jarl's hall, and the family is feeding it.",
        listOf(
            branch("hym_banish", "Banish the hym", "Name what the family did and drive the hym out.",
                "The hall clears", "The guilty stay guilty, and the haunting stops.",
                "A house that remembers", "Skellige keeps a jarl who has to live with the telling.",
                ConsequenceSeverity.POSITIVE),
            branch("hym_kill_host", "Kill the host", "Cut the hym out by killing the person it wears.",
                "The hym dies with them", "The hall is quiet because someone is dead.",
                "A blood price", "The clan pays for a secret with a life.",
                ConsequenceSeverity.NEGATIVE)
        )
    )
)

private fun tree(
    title: String,
    dilemma: String,
    description: String,
    paths: List<DecisionPath>
) = QuestDecisionTree(title, dilemma, description, paths)

private fun branch(
    id: String,
    choice: String,
    summary: String,
    immediateTitle: String,
    immediate: String,
    laterTitle: String,
    later: String,
    laterSeverity: ConsequenceSeverity
) = DecisionPath(
    id = id,
    choiceName = choice,
    summary = summary,
    immediateNode = DecisionNode(
        id = "${id}_now",
        type = NodeType.IMMEDIATE_CONSEQUENCE,
        title = immediateTitle,
        description = immediate,
        severity = ConsequenceSeverity.NEUTRAL
    ),
    longTermNode = DecisionNode(
        id = "${id}_later",
        type = NodeType.LONG_TERM_CONSEQUENCE,
        title = laterTitle,
        description = later,
        severity = laterSeverity
    ),
    logMessage = "$choice. $later"
)
