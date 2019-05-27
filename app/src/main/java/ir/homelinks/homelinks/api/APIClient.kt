package ir.homelinks.homelinks.api

import ir.homelinks.homelinks.utility.ClientConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.Gson




class APIClient {

    companion object {
        private var retrofit: Retrofit? = null


        fun getClient(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(ClientConstants.HOMELINKS_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }
    }
}