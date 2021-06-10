package com.dicoding.picodiploma.feedeepaplikasi.Home

import android.content.Context
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONObject

class RestApi constructor(private val context: Context) {
    private val BASE_URL = "http://35.240.249.113/feedeep/"
    private val client = AsyncHttpClient();

    init {
        client.addHeader("17162ea0215fe56d2dd0593fdd983c1793f98836", "0000000000000000")
        client.isLoggingEnabled = true
        client.loggingLevel = Log.ERROR
    }

    fun uploadImage(data: String, request: AsyncHttpResponseHandler) {
        val jsonObject = JSONObject()
        jsonObject.put("base64", data)
        client.post(
            context,
            BASE_URL,
            StringEntity(jsonObject.toString()),
            ContentType.APPLICATION_JSON.mimeType,
            request
        )
    }
}