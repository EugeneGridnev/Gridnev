package com.tinkofftest.filmbrowser.filmbrowserapp.api

import com.google.gson.GsonBuilder
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Constants.Companion.BASE_URL
import com.tinkofftest.filmbrowser.filmbrowserapp.util.NullIntDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            val gson = GsonBuilder()
                .registerTypeAdapter(Int::class.java, NullIntDeserializer())
                .create()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(FilmsAPI::class.java)
        }
    }
}