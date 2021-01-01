package com.sonmez.picturesharefirebase.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sonmez.picturesharefirebase.R
import kotlinx.android.synthetic.main.activity_picture_share.*
import java.util.*

class PictureShareActivity : AppCompatActivity() {

    var choosePic : Uri? = null
    var chooseBitmap : Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_share)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun share(view: View) {

        val uuid = UUID.randomUUID()
        val picName = "${uuid}.jpg"
        val reference = storage.reference

        val pictureReference = reference.child("images").child(picName)

        if(choosePic != null) {
            pictureReference.putFile(choosePic!!).addOnSuccessListener { taskSnapshot ->
                val uploadPictureReference = FirebaseStorage.getInstance().reference.child("images").child(picName)
                uploadPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val updatedUserEmail = auth.currentUser!!.email.toString()
                    val userComment = commentText.text.toString()
                    val date = Timestamp.now()

                    val postHasMap = hashMapOf<String, Any>()
                    postHasMap.put("pictureUrl", downloadUrl)
                    postHasMap.put("userEmail", updatedUserEmail)
                    postHasMap.put("userComment", userComment)
                    postHasMap.put("date", date)

                    database.collection("Post").add(postHasMap).addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    fun choosePicture(view: View) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val galeryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeryIntent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1) {
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            choosePic = data.data

            if(choosePic != null) {
                if(Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, choosePic!!)
                    chooseBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(chooseBitmap)
                } else {
                    chooseBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, choosePic)
                    imageView.setImageBitmap(chooseBitmap)
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}