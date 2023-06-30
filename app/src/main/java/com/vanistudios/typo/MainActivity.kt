package com.vanistudios.typo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
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
import com.vanistudios.typo.adapter.RoomAdapter
import com.vanistudios.typo.pojo.Message
import com.vanistudios.typo.pojo.Room
import com.vanistudios.typo.utilities.MessageDeserializer
import com.vanistudios.typo.utilities.TypoUtils
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.roomrecyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        var addNewButton : Button = findViewById<Button>(R.id.addNewRoomButton)

        fetchRoomsFromServer { rooms ->
            val adapter = RoomAdapter(rooms, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        addNewButton.setOnClickListener {
            var room = Room("untitled")
            addNewRoom(room, onSuccess = { room ->
                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra("room_id",  room.id)
                intent.putExtra("room_name",  room.room_name)
                this.startActivity(intent)
            }, onError = { error ->
                    // Handle network or JSON parsing error
                } );

        }
    }

    private fun fetchRoomsFromServer(callback: (List<Room>) -> Unit) {
        val url = TypoUtils().baseUrl+"/rooms/userid/2"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val gson = Gson()
                val type = object : TypeToken<List<Room>>() {}.type
                val rooms = gson.fromJson<List<Room>>(response, type) ?: emptyList()
                callback(rooms)
            },
            { error ->
                Log.e("MainActivity", "Error fetching rooms", error)
                callback(emptyList())
            })
        queue.add(stringRequest)
    }

    private fun addNewRoom(room:Room,onSuccess: (room: Room) -> Unit,
                           onError: (error: VolleyError) -> Unit) {
        val addRoomPostUrl = TypoUtils().baseUrl+"/rooms/create"
        val queue = Volley.newRequestQueue(this)
        val gson = Gson()
        val jsonString = gson.toJson(room)
        val request = JsonObjectRequest(Request.Method.POST, addRoomPostUrl, JSONObject(jsonString),
            { response ->
                try {
//                    val gson = GsonBuilder()
//                        .registerTypeAdapter(Room::class.java, MessageDeserializer())
//                        .create()
                    val room = Gson().fromJson(response.toString(), Room::class.java)
                    onSuccess(room)
                } catch (e: JsonSyntaxException) {
                    onError(VolleyError("JSON parsing error"))
                }
            },
            { error ->
                onError(error)
            })
        queue.add(request)
    }

}