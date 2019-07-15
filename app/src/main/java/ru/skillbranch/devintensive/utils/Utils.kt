package ru.skillbranch.devintensive.utils

object Utils {

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.split(" ")

        val firstName = parts?.getOrNull(0)?.ifEmpty{ null }
        val lastName = parts?.getOrNull(1)?.ifEmpty{ null }

        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var nickname = ""

        payload.trim().forEach {
            nickname += when {
                it == ' ' -> divider
                dictionary.containsKey(it.toLowerCase()) ->
                    if(it.isUpperCase()) dictionary[it.toLowerCase()]?.capitalize() else dictionary[it]
                else -> it
            }
        }
        return nickname
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val firstLetter = firstName?.trim()?.firstOrNull()?.toUpperCase() ?: ""
        val secondLetter = lastName?.trim()?.firstOrNull()?.toUpperCase() ?: ""

        return "$firstLetter$secondLetter".ifEmpty { null }
    }

    private val dictionary = mapOf(
        'а' to "a",
        'б' to "b",
        'в' to "v",
        'г' to "g",
        'д' to "d",
        'е' to "e",
        'ё' to "e",
        'ж' to "zh",
        'з' to "z",
        'и' to "i",
        'й' to "i",
        'к' to "k",
        'л' to "l",
        'м' to "m",
        'н' to "n",
        'о' to "o",
        'п' to "p",
        'р' to "r",
        'с' to "s",
        'т' to "t",
        'у' to "u",
        'ф' to "f",
        'х' to "h",
        'ц' to "c",
        'ч' to "ch",
        'ш' to "sh",
        'щ' to "sh'",
        'ъ' to "",
        'ы' to "i",
        'ь' to "",
        'э' to "e",
        'ю' to "yu",
        'я' to "ya"
    )
}