package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.humanizeDiff
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User (
    val id: String,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var rating: Int = 0,
    var respect: Int = 0,
    val lastVisit: Date? = null,
    var isOnline: Boolean = false
){
    fun toUserItem(): UserItem {
        val lastActivity = when{
            lastVisit == null -> "Еще ни разу не заходил"
            isOnline -> "online"
            else -> "Последний раз был ${lastVisit.humanizeDiff()}"
        }
        return UserItem(
            id,
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            Utils.toInitials(firstName, lastName),
            avatar,
            lastActivity,
            false,
            isOnline
        )
    }

    constructor(id: String, firstName: String?, lastName: String?) : this(id, firstName, lastName, avatar = null)

    constructor(id: String) : this(id, "John", "Wick")

    companion object Factory {
        private var lastId = -1
        fun makeUser(fullName: String?) : User {
            lastId++
            val (firstName, lastName) = Utils.parseFullName(fullName)
            return User(
                id = "$lastId",
                firstName = firstName,
                lastName = lastName
            )
        }
    }

    class Builder {
        private var id: String = "0"
        private var firstName: String? = null
        private var lastName: String? = null
        private var avatar: String? = null
        private var rating: Int = 0
        private var respect: Int = 0
        private var lastVisit: Date? = Date()
        private var isOnline: Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun firstName(firstName: String) = apply {this.firstName = firstName}
        fun lastName(lastName: String) = apply {this.lastName = lastName}
        fun avatar(avatar: String) = apply {this.avatar = avatar}
        fun rating(rating: Int) = apply {this.rating = rating}
        fun respect(respect: Int) = apply {this.respect = respect}
        fun lastVisit(lastVisit: Date) = apply {this.lastVisit = lastVisit}
        fun isOnline(isOnline: Boolean) = apply {this.isOnline = isOnline}

        fun build() = User(
            id,
            firstName,
            lastName,
            avatar,
            rating,
            respect,
            lastVisit,
            isOnline
        )
    }
}


