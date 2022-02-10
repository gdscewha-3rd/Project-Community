package com.example.leafy2.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.leafy2.R

class ChatbotAdapter(
    private val chatModelArrayList: List<ChatModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userTV: TextView = view.findViewById(R.id.user_tv)
    }

    class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val botTV: TextView = view.findViewById(R.id.bot_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_msg_item, parent, false)
                UserViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.bot_msg_item, parent, false)
                BotViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatModelArrayList[position]
        when (item.sender) {
            "user" -> {
                (holder as UserViewHolder).userTV.text = item.message
            }
            "bot" -> {
                (holder as BotViewHolder).botTV.text = item.message
            }
        }
    }

    override fun getItemCount(): Int {
        return chatModelArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = chatModelArrayList[position]
        return when (item.sender) {
            "user" -> 0
            "bot" -> 1
            else -> -1
        }
    }
}