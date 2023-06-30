package com.vanistudios.typo.utilities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.vanistudios.typo.pojo.ListNoteItem
import com.vanistudios.typo.pojo.Message
import com.vanistudios.typo.pojo.Room
import java.lang.reflect.Type

class MessageDeserializer : JsonDeserializer<Message> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Message {
        val jsonObject = json?.asJsonObject

        val id = jsonObject?.get("id")?.asLong ?: 0
        val text = jsonObject?.get("text")?.asString ?: ""
        val createdAt = jsonObject?.get("createdAt")?.asString ?: ""
        val updatedAt = jsonObject?.get("updatedAt")?.asString ?: ""
        val roomId = jsonObject?.get("room_id")?.asLong ?: 0

        val listNoteItemsJsonArray = jsonObject?.getAsJsonArray("listNoteItems")
        val listNoteItems = mutableListOf<ListNoteItem>()

        listNoteItemsJsonArray?.forEach { item ->
            val itemId = item.asJsonObject.get("id").asLong
            val messageId = item.asJsonObject.get("message_id").asLong
            val content = item.asJsonObject.get("content").asString
            val isChecked = item.asJsonObject.get("isChecked").asBoolean
            val listType = item.asJsonObject.get("listType").asString

            val listNoteItem = ListNoteItem(content,isChecked,listType)
            listNoteItems.add(listNoteItem)
        }

        val room = Room(roomId)
        val message = Message(text, room,)
        message.listNotesItems = listNoteItems

        return message
    }
}
