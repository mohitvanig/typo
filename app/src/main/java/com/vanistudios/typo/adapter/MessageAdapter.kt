package com.vanistudios.typo.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanistudios.typo.R
import com.vanistudios.typo.RoomActivity
import com.vanistudios.typo.pojo.CheckboxItem
import com.vanistudios.typo.pojo.ListNoteItem
import com.vanistudios.typo.pojo.Message
import kotlin.coroutines.coroutineContext

class MessageAdapter(private val messages: List<Message>,private var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val selectedItems = HashSet<Int>()
    private var messageList = ArrayList<Message> ()

    init {
        if(messages.isNotEmpty()){
            messageList = messages as ArrayList<Message>
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_MESSAGE -> {
                val view = inflater.inflate(R.layout.message_card, parent, false)
                MessageViewHolder(view)
            }
            TYPE_MESSAGE_WITH_LIST -> {
                val view = inflater.inflate(R.layout.message_with_list_card, parent, false)
                MessageWithListViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//        val message = messageList[position]
//        holder.messageText.text = message.text
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (selectedItems.contains(position)) {
            // Item is selected, modify its appearance
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            // Item is not selected, reset its appearance
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        when (holder.itemViewType) {
            TYPE_MESSAGE -> {
                val messageViewHolder = holder as MessageViewHolder
                messageViewHolder.messageText.text = message.text
            }
            TYPE_MESSAGE_WITH_LIST -> {
                val messageWithListViewHolder = holder as MessageWithListViewHolder
                //messageWithListViewHolder.messageText.text = message.text
                // Bind list items if needed
                val listNotesItems = message.listNotesItems
                messageWithListViewHolder.bindListItems(listNotesItems)
            }
        }

        holder.itemView.setOnLongClickListener {
            toggleItemSelection(position)
            true
        }
    }

    private fun toggleItemSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }
        notifyDataSetChanged()
    }

    fun deleteSelectedItems() {
        selectedItems.sortedDescending().forEach { position ->
            messageList.removeAt(position)
        }
        selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyDataSetChanged()
        //notifyItemInserted(messageList.size - 1)
    }

    companion object {
        private const val TYPE_MESSAGE = 0
        private const val TYPE_MESSAGE_WITH_LIST = 1
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.listNotesItems.isNotEmpty()) {
            TYPE_MESSAGE_WITH_LIST
        } else {
            TYPE_MESSAGE
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    inner class MessageWithListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val messageText: TextView = itemView.findViewById(R.id.message_text)
        val listItemsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)

        fun bindListItems(listNotesItems: List<ListNoteItem>) {
            // Bind the list items to the RecyclerView
            // You can use another adapter for the list items or handle it as per your requirement
            listItemsRecyclerView.layoutManager = LinearLayoutManager(context)
            var checkBoxItemList = ArrayList<CheckboxItem>()
            listNotesItems.forEach { n-> checkBoxItemList.add(CheckboxItem(n)) }
            var adapter = CheckboxListAdapter(checkBoxItemList)
            listItemsRecyclerView.adapter = adapter
        }
    }

    fun removeSelectedItems() {
        val selectedPositions = ArrayList(selectedItems)
        selectedItems.clear()
        selectedPositions.forEach {
            messageList.removeAt(it)
            notifyItemRemoved(it)
        }
    }


}