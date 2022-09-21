package com.example.emojistatus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emojistatus.databinding.ActivityMainBinding
import com.example.emojistatus.databinding.ItemEmojiBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class UserViewHolder(val binding: ItemEmojiBinding): RecyclerView.ViewHolder(binding.root)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    private companion object{
        private const val TAG ="MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        val query: CollectionReference = db.collection("users")
        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(query,User::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object: FirestoreRecyclerAdapter<User, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEmojiBinding.inflate(layoutInflater,parent,false)
                return UserViewHolder(binding)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                val tvName: TextView = holder.binding.tvName
                val tvEmoji: TextView = holder.binding.tvEmoji

                tvName.text = model.displayName
                tvEmoji.text = model.emojis
            }
        }
        binding.rvusers.adapter = adapter
        binding.rvusers.layoutManager = LinearLayoutManager(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mLogout){
            Log.i(TAG,"Logout")
            auth.signOut()
            val logoutIntent = Intent(this,SignInActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}


