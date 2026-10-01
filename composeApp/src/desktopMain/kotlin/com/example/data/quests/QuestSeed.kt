package com.example.data.quests

import com.example.data.Quest

internal fun quest(
    title: String,
    type: String,
    region: String,
    level: Int,
    description: String,
    questgiver: String,
    rewards: String,
    monsterWeaknesses: String? = null
) = Quest(
    title = title,
    type = type,
    region = region,
    recommendedLevel = level,
    description = description,
    questgiver = questgiver,
    rewards = rewards,
    status = "NOT_STARTED",
    monsterWeaknesses = monsterWeaknesses
)
