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
                "Dijkstra dies", "Roche, Ves, and Thaler live.",
                "A vassal Temeria", "Dijkstra dies. Temeria becomes a Nilfgaardian vassal, and Emhyr stays emperor.",
                ConsequenceSeverity.POSITIVE),
            branch("reason_dijkstra", "Side with Dijkstra", "Let Dijkstra finish Roche, Ves, and Thaler.",
                "The Temerians fall", "Roche, Ves, and Thaler die in the warehouse.",
                "Dijkstra holds the North", "Roche, Ves, and Thaler die. Dijkstra holds the North.",
                ConsequenceSeverity.NEGATIVE),
            branch("reason_walk", "Never do the assassination", "Refuse the plot and leave Radovid on the throne.",
                "The king lives", "Roche's conspiracy never strikes.",
                "Radovid wins the war", "Radovid wins and burns mages and non-humans.",
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
        "After the battle",
        "Ciri is raw from the fight at Kaer Morhen. How Geralt spends the hour with her is one of the five points.",
        listOf(
            branch("ciri_snowball", "I know what might lift your spirits", "Start a snowball fight in the courtyard.",
                "She laughs", "The fight in the snow pulls her out of the funeral dark.",
                "One positive point", "This choice is +1. There is no score counter. Three or more positive points, with the other four moments, and Ciri lives.",
                ConsequenceSeverity.POSITIVE),
            branch("ciri_drink", "Relax. You don't have to be good at everything", "Take her to drink instead of letting her be a child for an hour.",
                "The cup is bitter", "She drinks, and the grief stays sitting with her.",
                "One negative point", "This choice is -1. There is no score counter. Fewer than three positive points and Ciri dies in the White Frost.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "For the Advancement of Learning",
        "Keira's notes",
        "Keira Metz means to take Alexander's research to King Radovid.",
        listOf(
            branch("keira_morhen", "Ask her to Kaer Morhen", "Tell her the notes belong at the keep, with the people who will fight the Hunt.",
                "She agrees", "Keira comes to Kaer Morhen instead of Vizima.",
                "She lives", "She lives, saves Lambert during the battle, and later cures the Catriona plague.",
                ConsequenceSeverity.POSITIVE),
            branch("keira_radovid", "Let her go to Radovid", "Allow her to seek the king's patronage.",
                "She rides for Vizima", "Radovid does not reward her.",
                "A Final Kindness", "She is tortured and executed. That starts the quest A Final Kindness.",
                ConsequenceSeverity.NEGATIVE),
            branch("keira_fight", "Demand the notes", "Try to take the research by force on Fyke Isle.",
                "The fight starts", "Keira will not hand the notes over.",
                "Geralt kills her", "Geralt kills her on Fyke Isle. Lambert has no one to pull him out of the Hunt's fire.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Imperial Audience",
        "The road out of Witcher 2",
        "Emhyr's court already knows what Geralt did in Temeria, with the Scoia'tael, and with Letho.",
        listOf(
            branch("aryan_spare", "Spared Aryan La Valette", "Geralt did not kill Aryan at the castle of La Valette.",
                "Aryan lived", "The audience remembers a man who stayed his sword.",
                "A living heir", "Aryan La Valette survived the assault on his family's castle.",
                ConsequenceSeverity.POSITIVE),
            branch("aryan_kill", "Killed Aryan La Valette", "Geralt killed Aryan during the assault.",
                "Aryan died", "The court treats Geralt as the man who ended that line in the castle.",
                "No heir of that fight", "Aryan La Valette died by Geralt's hand.",
                ConsequenceSeverity.NEGATIVE),
            branch("side_roche", "Sided with Roche", "Geralt stood with Vernon Roche against the kingslayer hunt.",
                "Roche's path", "The audience knows Geralt chose the Temerian.",
                "Roche still answers", "Vernon Roche remains the ally that choice made.",
                ConsequenceSeverity.NEUTRAL),
            branch("side_iorveth", "Sided with Iorveth", "Geralt stood with Iorveth and the Scoia'tael.",
                "Iorveth's path", "The audience knows Geralt chose the elf.",
                "The Scoia'tael road", "Iorveth's war is the one Geralt walked.",
                ConsequenceSeverity.NEUTRAL),
            branch("letho_spare", "Spared Letho", "Geralt let the kingslayer leave alive.",
                "Letho walked", "The viper survived their last meeting.",
                "Ghosts of the Past", "Sparing Letho is what makes Ghosts of the Past exist in Velen.",
                ConsequenceSeverity.POSITIVE),
            branch("letho_kill", "Killed Letho", "Geralt killed Letho when they met again.",
                "Letho died", "There is no kingslayer left to find in a barn.",
                "No Ghosts of the Past", "Killing Letho means Ghosts of the Past never appears.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "It Takes Three to Tango",
        "Both of them",
        "This scene happens only if Geralt romanced both Triss and Yennefer.",
        listOf(
            branch("tango_both", "Face them together", "Triss and Yennefer already know about each other.",
                "The trick is sprung", "They tie him up and leave him with the knowledge that they compared notes.",
                "Both leave", "Both leave him. Now or Never and The Last Wish already held the love and the goodbye. This is what both at once costs.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Ugly Baby",
        "Emhyr's gold",
        "In Vizima, before the ship to Kaer Morhen, Emhyr offers payment for Ciri. This is not the choice of whether to bring her. That choice is The Isle of Mists.",
        listOf(
            branch("gold_refuse", "Refuse the payment", "Tell the emperor the child is not a debt he can settle with coin.",
                "The purse stays on the table", "Geralt takes Ciri onward without the reward.",
                "One positive point", "This choice is +1. There is no score counter.",
                ConsequenceSeverity.POSITIVE),
            branch("gold_take", "Take the gold", "Accept Emhyr's payment for finding his daughter.",
                "The coin is heavy", "Geralt leaves Vizima paid.",
                "One negative point", "This choice is -1. There is no score counter.",
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
    ),
    tree(
        "The Child of the Elder Blood",
        "Avallac'h's laboratory",
        "Ciri wants to wreck the elf's laboratory. This is the laboratory choice. It is not part of Blood on the Battlefield.",
        listOf(
            branch("lab_go", "Go for it", "Let her destroy the laboratory.",
                "The place comes apart", "She wrecks Avallac'h's laboratory.",
                "One positive point", "This choice is +1. There is no score counter.",
                ConsequenceSeverity.POSITIVE),
            branch("lab_calm", "Calm down", "Tell her to stop.",
                "She keeps the necklace", "She keeps the necklace instead of breaking the room.",
                "One negative point", "This choice is -1. There is no score counter.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Final Preparations",
        "The Lodge",
        "The sorceresses will see Ciri before the last battle. She can walk in alone.",
        listOf(
            branch("lodge_alone", "You'll do fine on your own", "Let her face the Lodge without Geralt in the room.",
                "She goes in alone", "The meeting is hers.",
                "One positive point", "This choice is +1. There is no score counter.",
                ConsequenceSeverity.POSITIVE),
            branch("lodge_with", "I'm coming with you", "Geralt goes into the meeting with her.",
                "He stands beside her", "The Lodge speaks to both of them.",
                "One negative point", "This choice is -1. There is no score counter.",
                ConsequenceSeverity.NEGATIVE)
        )
    ),
    tree(
        "Visit Skjall's Grave",
        "Hindarsfjall",
        "Ciri asks Geralt to come to Skjall's grave. The journal has no older title for this scene, so the entry uses the words she asks.",
        listOf(
            branch("skjall_yes", "Yeah, I'll go with you", "Go with her to the grave on Hindarsfjall.",
                "They stand at the stone", "She says what she needs to say, and he is there.",
                "One positive point", "This choice is +1. There is no score counter.",
                ConsequenceSeverity.POSITIVE),
            branch("skjall_no", "No time", "Tell her there is no time for the grave.",
                "The grave waits", "She does not get to take him there.",
                "One negative point", "This choice is -1. There is no score counter.",
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
