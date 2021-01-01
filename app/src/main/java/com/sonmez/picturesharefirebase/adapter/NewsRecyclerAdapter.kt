package com.sonmez.picturesharefirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sonmez.picturesharefirebase.R
import com.sonmez.picturesharefirebase.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class NewsRecyclerAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<NewsRecyclerAdapter.PostHolder>() {

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.recycler_row_user_email.text = postList[position].userEmail
        holder.itemView.recycler_row_comment_text.text = postList[position].userComment
        Picasso.get().load(postList[position].pictureUrl).into(holder.itemView.recycler_row_imageView)
    }
}