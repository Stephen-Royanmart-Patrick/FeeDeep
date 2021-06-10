package com.dicoding.picodiploma.feedeepaplikasi.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.dicoding.picodiploma.feedeepaplikasi.R

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.title = "Feedeep"
        val tvAboutName: TextView = findViewById(R.id.about_name)
        val tvAboutEmail: TextView = findViewById(R.id.about)
        val tvAboutPhoto: de.hdodenhof.circleimageview.CircleImageView = findViewById(R.id.about_photo)

    }
}