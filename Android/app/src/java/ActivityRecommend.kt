package com.dicoding.picodiploma.feedeepaplikasi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dicoding.picodiploma.feedeepaplikasi.Detail.DetailActivity
import com.dicoding.picodiploma.feedeepaplikasi.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.feedeepaplikasi.databinding.ActivityRecommendBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.math.BigDecimal

class ActivityRecommend : AppCompatActivity() {

    companion object {
        private val TAG = ActivityRecommend::class.java.simpleName
    }
    private lateinit var binding : ActivityRecommendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "FeeDeep"

        getRecommend()


    }
        fun getRecommend() {

            val client = AsyncHttpClient()
            client.addHeader("User-Agent", "request")
            client.addHeader("Authorization", "token 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")

            val url ="https://test-gateway-pkghig2.de.gateway.dev/data_kebutuhan_hewan?id_hewan=kebutuhan_sapi"
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
                        val nutrientkalsium = responseObject.getString("kalsium")
                        val nutrientvitaminA = responseObject.getString("vitamin_A")
                        val nutrientkaroten = responseObject.getString("karoten")
                        val nutrientfosfor = responseObject.getString("fosfor")
                        val nutrientprotein = responseObject.getString("protein")
                        binding.txtKalsium.text = nutrientkalsium
                        binding.txtVitaminA.text = nutrientvitaminA
                        binding.txtKaroten.text = nutrientkaroten
                        binding.txtFosfor.text = nutrientfosfor
                        binding.txtProtein.text = nutrientprotein

                        val rekapKebutuhan = mapOf("kalsium" to BigDecimal(nutrientkalsium), "Vitamin A" to BigDecimal(nutrientvitaminA), "Karoten" to BigDecimal(nutrientkaroten), "Fosfor" to BigDecimal(nutrientfosfor), "Protein" to BigDecimal(nutrientprotein))

                        // Custom Stephen - Data intent dari DetailActivity
                        val idBahan = intent.getStringExtra("id")
                        val kalsiumBahan = intent.getStringExtra("kalsium")?.replace(",",".")
                        val vitABahan = intent.getStringExtra("vitamin_A")?.replace(",",".")
                        val karotenBahan = intent.getStringExtra("karoten")?.replace(",",".")
                        val fosforBahan = intent.getStringExtra("fosfor")?.replace(",",".")
                        val proteinBahan = intent.getStringExtra("protein")?.replace(",",".")
                        binding.txtKalsium2.text = kalsiumBahan
                        binding.txtVitaminA2.text = vitABahan
                        binding.txtKaroten2.text = karotenBahan
                        binding.txtFosfor2.text = fosforBahan
                        binding.txtProtein2.text = proteinBahan

                        val rekapBahan = mapOf("kalsium" to BigDecimal(kalsiumBahan), "Vitamin A" to BigDecimal(vitABahan), "Karoten" to BigDecimal(karotenBahan), "Fosfor" to BigDecimal(fosforBahan), "Protein" to BigDecimal(proteinBahan))

                        // Custom Stephen - Perhitungan kebutuhan
                        val kebKalsium = BigDecimal(nutrientkalsium) / BigDecimal(kalsiumBahan) / BigDecimal(10) / BigDecimal(3)
                        val kebVitABahan = BigDecimal(nutrientvitaminA) / BigDecimal(vitABahan) / BigDecimal(10) / BigDecimal(3)
                        val kebKaroten = BigDecimal(nutrientkaroten) / BigDecimal(karotenBahan) / BigDecimal(10) / BigDecimal(3)
                        val kebFosfor = BigDecimal(nutrientfosfor) / BigDecimal(fosforBahan) / BigDecimal(10) / BigDecimal(3)
                        val kebProtein = BigDecimal(nutrientprotein) / BigDecimal(proteinBahan) / BigDecimal(10) / BigDecimal(3)

                        // Custom Stephen - Perekapan, perhitungan maksimal, dan pembuatan kesimpulan
                        val rekapHasil = mapOf("kalsium" to kebKalsium, "Vitamin A" to kebVitABahan, "Karoten" to kebKaroten, "Fosfor" to kebFosfor, "Protein" to kebProtein)
                        val max = rekapHasil.maxByOrNull { it.value }

                        val kesimpulan = "Dengan " + idBahan + ", kamu setidaknya membutuhkan ±" + max?.value + " kilogram untuk sekali pemberian pakan (rekomendasi 3x sehari).\n\n" +
                                "Hal tersebut dikarenakan pada 100 gram " + idBahan + ", hanya mengandung " + rekapBahan[max?.key] +
                                " gram " + max?.key + ". Sedangkan kebutuhan hewanmu, " + rekapKebutuhan[max?.key] + " gram " + max?.key + " perhari"

                        binding.txtDeskripsiKesimpulan.text = kesimpulan
//                        println(kesimpulan)
                    }
                    catch (e: Exception) {
                        Toast.makeText(this@ActivityRecommend, e.message, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@ActivityRecommend, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })



    }
}