package com.vanistudios.typo

import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.BulletSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.vanistudios.typo.adapter.CheckboxListAdapter
import com.vanistudios.typo.adapter.MessageAdapter
import com.vanistudios.typo.pojo.CheckboxItem
import com.vanistudios.typo.pojo.ListNoteItem
import com.vanistudios.typo.pojo.Message
import com.vanistudios.typo.utilities.MessageDeserializer
import com.vanistudios.typo.utilities.TypoUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class RoomActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private lateinit var checkboxRecyclerView: RecyclerView
    private lateinit var checkboxAdapter: CheckboxListAdapter
    private val items: MutableList<CheckboxItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

//        window.apply {
//            statusBarColor = ContextCompat.getColor(this@RoomActivity, com.google.android.material.R.color.m3_ref_palette_black)
//        }
        checkboxRecyclerView = findViewById(R.id.recycler_view)
        checkboxRecyclerView.layoutManager = LinearLayoutManager(this)
        checkboxAdapter = CheckboxListAdapter(items)
        checkboxRecyclerView.adapter = checkboxAdapter

        var deleteButton : Button = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            adapter.removeSelectedItems()
        }



        val recyclerView : RecyclerView = findViewById(R.id.message_recycler_view)
        var layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        var sendButton : Button = findViewById(R.id.button_send)
        var enableList : Boolean = false

//        listButton.setOnClickListener {
//            enableList = !enableList
//            if (enableList) {
//                listButton.background = getDrawable(R.drawable.sendbutton_background_clicked)
//            } else{
//                listButton.background = getDrawable(R.drawable.sendbutton_background_grey)
//            }
//        }

        sendButton.isEnabled = false
        var msgEditText : EditText = findViewById(R.id.edit_text_message_input)

        var roomNameTextView: TextView = findViewById(R.id.room_name)
        roomNameTextView.text = getCurrentRoomName()

        lifecycleScope.launch {
            fetchMessagesByRoomid { messages ->
                adapter = MessageAdapter(messages,this@RoomActivity)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                makeAdapterScroll(adapter,recyclerView)
            }
        }

        var editText : EditText = findViewById(R.id.edit_text_message_input)
        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                recyclerView.doOnLayout {
                    makeAdapterScroll(adapter,recyclerView)
                }
                editText.requestFocus()
            }
        }

        editText.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.endsWith("\n") == true) {
                    val lines = p0?.split("\n")
                }
                if (p0 != null) {
                    if (p0.isNotEmpty()) {
                        if (p0?.endsWith("\n")) {
                            val item = CheckboxItem(p0.toString(), false)
                            items.add(item)
                            checkboxAdapter.notifyItemInserted(items.size - 1)
                            editText.text.clear()
                            checkboxRecyclerView.scrollToPosition(items.size - 1)
                            sendButton.setOnClickListener {
                                val msgString = msgEditText.text?.toString() ?: return@setOnClickListener
                                msgEditText.text.clear()
                                if (msgString.isEmpty()&&items.isEmpty()) {
                                    // Handle empty message error
                                    return@setOnClickListener
                                }
                                var checkBoxList = ArrayList<ListNoteItem>()
                                items.forEach { n-> checkBoxList.add(ListNoteItem(n))}
                                val message = Message(roomid = getCurrentRoomid().toLong(), text = "Untitled", listNoteItem = checkBoxList)
                                addMessageToServer(
                                    message = message,
                                    onSuccess = { message ->
                                        msgEditText.text.clear()
                                        items.clear()
                                        checkboxAdapter.notifyDataSetChanged()
                                        adapter.addMessage(message) // Add the new message to the adapter
                                        adapter.notifyDataSetChanged()
                                        makeAdapterScroll(adapter, recyclerView)
                                    },
                                    onError = { error ->
                                        print(error)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {
                if(!p0.isNullOrEmpty() || items.isNotEmpty()){
                    if(!sendButton.isEnabled){
                        sendButton.isEnabled = true
                    }
                    sendButton.background = getDrawable(R.drawable.sendbutton_background_grey)
                }else{
                    if(sendButton.isEnabled){
                        sendButton.isEnabled = false
                    }
                    sendButton.background = getDrawable(R.drawable.sendbutton_background_clicked)
                }
            }
        })


        sendButton.setOnClickListener {
            val msgString = msgEditText.text?.toString() ?: return@setOnClickListener
            msgEditText.text.clear()
            if (msgString.isEmpty()) {
                // Handle empty message error
                return@setOnClickListener
            }
            val message = Message(roomid = getCurrentRoomid().toLong(), text = msgString)
            addMessageToServer(
                message = message,
                onSuccess = { message ->
                    msgEditText.text.clear()
                    adapter.addMessage(message) // Add the new message to the adapter
                    adapter.notifyDataSetChanged()
                    //adapter.notifyItemInserted(adapter.messageList.size)
                    makeAdapterScroll(adapter, recyclerView)
                },
                onError = { error ->
                    // Handle network or JSON parsing error
                }
            )
        }

    }

    private fun fetchMessagesByRoomid (callback: (List<Message>) -> Unit){
        val queue = Volley.newRequestQueue(this)
        val url = TypoUtils().baseUrl+"/messages/roomx/"+getCurrentRoomid()
        val stringRequest = StringRequest( Request.Method.GET, url,
            { response ->
                var roomsJsonResponse = response
                //val gson = Gson()
                 val type = object : TypeToken<List<Message>>() {}.type
                //val messages = gson.fromJson<List<Message>>(roomsJsonResponse, type) ?: emptyList()
                val gson = GsonBuilder()
                    .registerTypeAdapter(Message::class.java, MessageDeserializer())
                    .create()

                val messages = gson.fromJson<List<Message>>(roomsJsonResponse, type) ?: emptyList()

                callback(messages)
            },
            { error ->
                Log.e("RoomActivity", "Error fetching messages", error)
                callback(emptyList())
            })
        queue.add(stringRequest)
    }

    fun getCurrentRoomid() : String{
        return intent.getStringExtra("room_id").toString()
    }

    fun getCurrentRoomName() : String{
        var room_name = intent.getStringExtra("room_name")?:"Untitled"
        return room_name
    }

    fun addMessageToServer(
        message: Message,
        onSuccess: (message: Message) -> Unit,
        onError: (error: VolleyError) -> Unit
    ) {
        val queue = Volley.newRequestQueue(this)
        val addMessagePostUrl = TypoUtils().baseUrl+"/messages"
        val gson = Gson()
        val jsonString = gson.toJson(message)
        val request = JsonObjectRequest(Request.Method.POST, addMessagePostUrl, JSONObject(jsonString),
            { response ->
                try {
                    val gson = GsonBuilder()
                        .registerTypeAdapter(Message::class.java, MessageDeserializer())
                        .create()
                    val message = gson.fromJson(response.toString(), Message::class.java)
                    onSuccess(message)
                } catch (e: JsonSyntaxException) {
                    onError(VolleyError("JSON parsing error"))
                }
            },
            { error ->
                onError(error)
            })
        queue.add(request)
    }

    fun makeAdapterScroll(adapter : MessageAdapter,recyclerView:RecyclerView){
        if(adapter.itemCount!=0){
            // Once layout and measurement is complete, scroll to the last position
            recyclerView.smoothScrollToPosition(adapter.itemCount + 1)
        }
    }
}