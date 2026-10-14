package com.example

/** Spoken line for a beast card: name, kind, weaknesses, and how to fight it. */
fun beastSpokenLine(name: String, kind: String, weaknesses: List<String>, howToFight: String): String {
    val weaknessLine = weaknesses.joinToString(", ")
    return "$name. $kind. Weaknesses: $weaknessLine. $howToFight"
}

/** Spoken line for a quest notice: title, region, and the one-line description. */
fun questNoticeSpokenLine(title: String, regionLabel: String, description: String): String {
    return "$title. $regionLabel. $description"
}

/** Spoken line for an open destiny choice and the result that follows it. */
fun destinyChoiceSpokenLine(choice: String, laterResult: String): String {
    return "$choice. $laterResult"
}
