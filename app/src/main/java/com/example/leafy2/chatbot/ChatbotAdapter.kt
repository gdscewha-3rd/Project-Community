package com.example.leafy2.chatbot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.leafy2.R

class ChatbotAdapter(
    context: Context,
    private val chatModelArrayList: List<ChatModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class UserViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        val userTV: TextView = view.findViewById(R.id.user_tv)
    }
    class BotViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        val botTV: TextView = view.findViewById(R.id.bot_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when(viewType){
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.user_msg_item, parent, false)
                return UserViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.bot_msg_item, parent, false)
                return BotViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatModelArrayList[position]
        when(item.sender){
            "user" -> {
                (holder as UserViewHolder).userTV.setText(item.message)
            }
            "bot" -> {
                (holder as BotViewHolder).botTV.setText(item.message)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatModelArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = chatModelArrayList[position]
        return when (item.sender){
            "user" -> 0
            "bot" -> 1
            else -> -1
        }
    }
}