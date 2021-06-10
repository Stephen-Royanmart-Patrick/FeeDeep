package com.dicoding.picodiploma.feedeepaplikasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.viewpager.widget.ViewPager
import com.dicoding.picodiploma.feedeepaplikasi.Data.FeedeepData
import com.dicoding.picodiploma.feedeepaplikasi.Detail.DetailActivity
import com.dicoding.picodiploma.feedeepaplikasi.Home.MainAccessCamera
import com.dicoding.picodiploma.feedeepaplikasi.UI.About
import com.dicoding.picodiploma.feedeepaplikasi.UI.SlideAdapter
import com.dicoding.picodiploma.feedeepaplikasi.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainModel : DetailActivity
    private val itemdetect = ArrayList<FeedeepData>()
    private lateinit var adapter: FeedeepAdapter
    private lateinit var spinner: Spinner

    private var currentPage = 0
    private var numPages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "FeeDeep"

        val assets =  listOf(
            R.drawable.bennerfeedeep1,
            R.drawable.bennerfeedeep2,
            R.drawable.bennerfeedeep

        )


        val animals = resources.getStringArray(R.array.animal)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, animals)
        binding.AutoCompleteTextView.setAdapter(arrayAdapter)

        //intentkeakseskamera
        binding.picture.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainAccessCamera::class.java))

        }

        createSlider(assets)
    }
    private fun createSlider(string: List<Int>) {

        binding.vpSlider.adapter = SlideAdapter (this, string)
        binding.indicator.setViewPager(binding.vpSlider)
        val density = resources.displayMetrics.density
        //Set circle indicator radius
        binding.indicator.radius = 5 * density
        numPages = string.size
        // Auto getData of viewpager
        val update = Runnable {
            if (currentPage === numPages) {
                currentPage = 0
            }
            binding.vpSlider.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(update)
            }
        }, 5000, 5000)
        // Pager listener over indicator
        binding.indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(pos: Int) {}
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }


    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
            R.id.action_about -> {
                showAbout()
            }
        }
    }

    private fun showAbout() {
        val moveIntent = Intent(this@MainActivity, About::class.java)
        startActivity(moveIntent)
    }
}