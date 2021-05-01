package com.oamk.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oamk.chatapp.R
import com.oamk.chatapp.adapter.UserAdapter
import com.oamk.chatapp.model.User
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {
    var userlist = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        imgBack.setOnClickListener{
            onBackPressed()
        }

        imgProfile.setOnClickListener{
            val intent = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        getUsersList()
    }

    // method to get users

    fun  getUsersList(){
        val firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()

                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage ==""){
                    imgProfile.setImageResource(R.drawable.profile_image)
                } else {
                    Glide.with(this@UsersActivity).load(currentUser.profileImage).into(imgProfile)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children){
                    val user = dataSnapShot.getValue(User::class.java)

                    if (user!!.userId.equals(firebase.uid)){
                        userlist.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity, userlist)

                userRecyclerView.adapter = userAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}