package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat

object ChatRepository {
    private val chats = CacheManager.loadChats()

    fun loadChats() : MutableLiveData<List<Chat>> {
        return chats
    }

    fun find(chatId: String): Chat? {
        val index = chats.value!!.indexOfFirst { it.id == chatId }
        return chats.value!!.getOrNull(index)
    }

    fun update(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        val index = chats.value!!.indexOfFirst { it.id == chat.id }
        if (index == -1) return
        copy[index] = chat
        chats.value = copy
    }
}