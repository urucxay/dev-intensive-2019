package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils

data class Profile(
    val firstName: String,
    val lastName: String,
    val about: String,
    val repository: String,
    val rating: Int = 0,
    val respect: Int = 0
) {
    val fullName = "$firstName $lastName"
    val nickName = Utils.transliteration(fullName, "_")
    val initials = Utils.toInitials(firstName, lastName) ?: ""
    val rank = "Junior Android Developer"

    fun toMap(): Map<String, Any> = mapOf(
        "initials" to initials,
        "nickName" to nickName,
        "rank" to rank,
        "firstName" to firstName,
        "lastName" to lastName,
        "about" to about,
        "repository" to repository,
        "rating" to rating,
        "respect" to respect
    )
}