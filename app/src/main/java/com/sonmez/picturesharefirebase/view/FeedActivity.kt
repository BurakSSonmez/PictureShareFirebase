package com.sonmez.picturesharefirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sonmez.picturesharefirebase.model.Post
import com.sonmez.picturesharefirebase.R
import com.sonmez.picturesharefirebase.adapter.NewsRecyclerAdapter
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var recyclerViewAdapter : NewsRecyclerAdapter

    var postLists = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getData()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = NewsRecyclerAdapter(postLists)
        recyclerView.adapter = recyclerViewAdapter


    }

    fun getData() {
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if(value != null) {
                    if(!value.isEmpty) {
                        val documents = value.documents

                        postLists.clear()

                        for(document in documents) {
                            val userEmail = document.get("userEmail") as String
                            val userComment = document.get("userComment") as String
                            val pictureUrl = document.get("pictureUrl") as String

                            val downloadPost =
                                Post(
                                    userEmail,
                                    userComment,
                                    pictureUrl
                                )
                            postLists.add(downloadPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.share_pictures) {
            val intent = Intent(this, PictureShareActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.exit) {
            auth.signOut()
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}