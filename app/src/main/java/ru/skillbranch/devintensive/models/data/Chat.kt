package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount(): Int {
        return messages.size
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date {
        return messages.lastOrNull()?.date ?: Date()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String?, String?> =
        when (val lastMessage = messages.lastOrNull()) {
            is TextMessage -> lastMessage.text to lastMessage.from.firstName
//            lastMessage as TextMessage -> lastMessage.text to lastMessage.from.firstName
            else -> "Сообщений нет" to ""
        }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        val user = members.first()
        return if (isSingle()) {
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                Utils.toInitials(user.firstName, null) ?: "??",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }
//    fun toChatItem(): ChatItem {
//        val user = members.first()
//        return when {
//            members.isEmpty() -> ChatItem(
//
//            )
//            members.size == 1 -> ChatItem(
//                id,
//                user.avatar,
//                Utils.toInitials(user.firstName, user.lastName) ?: "??",
//                "${user.firstName ?: ""} ${user.lastName ?: ""}",
//                lastMessageShort().first,
//                unreadableMessageCount(),
//                lastMessageDate()?.shortFormat(),
//                user.isOnline)
//            else -> ChatItem(
//                id,
//                null,
//                Utils.toInitials(user.firstName, null) ?: "??",
//                title,
//                lastMessageShort().first,
//                unreadableMessageCount(),
//                lastMessageDate()?.shortFormat(),
//                false,
//                ChatType.GROUP,
//                lastMessageShort().second)
//        }
//}


}


enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}



