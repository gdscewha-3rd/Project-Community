package com.example.leafy2.cardnews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.leafy2.R

class ThumbnailAdapter(
    private val context: Context,
    private val dataset: List<ThumbnailData>)
    : RecyclerView.Adapter<ThumbnailAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.item_img)
        val title: TextView = view.findViewById(R.id.item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardnews_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.image.setImageResource(item.imageResourceId)
        holder.title.text = item.title
        holder.image.setOnClickListener {
            val action = NewsFragmentDirections.actionNewsFragmentToContentsFragment(index = position)
            holder.view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}