package ru.skillbranch.devintensive.extensions

    fun String.truncate(newStringLength: Int = 16) : String {
        val newString = this.trim()
        return if (newString.length <= newStringLength) newString
                else newString.substring(0, newStringLength).trim() + "..."
    }

    fun String.stripHtml() : String {
        return this.replace( Regex("(<.*?>)|(&[^ а-я]{1,4}?;)"), "")
            .replace(Regex(" {2,}"), " ")
    }
