package com.dicoding.picodiploma.feedeepaplikasi.Detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dicoding.picodiploma.feedeepaplikasi.ActivityRecommend
import com.dicoding.picodiploma.feedeepaplikasi.Data.FeedeepData
import com.dicoding.picodiploma.feedeepaplikasi.Home.base64ToBitmap
import com.dicoding.picodiploma.feedeepaplikasi.R
import com.dicoding.picodiploma.feedeepaplikasi.databinding.ActivityDetailBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailActivity : AppCompatActivity(){
    companion object {
        private val TAG = DetailActivity::class.java.simpleName
        const val EXTRA_DATA = "extra_detail_fav"
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var imageview: ImageView
    private lateinit var textView: TextView
    private val listdata = ArrayList<FeedeepData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "FeeDeep"

        textView = findViewById(R.id.txt_detection_object)
        imageview = findViewById(R.id.imageView)
        val nama = intent.getStringExtra("nama")
        val image = intent.getStringExtra("image") as String
        textView.text = nama

        imageview.setImageBitmap(base64ToBitmap(image))

        println(nama)
        when (nama) {
            "Tomato" -> {intent = getListDetail(this, "tomat")}
            "Cabbage" -> {intent = getListDetail(this, "kubis")}
            "Carrot" -> {intent = getListDetail(this, "wortel")}
            "Pisang" -> {intent = getListDetail(this, "pisang")}
        }

        binding.recommend.setOnClickListener {
            startActivity(intent)
        }


    }

    fun getListDetail(context: Context, id:String): Intent {

        val intent = Intent(this@DetailActivity, ActivityRecommend::class.java)
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token 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")

        val url ="https://test-gateway-pkghig2.de.gateway.dev/data_pakan?pakan_id=$id"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                    statusCode: Int,

                    headers: Array<Header>,
                    responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)
                val d = Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val buah = responseObject.getString("nama").toString()
                    val afficacy = responseObject.getString("khasiat").toString()
                    val serving = responseObject.getString("cara_penyajian").toString()
                        val nutrientkalsium = responseObject.getString("kalsium")
                        val nutrientvitamin_A = responseObject.getString("vitamin_A")
                        val nutrientkaroten = responseObject.getString("karoten")
                        val nutrientfosfor = responseObject.getString("fosfor")
                        val nutrientprotein = responseObject.getString("protein")
                        binding.txtDetectionObject.text = buah
                        binding.txtDescriptionManfaat.text = afficacy
                        binding.txtDescriptionPenyajian.text = serving
                        binding.txtKalsium.text = nutrientkalsium
                        binding.txtVitaminA.text = nutrientvitamin_A
                        binding.txtKaroten.text = nutrientkaroten
                        binding.txtFosfor.text = nutrientfosfor
                        binding.txtProtein.text = nutrientprotein

                    intent.putExtra("id", id)
                    intent.putExtra("kalsium", nutrientkalsium)
                    intent.putExtra("vitamin_A",nutrientvitamin_A)
                    intent.putExtra("karoten",nutrientkaroten)
                    intent.putExtra("fosfor",nutrientfosfor)
                    intent.putExtra("protein",nutrientprotein)
                    }
                catch (e: Exception) {
                    Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseBody: ByteArray,
                    error: Throwable
            ) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
        return intent
    }


}