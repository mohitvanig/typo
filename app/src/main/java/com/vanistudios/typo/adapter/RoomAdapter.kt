package com.vanistudios.typo.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanistudios.typo.R
import com.vanistudios.typo.RoomActivity
import com.vanistudios.typo.pojo.Room
import java.util.*
import kotlin.random.Random.Default.nextInt

class RoomAdapter (private val rooms: List<Room>,private val context: Context) :
    RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.room_name_tv)
        val imageView : ImageView = itemView.findViewById(R.id.roomlogo)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val intent = Intent(context, RoomActivity::class.java)
                    intent.putExtra("room_id",  rooms[position].id.toString())
                    intent.putExtra("room_name",  rooms[position].room_name)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_card_potrait, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rooms[position]
        holder.textView.text = currentItem.room_name
        holder.imageView.setImageResource(getRandomLogo())
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    fun getRandomLogo():Int{
        var listlogo : Int = R.drawable.listlogo;
        var expenselogo : Int = R.drawable.expenselogo;
        var settinglogo : Int = R.drawable.settingslogo;
        var somethinglogo : Int = R.drawable.somethinglogo;
        val randomNumber = Random().nextInt(4) // Generates a random number between 0 and 3 (inclusive)

        return when (randomNumber) {
            0 -> listlogo
            1 -> expenselogo
            2 -> settinglogo
            3 -> somethinglogo
            else -> throw IllegalStateException("Invalid random number generated")
        }
    }


}