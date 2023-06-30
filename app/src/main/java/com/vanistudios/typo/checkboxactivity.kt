package com.vanistudios.typo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanistudios.typo.adapter.CheckboxListAdapter
import com.vanistudios.typo.pojo.CheckboxItem

class checkboxactivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CheckboxListAdapter
    private val items: MutableList<CheckboxItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkboxactivity)
            items.add(CheckboxItem("first cb",true))
            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = CheckboxListAdapter(items)
            recyclerView.adapter = adapter

            val editText: EditText = findViewById(R.id.edit_text)
            val button: Button = findViewById(R.id.addbutton)

            button.setOnClickListener {
                val text = editText.text.toString()
                if (text.isNotEmpty()) {
                    val item = CheckboxItem(text, false)
                    items.add(item)
                    adapter.notifyItemInserted(items.size - 1)
                    editText.text.clear()
                    recyclerView.scrollToPosition(items.size - 1)
                }
            }
        }
    }